package com.levelrin.format;

import com.levelrin.antlr.generated.OurGrammarBaseVisitor;
import com.levelrin.antlr.generated.OurGrammarParser;
import java.util.List;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the class that has the formatting logic.
 */
// Excluding the following PMD rules via `ruleSet.xml` didn't work, for some reason.
// Except PMD.UnusedAssignment was added due to a false-positive.
@SuppressWarnings({"PMD.TooManyMethods", "PMD.LinguisticNaming", "PMD.UnusedAssignment"})
public final class FormatVisitor extends OurGrammarBaseVisitor<String> {

    /**
     * For logging.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(FormatVisitor.class);

    /**
     * Number of spaces for an indentation.
     */
    private static final String INDENT_UNIT = "  ";

    /**
     * As is.
     */
    private int currentIndentLevel;

    @Override
    public String visitFile(final OurGrammarParser.FileContext context) {
        final OurGrammarParser.MetadataContext metadataContext = context.metadata();
        final List<OurGrammarParser.OptionalSectionContext> optionalSectionContexts = context.optionalSection();
        final StringBuilder text = new StringBuilder();
        text.append(this.visit(metadataContext));
        for (final OurGrammarParser.OptionalSectionContext optionalSectionContext : optionalSectionContexts) {
            this.appendNewLinesAndIndent(text, 2);
            text.append(this.visit(optionalSectionContext));
        }
        this.appendNewLinesAndIndent(text, 1);
        return text.toString();
    }

    @Override
    public String visitMetadata(final OurGrammarParser.MetadataContext context) {
        final OurGrammarParser.MetadataHeaderContext metadataHeaderContext = context.metadataHeader();
        final OurGrammarParser.MetadataBodyContext metadataBodyContext = context.metadataBody();
        final StringBuilder text = new StringBuilder();
        text.append(this.visit(metadataHeaderContext));
        this.appendNewLinesAndIndent(text, 2);
        text.append(this.visit(metadataBodyContext));
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
        final OurGrammarParser.ObjectsHeaderContext objectsHeaderContext = context.objectsHeader();
        final OurGrammarParser.ObjectsBodyContext objectsBodyContext = context.objectsBody();
        final StringBuilder text = new StringBuilder();
        text.append(this.visit(objectsHeaderContext));
        this.appendNewLinesAndIndent(text, 2);
        text.append(this.visit(objectsBodyContext));
        return text.toString();
    }

    @Override
    public String visitLogic(final OurGrammarParser.LogicContext context) {
        final OurGrammarParser.LogicHeaderContext logicHeaderContext = context.logicHeader();
        final OurGrammarParser.LogicBodyContext logicBodyContext = context.logicBody();
        final StringBuilder text = new StringBuilder();
        text.append(this.visit(logicHeaderContext));
        this.appendNewLinesAndIndent(text, 2);
        text.append(this.visit(logicBodyContext));
        return text.toString();
    }

    @Override
    public String visitMetadataHeader(final OurGrammarParser.MetadataHeaderContext context) {
        final List<TerminalNode> doubleEqualTerminals = context.DOUBLE_EQUAL();
        final TerminalNode metadataTerminal = context.METADATA();
        final StringBuilder text = new StringBuilder();
        text.append(this.visit(doubleEqualTerminals.get(0)))
            .append(' ')
            .append(this.visit(metadataTerminal))
            .append(' ')
            .append(this.visit(doubleEqualTerminals.get(1)));
        return text.toString();
    }

    @Override
    public String visitMetadataBody(final OurGrammarParser.MetadataBodyContext context) {
        final OurGrammarParser.AboutAttributeContext aboutAttributeContext = context.aboutAttribute();
        final StringBuilder text = new StringBuilder();
        text.append(this.visit(aboutAttributeContext));
        return text.toString();
    }

    @Override
    public String visitAboutAttribute(final OurGrammarParser.AboutAttributeContext context) {
        final TerminalNode aboutTerminal = context.ABOUT();
        final TerminalNode colonTerminal = context.COLON();
        final TerminalNode executableTerminal = context.EXECUTABLE();
        final StringBuilder text = new StringBuilder();
        text.append(this.visit(aboutTerminal))
            .append(this.visit(colonTerminal))
            .append(' ')
            .append(this.visit(executableTerminal));
        return text.toString();
    }

    @Override
    public String visitObjectsHeader(final OurGrammarParser.ObjectsHeaderContext context) {
        final List<TerminalNode> doubleEqualTerminals = context.DOUBLE_EQUAL();
        final TerminalNode objectsTerminal = context.OBJECTS();
        final StringBuilder text = new StringBuilder();
        text.append(this.visit(doubleEqualTerminals.get(0)))
            .append(' ')
            .append(this.visit(objectsTerminal))
            .append(' ')
            .append(this.visit(doubleEqualTerminals.get(1)));
        return text.toString();
    }

