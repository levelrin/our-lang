package com.levelrin.compile;

import com.levelrin.antlr.generated.OurGrammarLexer;
import com.levelrin.antlr.generated.OurGrammarParser;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

/**
 * It represents the collection of `about`s in the metadata.
 */
public final class Abouts {

    /**
     * See {@link Sources#sourceMap()}.
     */
    private final Map<Path, String> sourceMap;

    /**
     * Constructor.
     *
     * @param sourceMap See {@link Abouts#sourceMap}.
     */
    public Abouts(final Map<Path, String> sourceMap) {
        this.sourceMap = sourceMap;
    }

    /**
     * Key - the `about` value in the metadata.
     * Value - list of corresponding file paths.
     *
     * @return Already explained.
     */
    public Map<String, List<Path>> aboutMap() {
        final Map<String, List<Path>> result = new HashMap<>();
        result.put("executable", new ArrayList<>());
        result.put("class", new ArrayList<>());
        result.put("native-method", new ArrayList<>());
        result.put("settings", new ArrayList<>());
        for (final Map.Entry<Path, String> entry : this.sourceMap.entrySet()) {
            final Path path = entry.getKey();
            final String content = entry.getValue();
            final CharStream charStream = CharStreams.fromString(content);
            final OurGrammarLexer lexer = new OurGrammarLexer(charStream);
            final CommonTokenStream tokens = new CommonTokenStream(lexer);
            final OurGrammarParser parser = new OurGrammarParser(tokens);
            final ParseTree tree = parser.file();
            final AboutListener listener = new AboutListener();
            try {
                ParseTreeWalker.DEFAULT.walk(listener, tree);
            } catch (final ParseCancellationException ex) {
                // Do nothing.
            }
            final String about = listener.about();
            result.get(about).add(path);
        }
        return result;
    }

}
