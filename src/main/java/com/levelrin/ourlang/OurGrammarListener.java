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
import java.util.Map;
import java.util.Objects;
import org.antlr.v4.runtime.tree.TerminalNode;

public final class OurGrammarListener extends OurGrammarBaseListener {

    /**
     * Path to the file that it's listening to.
     */
    private final Path path;

    private final Output output;

    private String currentCallerType;

    private String currentVariableName;

    /**
     * Only available for methods.
     */
    private String returnType;

    private final StringBuilder localOutput = new StringBuilder();

    private final Map<String, String> localVariableTypeMap = new HashMap<>();

    public OurGrammarListener(final Path path, final Output output) {
        this.path = path;
        this.output = output;
    }

    @Override
    public void enterMetadataPair(final OurGrammarParser.MetadataPairContext context) {
        final OurGrammarParser.MetadataPairKeyContext keyContext = context.metadataPairKey();
        final OurGrammarParser.MetadataPairValueContext valueContext = context.metadataPairValue();
        if ("test-description".equals(keyContext.NAME().getText())) {
            this.output.appendToTestLogic("test(" + valueContext.STRING().getText() + ", () => {");
        } else if ("return-type".equals(keyContext.NAME().getText())) {
            this.returnType = valueContext.NAME().getText();
        }
    }

    @Override
    public void exitLogicSection(final OurGrammarParser.LogicSectionContext context) {
        if (this.path.endsWith("main.ours")) {
            this.output.appendToMainLogic(this.localOutput.toString());
        } else if (this.isTest(this.path)) {
            this.output.appendToTestLogic(this.localOutput + "});");
        } else {
            this.output.appendToMethodDefinitions(this.localOutput.toString());
        }
    }

    @Override
    public void exitStatement(final OurGrammarParser.StatementContext context) {
        this.localOutput.append(';');
    }

    @Override
    public void enterVariableDeclaration(final OurGrammarParser.VariableDeclarationContext context) {
        this.localOutput
            .append(this.jsVariableName(context.NAME()))
            .append(" = ");
        final OurGrammarParser.ValueContext valueContext = context.value();
        if (valueContext.STRING() != null) {
            this.localVariableTypeMap.put(context.NAME().getText(), "string");
        } else {
            this.currentVariableName = context.NAME().getText();
        }
    }

    @Override
    public void exitVariableDeclaration(final OurGrammarParser.VariableDeclarationContext context) {
        this.currentVariableName = null;
    }

    @Override
    public void enterValue(final OurGrammarParser.ValueContext context) {
        if (context.STRING() != null) {
            this.localOutput.append(context.STRING().getText());
        } else if (context.NUMBER() != null) {
            this.localOutput.append(context.NUMBER().getText());
        } else if (context.NAME() != null) {
            this.localOutput.append(this.jsVariableName(context.NAME()));
        }
    }

    @Override
    public void enterArithmeticOperation(final OurGrammarParser.ArithmeticOperationContext context) {
        if (context.OPEN_PARENTHESIS() != null) {
            this.localOutput.append('(');
        } else if (context.NUMBER() != null) {
            this.localOutput.append(context.NUMBER().getText());
        } else if (context.NAME() != null) {
            this.localOutput.append(this.jsVariableName(context.NAME()));
        } else if (context.MINUS() != null) {
            this.localOutput.append('-');
        }
    }

    @Override
    public void exitArithmeticOperation(final OurGrammarParser.ArithmeticOperationContext context) {
        if (context.CLOSE_PARENTHESIS() != null) {
            this.localOutput.append(')');
        }
    }

    @Override
    public void enterArithmeticOperator(final OurGrammarParser.ArithmeticOperatorContext context) {
        if (context.PLUS() != null) {
            this.localOutput.append('+');
        } else if (context.MINUS() != null) {
            this.localOutput.append('-');
        } else if (context.STAR() != null) {
            this.localOutput.append('*');
        } else if (context.DOUBLE_STAR() != null) {
            this.localOutput.append("**");
        } else if (context.SLASH() != null) {
            this.localOutput.append('/');
        } else if (context.PERCENTAGE() != null) {
            this.localOutput.append('%');
        }
    }

    @Override
    public void enterPrimaryCaller(final OurGrammarParser.PrimaryCallerContext context) {
        if (context.string() != null) {
            this.currentCallerType = "string";
            this.localOutput.append(context.string().getText());
        } else if (context.NAME() != null) {
            this.identifyCurrentCallerTypeFromName(context.NAME());
            this.localOutput.append(this.jsVariableName(context.NAME()));
        }
    }

