package com.levelrin.node;

import com.levelrin.antlr.generated.OurGrammarBaseVisitor;
import com.levelrin.antlr.generated.OurGrammarLexer;
import com.levelrin.antlr.generated.OurGrammarParser;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * It's for converting our-lang into Node.js code.
 */
// Excluding the following PMD rules via `ruleSet.xml` didn't work, for some reason.
// Except PMD.UnusedAssignment was added due to a false-positive.
@SuppressWarnings({"PMD.TooManyMethods", "PMD.LinguisticNaming", "PMD.UnusedAssignment"})
public final class NodeVisitor extends OurGrammarBaseVisitor<String> {

    /**
     * For logging.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(NodeVisitor.class);

    @Override
    public String visitFile(final OurGrammarParser.FileContext context) {
        final List<OurGrammarParser.OptionalSectionContext> optionalSectionContexts = context.optionalSection();
        final StringBuilder text = new StringBuilder();
        for (final OurGrammarParser.OptionalSectionContext optionalSectionContext : optionalSectionContexts) {
            text.append(this.visit(optionalSectionContext));
        }
        return text.toString();
    }

    @Override
    public String visitOptionalSection(final OurGrammarParser.OptionalSectionContext context) {
        final OurGrammarParser.ObjectsContext objectsContext = context.objects();
        final OurGrammarParser.LogicContext logicContext = context.logic();
        final StringBuilder text = new StringBuilder();
        if (objectsContext != null) {
            text.append(this.visit(objectsContext));
        } else if (logicContext != null) {
            text.append(this.visit(logicContext));
        }
        return text.toString();
    }

    @Override
    public String visitObjects(final OurGrammarParser.ObjectsContext context) {
        final OurGrammarParser.ObjectsBodyContext objectsBodyContext = context.objectsBody();
        final StringBuilder text = new StringBuilder();
        text.append(this.visit(objectsBodyContext));
        return text.toString();
    }

    @Override
    public String visitObjectsBody(final OurGrammarParser.ObjectsBodyContext context) {
        final List<OurGrammarParser.ObjectDeclarationContext> objectDeclarationContexts = context.objectDeclaration();
        final StringBuilder text = new StringBuilder();
        for (final OurGrammarParser.ObjectDeclarationContext objectDeclarationContext : objectDeclarationContexts) {
            text.append(this.visit(objectDeclarationContext));
        }
        return text.toString();
    }

    @Override
    public String visitObjectDeclaration(final OurGrammarParser.ObjectDeclarationContext context) {
        final OurGrammarParser.ObjectFromSdkContext objectFromSdkContext = context.objectFromSdk();
        final StringBuilder text = new StringBuilder();
        text.append(this.visit(objectFromSdkContext));
        return text.toString();
    }

    @Override
    public String visitObjectFromSdk(final OurGrammarParser.ObjectFromSdkContext context) {
        final TerminalNode nameContext = context.NAME();
        final StringBuilder text = new StringBuilder();
        final String nameText = nameContext.getText();
        final String fileName = String.format("%s.js", nameText);
        try {
            final Path path = Paths.get(
                ClassLoader.getSystemResource(
                    String.format("node/%s", fileName)
                ).toURI()
            );
            final String code = Files.readString(path, StandardCharsets.UTF_8);
            text.append(code);
        } catch (final URISyntaxException | IOException ex) {
            throw new IllegalStateException(
                String.format(
                    "Could not load the object from the SDK. object name: %s",
                    nameText
                ),
                ex
            );
        }
        return text.toString();
    }

    @Override
    public String visitLogic(final OurGrammarParser.LogicContext context) {
        final OurGrammarParser.LogicBodyContext logicBodyContext = context.logicBody();
        final StringBuilder text = new StringBuilder();
        text.append(this.visit(logicBodyContext));
        return text.toString();
    }

    @Override
    public String visitLogicBody(final OurGrammarParser.LogicBodyContext context) {
        final List<OurGrammarParser.StatementContext> statementContexts = context.statement();
        final StringBuilder text = new StringBuilder();
        for (final OurGrammarParser.StatementContext statementContext : statementContexts) {
            text.append(this.visit(statementContext));
        }
        return text.toString();
    }

    @Override
    public String visitStatement(final OurGrammarParser.StatementContext context) {
        final OurGrammarParser.VoidMethodCallContext voidMethodCallContext = context.voidMethodCall();
        final StringBuilder text = new StringBuilder();
        text.append(this.visit(voidMethodCallContext));
        return text.toString();
    }

    @Override
    public String visitVoidMethodCall(final OurGrammarParser.VoidMethodCallContext context) {
        final OurGrammarParser.VariableNameContext variableNameContext = context.variableName();
        final OurGrammarParser.MethodNameContext methodNameContext = context.methodName();
        final OurGrammarParser.ArgumentsContext argumentsContext = context.arguments();
        final StringBuilder text = new StringBuilder();
        text.append(this.visit(variableNameContext))
            .append('.')
            .append(this.visit(methodNameContext));
        if (argumentsContext == null) {
            text.append("()");
        } else {
            text.append('(')
                .append(this.visit(argumentsContext))
                .append(')');
        }
        text.append(';');
        return text.toString();
    }

    @Override
    public String visitVariableName(final OurGrammarParser.VariableNameContext context) {
        final TerminalNode nameTerminal = context.NAME();
        final StringBuilder text = new StringBuilder();
        text.append("_our_")
            .append(this.visit(nameTerminal));
        return text.toString();
    }

    @Override
    public String visitMethodName(final OurGrammarParser.MethodNameContext context) {
        final TerminalNode nameTerminal = context.NAME();
        final StringBuilder text = new StringBuilder();
        text.append(this.visit(nameTerminal));
        return text.toString();
    }

    @Override
    public String visitArguments(final OurGrammarParser.ArgumentsContext context) {
        final List<OurGrammarParser.ArgumentContext> argumentContexts = context.argument();
        final StringBuilder text = new StringBuilder();
        for (final OurGrammarParser.ArgumentContext argumentContext : argumentContexts) {
            text.append(this.visit(argumentContext));
        }
        return text.toString();
    }

    @Override
    public String visitArgument(final OurGrammarParser.ArgumentContext context) {
        final TerminalNode stringLiteralTerminal = context.STRING_LITERAL();
        final StringBuilder text = new StringBuilder();
        text.append(this.visit(stringLiteralTerminal));
        return text.toString();
    }

    @Override
    public String visit(final ParseTree tree) {
        final String ruleName = tree.getClass().getSimpleName();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Enter `{}` text: {}", ruleName, tree.getText());
        }
        return tree.accept(this);
    }

    @Override
    public String visitTerminal(final TerminalNode node) {
        final StringBuilder text = new StringBuilder();
        final int nodeType = node.getSymbol().getType();
        final String nodeText = node.getText();
        if (nodeType == OurGrammarLexer.STRING_LITERAL) {
            // Remove the surrounding backquotes of the string literal.
            final String literalContent = nodeText.substring(1, nodeText.length() - 1);
            // Surround the string content with double quotes.
            text.append(String.format("\"%s\"", literalContent));
        } else {
            text.append(nodeText);
        }
        return text.toString();
    }

}
