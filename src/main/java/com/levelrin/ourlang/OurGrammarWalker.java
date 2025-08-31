package com.levelrin.ourlang;

import com.levelrin.antlr.generated.OurGrammarLexer;
import com.levelrin.antlr.generated.OurGrammarParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

public final class OurGrammarWalker {

    private final String content;

    private final OurGrammarListener listener;

    public OurGrammarWalker(final String content, final OurGrammarListener listener) {
        this.content = content;
        this.listener = listener;
    }

    public void walk() {
        final CharStream charStream = CharStreams.fromString(this.content);
        final OurGrammarLexer lexer = new OurGrammarLexer(charStream);
        final CommonTokenStream tokens = new CommonTokenStream(lexer);
        final OurGrammarParser parser = new OurGrammarParser(tokens);
        final ParseTree tree = parser.headers();
        ParseTreeWalker.DEFAULT.walk(this.listener, tree);
    }

}
