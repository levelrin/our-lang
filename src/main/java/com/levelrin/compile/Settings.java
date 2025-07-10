package com.levelrin.compile;

import com.levelrin.antlr.generated.OurGrammarLexer;
import com.levelrin.antlr.generated.OurGrammarParser;
import java.nio.file.Path;
import java.util.Map;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

/**
 * It represents the `settings.ours` file.
 */
public final class Settings {

    /**
     * See {@link Sources#sourceMap()}.
     */
    private final Map<Path, String> sourceMap;

    /**
     * The path to the `settings.ours` file.
     */
    private final Path path;

    /**
     * Constructor.
     *
     * @param sourceMap See {@link Settings#sourceMap}.
     * @param path See {@link Settings#path}.
     */
    public Settings(final Map<Path, String> sourceMap, final Path path) {
        this.sourceMap = sourceMap;
        this.path = path;
    }

    /**
     * The value of the `package-name` in the metadata.
     *
     * @return As is.
     */
    public String packageName() {
        final String settingsContent = this.sourceMap.get(this.path);
        final CharStream charStream = CharStreams.fromString(settingsContent);
        final OurGrammarLexer lexer = new OurGrammarLexer(charStream);
        final CommonTokenStream tokens = new CommonTokenStream(lexer);
        final OurGrammarParser parser = new OurGrammarParser(tokens);
        final ParseTree tree = parser.file();
        final SettingsListener listener = new SettingsListener();
        try {
            ParseTreeWalker.DEFAULT.walk(listener, tree);
        } catch (final ParseCancellationException ex) {
            // Do nothing.
        }
        return listener.packageName();
    }

}
