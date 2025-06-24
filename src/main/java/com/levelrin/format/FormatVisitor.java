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
        final TerminalNode metadataHeaderTerminal = context.METADATA_HEADER();
        final OurGrammarParser.MetadataBodyContext metadataBodyContext = context.metadataBody();
        final StringBuilder text = new StringBuilder();
        text.append(this.visit(metadataHeaderTerminal));
        this.appendNewLinesAndIndent(text, 2);
        text.append(this.visit(metadataBodyContext));
        return text.toString();
    }

    @Override
    public String visitOptionalSection(final OurGrammarParser.OptionalSectionContext context) {
        final OurGrammarParser.ObjectsContext objectsContext = context.objects();
        final OurGrammarParser.LogicContext logicContext = context.logic();
        final OurGrammarParser.ParametersContext parametersContext = context.parameters();
        final OurGrammarParser.SupportedTargetsContext supportedTargetsContext = context.supportedTargets();
        final StringBuilder text = new StringBuilder();
        if (objectsContext != null) {
            text.append(this.visit(objectsContext));
        } else if (logicContext != null) {
            text.append(this.visit(logicContext));
        } else if (parametersContext != null) {
            text.append(this.visit(parametersContext));
        } else if (supportedTargetsContext != null) {
            text.append(this.visit(supportedTargetsContext));
        }
        return text.toString();
    }

    @Override
    public String visitObjects(final OurGrammarParser.ObjectsContext context) {
        final TerminalNode objectsHeaderTerminal = context.OBJECTS_HEADER();
        final OurGrammarParser.ObjectsBodyContext objectsBodyContext = context.objectsBody();
        final StringBuilder text = new StringBuilder();
        text.append(this.visit(objectsHeaderTerminal));
        this.appendNewLinesAndIndent(text, 2);
        text.append(this.visit(objectsBodyContext));
        return text.toString();
    }

    @Override
    public String visitLogic(final OurGrammarParser.LogicContext context) {
        final TerminalNode logicHeaderTerminal = context.LOGIC_HEADER();
        final OurGrammarParser.LogicBodyContext logicBodyContext = context.logicBody();
        final StringBuilder text = new StringBuilder();
        text.append(this.visit(logicHeaderTerminal));
        this.appendNewLinesAndIndent(text, 2);
        text.append(this.visit(logicBodyContext));
        return text.toString();
    }

    @Override
    public String visitParameters(final OurGrammarParser.ParametersContext context) {
        final TerminalNode parametersHeaderTerminal = context.PARAMETERS_HEADER();
        final OurGrammarParser.ParametersBodyContext parametersBodyContext = context.parametersBody();
        final StringBuilder text = new StringBuilder();
        text.append(this.visit(parametersHeaderTerminal));
        this.appendNewLinesAndIndent(text, 2);
        text.append(this.visit(parametersBodyContext));
        return text.toString();
    }

    @Override
    public String visitSupportedTargets(final OurGrammarParser.SupportedTargetsContext context) {
        final TerminalNode supportedTargetsTerminal = context.SUPPORTED_TARGETS();
        final OurGrammarParser.SupportedTargetsBodyContext supportedTargetsBodyContext = context.supportedTargetsBody();
        final StringBuilder text = new StringBuilder();
        text.append(this.visit(supportedTargetsTerminal));
        this.appendNewLinesAndIndent(text, 2);
        text.append(this.visit(supportedTargetsBodyContext));
        return text.toString();
    }

    @Override
    public String visitMetadataBody(final OurGrammarParser.MetadataBodyContext context) {
        final OurGrammarParser.PairsContext pairsContext = context.pairs();
        final StringBuilder text = new StringBuilder();
        text.append(this.visit(pairsContext));
        return text.toString();
    }

    @Override
    public String visitParametersBody(final OurGrammarParser.ParametersBodyContext context) {
        final OurGrammarParser.PairsContext pairsContext = context.pairs();
        final StringBuilder text = new StringBuilder();
        text.append(this.visit(pairsContext));
        return text.toString();
    }

    @Override
    public String visitSupportedTargetsBody(final OurGrammarParser.SupportedTargetsBodyContext context) {
        final OurGrammarParser.PairContext pairContext = context.pair();
        final StringBuilder text = new StringBuilder();
        text.append(this.visit(pairContext));
        return text.toString();
    }

    @Override
    public String visitPairs(final OurGrammarParser.PairsContext context) {
        final List<OurGrammarParser.PairContext> pairContexts = context.pair();
        final List<TerminalNode> commaTerminals = context.COMMA();
        final StringBuilder text = new StringBuilder();
        final OurGrammarParser.PairContext firstPairContext = pairContexts.get(0);
        text.append(this.visit(firstPairContext));
        for (int index = 0; index < commaTerminals.size(); index++) {
            final TerminalNode commaTerminal = commaTerminals.get(index);
            final OurGrammarParser.PairContext pairContext = pairContexts.get(index + 1);
            text.append(this.visit(commaTerminal));
            this.appendNewLinesAndIndent(text, 1);
            text.append(this.visit(pairContext));
        }
        return text.toString();
    }

    @Override
    public String visitPair(final OurGrammarParser.PairContext context) {
        final TerminalNode nameTerminal = context.NAME();
        final TerminalNode colonTerminal = context.COLON();
        final OurGrammarParser.ValueContext valueContext = context.value();
        final StringBuilder text = new StringBuilder();
        text.append(this.visit(nameTerminal))
            .append(this.visit(colonTerminal))
            .append(' ')
            .append(this.visit(valueContext));
        return text.toString();
    }

    @Override
    public String visitValue(final OurGrammarParser.ValueContext context) {
        final TerminalNode nameTerminal = context.NAME();
        final TerminalNode stringLiteralTerminal = context.STRING_LITERAL();
        final OurGrammarParser.ListContext listContext = context.list();
        final OurGrammarParser.PairContext pairContext = context.pair();
        final StringBuilder text = new StringBuilder();
        if (nameTerminal != null) {
            text.append(this.visit(nameTerminal));
        } else if (stringLiteralTerminal != null) {
            text.append(this.visit(stringLiteralTerminal));
        } else if (listContext != null) {
            text.append(this.visit(listContext));
        } else if (pairContext != null) {
            text.append(this.visit(pairContext));
        }
        return text.toString();
    }

    @Override
    public String visitList(final OurGrammarParser.ListContext context) {
        final TerminalNode openBracketTerminal = context.OPEN_BRACKET();
        final List<OurGrammarParser.ValueContext> valueContexts = context.value();
        final List<TerminalNode> commaTerminals = context.COMMA();
        final TerminalNode closeBracketTerminal = context.CLOSE_BRACKET();
        final StringBuilder text = new StringBuilder();
        text.append(this.visit(openBracketTerminal));
        this.currentIndentLevel++;
        this.appendNewLinesAndIndent(text, 1);
        final OurGrammarParser.ValueContext firstValueContext = valueContexts.get(0);
        text.append(this.visit(firstValueContext));
        for (int index = 0; index < commaTerminals.size(); index++) {
            final TerminalNode commaTerminal = commaTerminals.get(index);
            final OurGrammarParser.ValueContext valueContext = valueContexts.get(index + 1);
            text.append(this.visit(commaTerminal));
            this.appendNewLinesAndIndent(text, 1);
            text.append(this.visit(valueContext));
        }
        this.currentIndentLevel--;
        this.appendNewLinesAndIndent(text, 1);
        text.append(this.visit(closeBracketTerminal));
        return text.toString();
    }

    @Override
    public String visitObjectsBody(final OurGrammarParser.ObjectsBodyContext context) {
        final List<OurGrammarParser.ObjectDeclarationContext> objectDeclarationContexts = context.objectDeclaration();
        final List<TerminalNode> commaTerminals = context.COMMA();
        final StringBuilder text = new StringBuilder();
        final OurGrammarParser.ObjectDeclarationContext firstObjectDeclarationContext = objectDeclarationContexts.get(0);
        text.append(this.visit(firstObjectDeclarationContext));
        for (int index = 0; index < commaTerminals.size(); index++) {
            final TerminalNode commaTerminal = commaTerminals.get(index);
            final OurGrammarParser.ObjectDeclarationContext objectDeclarationContext = objectDeclarationContexts.get(index + 1);
            text.append(this.visit(commaTerminal));
            this.appendNewLinesAndIndent(text, 1);
            text.append(this.visit(objectDeclarationContext));
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
        final TerminalNode fromSdkTerminal = context.FROM_SDK();
        final StringBuilder text = new StringBuilder();
        text.append(this.visit(nameTerminal))
            .append(' ')
            .append(this.visit(fromSdkTerminal));
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
        final OurGrammarParser.ReturnMethodCallContext returnMethodCallContext = context.returnMethodCall();
        final StringBuilder text = new StringBuilder();
        if (voidMethodCallContext != null) {
            text.append(this.visit(voidMethodCallContext));
        } else if (returnMethodCallContext != null) {
            text.append(this.visit(returnMethodCallContext));
        }
        return text.toString();
    }

    @Override
    public String visitVoidMethodCall(final OurGrammarParser.VoidMethodCallContext context) {
        final OurGrammarParser.VariableNameContext variableNameContext = context.variableName();
        final TerminalNode commaTerminal = context.COMMA();
        final OurGrammarParser.MethodNameContext methodNameContext = context.methodName();
        final OurGrammarParser.CallSuffixContext callSuffixContext = context.callSuffix();
        final StringBuilder text = new StringBuilder();
        text.append(this.visit(variableNameContext))
            .append(this.visit(commaTerminal))
            .append(' ')
            .append(this.visit(methodNameContext))
            .append(this.visit(callSuffixContext));
        return text.toString();
    }

    @Override
    public String visitReturnMethodCall(final OurGrammarParser.ReturnMethodCallContext context) {
        final OurGrammarParser.VariableNameContext variableNameContext = context.variableName();
        final TerminalNode commaTerminal = context.COMMA();
        final TerminalNode weNeedTerminal = context.WE_NEED();
        final OurGrammarParser.MethodNameContext methodNameContext = context.methodName();
        final OurGrammarParser.CallSuffixContext callSuffixContext = context.callSuffix();
        final StringBuilder text = new StringBuilder();
        text.append(this.visit(variableNameContext))
            .append(this.visit(commaTerminal))
            .append(' ')
            .append(this.visit(weNeedTerminal))
            .append(' ')
            .append(this.visit(methodNameContext))
            .append(this.visit(callSuffixContext));
        return text.toString();
    }

    @Override
    public String visitCallSuffix(final OurGrammarParser.CallSuffixContext context) {
        final TerminalNode dotTerminal = context.DOT();
        final OurGrammarParser.SimpleArgumentsContext simpleArgumentsContext = context.simpleArguments();
        final OurGrammarParser.NamedArgumentsContext namedArgumentsContext = context.namedArguments();
        final StringBuilder text = new StringBuilder();
        if (simpleArgumentsContext != null) {
            text.append(' ')
                .append(this.visit(simpleArgumentsContext))
                .append(this.visit(dotTerminal));
        } else if (namedArgumentsContext != null) {
            text.append(' ')
                .append(this.visit(namedArgumentsContext));
        } else if (dotTerminal != null) {
            text.append(this.visit(dotTerminal));
        }
        return text.toString();
    }

    @Override
    public String visitSimpleArguments(final OurGrammarParser.SimpleArgumentsContext context) {
        final OurGrammarParser.ValueContext valueContext = context.value();
        final StringBuilder text = new StringBuilder();
        text.append(this.visit(valueContext));
        return text.toString();
    }

    @Override
    public String visitNamedArguments(final OurGrammarParser.NamedArgumentsContext context) {
        final TerminalNode openBraceTerminal = context.OPEN_BRACE();
        final OurGrammarParser.PairsContext pairsContext = context.pairs();
        final TerminalNode closeBraceTerminal = context.CLOSE_BRACE();
        final StringBuilder text = new StringBuilder();
        text.append(this.visit(openBraceTerminal));
        this.currentIndentLevel++;
        this.appendNewLinesAndIndent(text, 1);
        text.append(this.visit(pairsContext));
        this.currentIndentLevel--;
        this.appendNewLinesAndIndent(text, 1);
        text.append(this.visit(closeBraceTerminal));
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
