package com.levelrin.ourlang;

import com.levelrin.antlr.generated.OurGrammarBaseListener;
import com.levelrin.antlr.generated.OurGrammarParser;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.antlr.v4.runtime.tree.TerminalNode;

public final class OurGrammarListener extends OurGrammarBaseListener {

    private final StringBuilder js;

    private final List<Path> loadedMethods;

    private final Map<String, String> variableTypeMap = new HashMap<>();

    private StringBuilder tempMethodCall = new StringBuilder();

    public OurGrammarListener(final StringBuilder js, final List<Path> loadedMethods) {
        this.js = js;
        this.loadedMethods = loadedMethods;
    }

    @Override
    public void enterHeader(final OurGrammarParser.HeaderContext context) {
        if ("native-logic".equals(context.NAME().getText())) {
            String nativeMethodContent = "not initialized yet.";
            if (context.content().string().STRING() != null) {
                nativeMethodContent = this.stringContent(context.content().string().STRING());
            } else if (context.content().string().COMPLEX_STRING() != null) {
                nativeMethodContent = this.complexStringContent(context.content().string().COMPLEX_STRING());
            }
            this.js.append(nativeMethodContent);
        }
    }

    @Override
    public void exitStatement(final OurGrammarParser.StatementContext context) {
        this.js.append(';');
    }

    @Override
    public void enterMethodCall(final OurGrammarParser.MethodCallContext context) {
        final OurGrammarParser.PrimaryValueContext primaryValue = context.primaryValue();
        String callerType;
        if (primaryValue.STRING() != null) {
            callerType = "string";
        } else if (this.variableTypeMap.containsKey(primaryValue.NAME().getText())) {
            callerType = this.variableTypeMap.get(primaryValue.NAME().getText());
        } else {
            callerType = primaryValue.NAME().getText();
        }
        final OurGrammarParser.PostfixExpressionContext firstPostfixExpression = context.postfixExpression(0);
        final String firstMethodName = firstPostfixExpression.NAME().getText();
        this.loadMethod(callerType, firstMethodName);
        if (primaryValue.NAME() != null) {
            this.tempMethodCall.append(this.kebabToSnakeCase(primaryValue.NAME()));
        } else if (primaryValue.STRING() != null) {
            this.tempMethodCall.append(primaryValue.STRING().getText());
        }
        this.tempMethodCall.append('.').append(this.kebabToSnakeCase(firstPostfixExpression.NAME())).append('(');
    }

    @Override
    public void exitMethodCall(final OurGrammarParser.MethodCallContext context) {
        this.js.append(this.tempMethodCall);
        this.tempMethodCall = new StringBuilder();
    }

    @Override
    public void exitPostfixExpression(final OurGrammarParser.PostfixExpressionContext context) {
        this.tempMethodCall.append(')');
    }

    @Override
    public void enterParamPrimaryValue(final OurGrammarParser.ParamPrimaryValueContext context) {
        if (context.STRING() != null) {
            this.tempMethodCall.append(context.STRING().getText());
        } else if (context.NAME() != null) {
            this.tempMethodCall.append(this.kebabToSnakeCase(context.NAME()));
        }
    }

    @Override
    public void enterParamSeparator(final OurGrammarParser.ParamSeparatorContext context) {
        this.tempMethodCall.append(',');
    }

    private String kebabToSnakeCase(final TerminalNode terminal) {
        return terminal.getText().replaceAll("-", "_");
    }

    private String complexStringContent(final TerminalNode terminal) {
        return terminal.getText().substring(
            "-- our-string-start --".length(),
            terminal.getText().length() - "-- our-string-end --".length()
        );
    }

    private String stringContent(final TerminalNode terminal) {
        return terminal.getText().substring(1, terminal.getText().length() - 1);
    }

    private void loadMethod(final String callerType, final String methodName) {
        if (this.methodExistsInSdk(callerType, methodName)) {
            this.loadSdkMethod(callerType, methodName);
        }
    }

    private boolean methodExistsInSdk(final String callerType, final String methodName) {
        return this.getClass().getClassLoader().getResource(this.sdkMethodResourceName(callerType, methodName)) != null;
    }

    private void loadSdkMethod(final String callerType, final String methodName) {
        try {
            final Path path = Paths.get(
                Objects.requireNonNull(
                    this.getClass().getClassLoader().getResource(
                        this.sdkMethodResourceName(callerType, methodName)
                    )
                ).toURI()
            );
            if (!this.loadedMethods.contains(path)) {
                final String methodContent = Files.readString(path, StandardCharsets.UTF_8);
                final OurGrammarListener methodListener = new OurGrammarListener(this.js, this.loadedMethods);
                new OurGrammarWalker(methodContent, methodListener).walk();
                this.loadedMethods.add(path);
            }
        } catch (final URISyntaxException ex) {
            throw new IllegalStateException("Failed to get the path of the SDK method.", ex);
        } catch (final IOException ex) {
            throw new IllegalStateException("Failed to read the SDK method content.", ex);
        }
    }

    private String sdkMethodResourceName(final String callerType, final String methodName) {
        return "our-lang/sdk/" + callerType + "/" + methodName + ".ours";
    }

}