    @Override
    public void enterPostfixExpression(final OurGrammarParser.PostfixExpressionContext context) {
        final String methodName = context.NAME().getText();
        this.loadMethod(this.currentCallerType, methodName);
        this.localOutput.append('.').append(this.kebabToSnakeCase(context.NAME())).append('(');
    }

    @Override
    public void exitPostfixExpression(final OurGrammarParser.PostfixExpressionContext context) {
        this.localOutput.append(')');
    }

    @Override
    public void enterParameterSeparator(final OurGrammarParser.ParameterSeparatorContext context) {
        this.localOutput.append(',');
    }

    @Override
    public void enterNativeLogicContent(final OurGrammarParser.NativeLogicContentContext context) {
        if (context.STRING() != null) {
            this.output.appendToMethodDefinitions(this.stringContent(context.STRING()));
        } else if (context.COMPLEX_STRING() != null) {
            this.output.appendToMethodDefinitions(this.complexStringContent(context.COMPLEX_STRING()));
        }
    }

    @Override
    public void exitDefaultObjectContent(final OurGrammarParser.DefaultObjectContentContext context) {
        this.output.appendToDefaultObjects(";");
    }

    @Override
    public void enterDefaultObjectConstructorCall(final OurGrammarParser.DefaultObjectConstructorCallContext context) {
        this.output.appendToDefaultObjects("let " + this.jsVariableName(this.path) + " = new " + this.jsClassName(this.path) + "(");
    }

    @Override
    public void exitDefaultObjectConstructorCall(final OurGrammarParser.DefaultObjectConstructorCallContext context) {
        this.output.appendToDefaultObjects(")");
    }

    private String fileNameWithoutExtension(final Path path) {
        return path.getFileName().toString().substring(0, path.getFileName().toString().lastIndexOf(".ours"));
    }

    private void identifyCurrentCallerTypeFromName(final TerminalNode terminal) {
        final String callerName = terminal.getText();
        if (this.localVariableTypeMap.containsKey(callerName)) {
            this.currentCallerType = this.localVariableTypeMap.get(callerName);
        } else if (this.typeExistsInSdk(callerName)) {
            this.currentCallerType = callerName;
            this.loadSdkClass(callerName);
        } else {
            // todo: Check if the type exists in the project directory.
            throw new IllegalStateException("Could not find the type of the callerName: " + callerName);
        }
    }

    private void loadSdkClass(final String type) {
        try {
            final Path classPath = Paths.get(
                Objects.requireNonNull(
                    this.getClass().getClassLoader().getResource(
                        this.sdkTypeResourceName(type)
                    )
                ).toURI()
            );
            if (!this.output.hasClassLoaded(classPath)) {
                this.output.appendToClassDefinitions("class " + this.jsClassName(classPath) + " {}\n");
                final String classContent = Files.readString(classPath, StandardCharsets.UTF_8);
                final OurGrammarListener classListener = new OurGrammarListener(classPath, this.output);
                new OurGrammarWalker(classContent, classListener).walk();
                this.output.markLoadedClass(classPath);
            }
        } catch (final URISyntaxException ex) {
            throw new IllegalStateException("Failed to get the path of the SDK class.", ex);
        } catch (final IOException ex) {
            throw new IllegalStateException("Failed to read the SDK class content.", ex);
        }
    }

    private boolean typeExistsInSdk(final String callerName) {
        return this.getClass().getClassLoader().getResource(this.sdkTypeResourceName(callerName)) != null;
    }

    private String jsVariableName(final TerminalNode terminal) {
        return "_our_" + this.kebabToSnakeCase(terminal);
    }

    private String jsVariableName(final Path path) {
        return "_our_" + this.fileNameWithoutExtension(path).replaceAll("-", "_");
    }

    private String jsClassName(final Path path) {
        return "__our__" + this.fileNameWithoutExtension(path).replaceAll("-", "_");
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
            final Path methodPath = Paths.get(
                Objects.requireNonNull(
                    this.getClass().getClassLoader().getResource(
                        this.sdkMethodResourceName(callerType, methodName)
                    )
                ).toURI()
            );
            if (!this.output.hasMethodLoaded(methodPath)) {
                final String methodContent = Files.readString(methodPath, StandardCharsets.UTF_8);
                final OurGrammarListener methodListener = new OurGrammarListener(methodPath, this.output);
                new OurGrammarWalker(methodContent, methodListener).walk();
                if (this.currentVariableName != null) {
                    this.localVariableTypeMap.put(this.currentVariableName, methodListener.returnType);
                }
                this.output.markLoadedMethod(methodPath);
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

    private boolean isTest(final Path path) {
        final Path parent = path.getParent();
        return parent != null && !"our-lang/sdk/test".equals(parent.toString()) && "test".equals(parent.getFileName().toString());
    }

}
