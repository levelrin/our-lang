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
 * It represents the collection classes.
 */
public final class Classes {

    /**
     * See {@link Sources#sourceMap()}.
     */
    private final Map<Path, String> sourceMap;

    /**
     * List of class file paths.
     */
    private final List<Path> classPaths;

    /**
     * List of method file paths.
     */
    private final List<Path> methodPaths;

    /**
     * Constructor.
     *
     * @param sourceMap See {@link Classes#sourceMap}.
     * @param classPaths See {@link Classes#classPaths}.
     * @param methodPaths See {@link Classes#methodPaths}.
     */
    public Classes(final Map<Path, String> sourceMap, final List<Path> classPaths, final List<Path> methodPaths) {
        this.sourceMap = sourceMap;
        this.classPaths = classPaths;
        this.methodPaths = methodPaths;
    }

    /**
     * Key - path to the class file.
     * Value - list of corresponding method file paths.
     *
     * @return Already explained.
     */
    public Map<Path, List<Path>> classMap() {
        final Map<Path, List<Path>> result = new HashMap<>();
        for (final Path path : this.classPaths) {
            result.put(path, new ArrayList<>());
        }
        for (final Path path : this.methodPaths) {
            final String methodContent = this.sourceMap.get(path);
            final CharStream charStream = CharStreams.fromString(methodContent);
            final OurGrammarLexer lexer = new OurGrammarLexer(charStream);
            final CommonTokenStream tokens = new CommonTokenStream(lexer);
            final OurGrammarParser parser = new OurGrammarParser(tokens);
            final ParseTree tree = parser.file();
            final ClassUriListener listener = new ClassUriListener();
            try {
                ParseTreeWalker.DEFAULT.walk(listener, tree);
            } catch (final ParseCancellationException ex) {
                // Do nothing.
            }
            final Path key = listener.classUriInOurString().classUriPath();
            result.get(key).add(path);
        }
        return result;
    }

}
