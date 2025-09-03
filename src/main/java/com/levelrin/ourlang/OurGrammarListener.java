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

    private String currentCallerType;

    /**
     * Since we need to load the method definition first, we will store the method call here.
     * And then, we will append the method call to the {@link #js}.
     */
    private StringBuilder jsMethodCall = new StringBuilder();

    public OurGrammarListener(final StringBuilder js, final List<Path> loadedMethods) {
        this.js = js;
        this.loadedMethods = loadedMethods;
    }

    @Override
    public void exitStatement(final OurGrammarParser.StatementContext context) {
        this.js.append(';');
    }

    @Override
    public void exitMethodCall(final OurGrammarParser.MethodCallContext context) {
        this.js.append(this.jsMethodCall);
        this.jsMethodCall = new StringBuilder();
    }

    @Override
    public void enterPrimaryCaller(final OurGrammarParser.PrimaryCallerContext context) {
        if (context.string() != null) {
            this.currentCallerType = "string";
            this.jsMethodCall.append(context.string().getText());
        } else if (context.NAME() != null) {
            this.identifyCurrentCallerTypeFromName(context.NAME());
            this.jsMethodCall.append(this.kebabToSnakeCase(context.NAME()));
        }
    }

    @Override
    public void enterPostfixExpression(final OurGrammarParser.PostfixExpressionContext context) {
        final String methodName = context.NAME().getText();
        this.loadMethod(this.currentCallerType, methodName);
        this.jsMethodCall.append('.').append(this.kebabToSnakeCase(context.NAME())).append('(');
    }

    @Override
    public void exitPostfixExpression(final OurGrammarParser.PostfixExpressionContext context) {
        this.jsMethodCall.append(')');
    }

    @Override
    public void enterParameter(final OurGrammarParser.ParameterContext context) {
        if (context.string() != null) {
            this.jsMethodCall.append(context.string().getText());
        } else if (context.NAME() != null) {
            this.jsMethodCall.append(this.kebabToSnakeCase(context.NAME()));
        }
    }

    @Override
    public void enterParameterSeparator(final OurGrammarParser.ParameterSeparatorContext context) {
        this.jsMethodCall.append(',');
    }

    @Override
    public void enterNativeLogicContent(final OurGrammarParser.NativeLogicContentContext context) {
        if (context.STRING() != null) {
            this.js.append(this.stringContent(context.STRING()));
        } else if (context.COMPLEX_STRING() != null) {
            this.js.append(this.complexStringContent(context.COMPLEX_STRING()));
        }
    }

    private void identifyCurrentCallerTypeFromName(final TerminalNode terminal) {
        final String callerName = terminal.getText();
        if (this.variableTypeMap.containsKey(callerName)) {
            this.currentCallerType = this.variableTypeMap.get(callerName);
        } else if (this.typeExistsInSdk(callerName)) {
            this.currentCallerType = callerName;
        } else {
            // todo: Check if the type exists in the project directory.
            throw new IllegalStateException("Could not find the type of the callerName: " + callerName);
        }
    }

    private boolean typeExistsInSdk(final String callerName) {
        return this.getClass().getClassLoader().getResource(this.sdkTypeResourceName(callerName)) != null;
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
        // todo: Update this.currentCallerType to the method's return type for method chaining.
        if (this.methodExistsInSdk(callerType, methodName)) {
            this.loadSdkMethod(callerType, methodName);
        } else {
            // todo: Check if the method exists in the project directory.
            throw new IllegalStateException("Could not find the method: " + methodName);
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
        return "our-lang/sdk/" + callerType + '/' + methodName + ".ours";
    }

    private String sdkTypeResourceName(final String callerType) {
        return "our-lang/sdk/" + callerType + '/' + callerType + ".ours";
    }

}