    @Override
    public String visitObjectsBody(final OurGrammarParser.ObjectsBodyContext context) {
        final List<OurGrammarParser.ObjectDeclarationContext> objectDeclarationContexts = context.objectDeclaration();
        final StringBuilder text = new StringBuilder();
        for (int index = 0; index < objectDeclarationContexts.size(); index++) {
            final OurGrammarParser.ObjectDeclarationContext objectDeclarationContext = objectDeclarationContexts.get(index);
            text.append(this.visit(objectDeclarationContext));
            if (index < objectDeclarationContexts.size() - 1) {
                this.appendNewLinesAndIndent(text, 1);
            }
        }
        return text.toString();
    }

    @Override
    public String visitObjectDeclaration(final OurGrammarParser.ObjectDeclarationContext context) {
        final OurGrammarParser.ObjectFromSdkContext objectFromSdkContext = context.objectFromSdk();
        final StringBuilder text = new StringBuilder();
        if (objectFromSdkContext != null) {
            text.append(this.visit(objectFromSdkContext));
        }
        return text.toString();
    }

    @Override
    public String visitObjectFromSdk(final OurGrammarParser.ObjectFromSdkContext context) {
        final TerminalNode nameTerminal = context.NAME();
        final TerminalNode fromTerminal = context.FROM();
        final TerminalNode sdkTerminal = context.SDK();
        final StringBuilder text = new StringBuilder();
        text.append(this.visit(nameTerminal))
            .append(' ')
            .append(this.visit(fromTerminal))
            .append(' ')
            .append(this.visit(sdkTerminal));
        return text.toString();
    }

    @Override
    public String visitLogicHeader(final OurGrammarParser.LogicHeaderContext context) {
        final List<TerminalNode> doubleEqualTerminals = context.DOUBLE_EQUAL();
        final TerminalNode logicTerminal = context.LOGIC();
        final StringBuilder text = new StringBuilder();
        text.append(this.visit(doubleEqualTerminals.get(0)))
            .append(' ')
            .append(this.visit(logicTerminal))
            .append(' ')
            .append(this.visit(doubleEqualTerminals.get(1)));
        return text.toString();
    }

    @Override
    public String visitLogicBody(final OurGrammarParser.LogicBodyContext context) {
        final List<OurGrammarParser.StatementContext> statementContexts = context.statement();
        final StringBuilder text = new StringBuilder();
        for (int index = 0; index < statementContexts.size(); index++) {
            final OurGrammarParser.StatementContext statementContext = statementContexts.get(index);
            text.append(this.visit(statementContext));
            if (index < statementContexts.size() - 1) {
                this.appendNewLinesAndIndent(text, 1);
            }
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
        final TerminalNode commaTerminal = context.COMMA();
        final OurGrammarParser.MethodNameContext methodNameContext = context.methodName();
        final OurGrammarParser.ArgumentsContext argumentsContext = context.arguments();
        final TerminalNode dotTerminal = context.DOT();
        final StringBuilder text = new StringBuilder();
        text.append(this.visit(variableNameContext))
            .append(this.visit(commaTerminal))
            .append(' ')
            .append(this.visit(methodNameContext));
        if (argumentsContext != null) {
            text.append(' ')
                .append(this.visit(argumentsContext));
        }
        text.append(this.visit(dotTerminal));
        return text.toString();
    }

    @Override
    public String visitVariableName(final OurGrammarParser.VariableNameContext context) {
        final TerminalNode nameTerminal = context.NAME();
        final StringBuilder text = new StringBuilder();
        text.append(this.visit(nameTerminal));
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
        for (int index = 0; index < argumentContexts.size(); index++) {
            final OurGrammarParser.ArgumentContext argumentContext = argumentContexts.get(index);
            text.append(this.visit(argumentContext));
            if (index < argumentContexts.size() - 1) {
                this.appendNewLinesAndIndent(text, 1);
            }
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
        return node.getText();
    }

    /**
     * We use this to add new lines with appropriate indentations.
     *
     * @param text We will append the new lines and indentations into this.
     * @param newLines Number of new lines before appending indentations.
     */
    private void appendNewLinesAndIndent(final StringBuilder text, final int newLines) {
        text.append("\n".repeat(newLines))
            .append(INDENT_UNIT.repeat(this.currentIndentLevel));
    }

}
