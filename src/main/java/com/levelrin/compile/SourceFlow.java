package com.levelrin.compile;

import com.levelrin.antlr.generated.OurGrammarLexer;
import com.levelrin.antlr.generated.OurGrammarParser;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

/**
 * We want to know which source files are actually used by the executable file.
 * For that reason, we will record the source files in order of usages.
 */
public final class SourceFlow {

    /**
     * See {@link Sources#sourceMap()}.
     */
    private final Map<Path, String> sourceMap;

    /**
     * As is.
     */
    private final Path executablePath;

    /**
     * See {@link SdkObjects#sourceMap()}.
     */
    private final Map<String, Path> sdkObjectsMap;

    /**
     * Constructor.
     *
     * @param sourceMap See {@link SourceFlow#sourceMap}.
     * @param executablePath See {@link SourceFlow#executablePath}.
     * @param sdkObjectsMap  See {@link SourceFlow#sdkObjectsMap}.
     */
    public SourceFlow(final Map<Path, String> sourceMap, final Path executablePath, final Map<String, Path> sdkObjectsMap) {
        this.sourceMap = sourceMap;
        this.executablePath = executablePath;
        this.sdkObjectsMap = sdkObjectsMap;
    }

    /**
     * Return the set of source files used by the executable in order.
     *
     * @return Already explained.
     */
    public Set<Path> set() {
        final String executableContent = this.sourceMap.get(this.executablePath);
        final Set<Path> result = new HashSet<>(this.fromListener(executableContent));
        final Set<Path> checked = new HashSet<>();
        checked.add(this.executablePath);
        boolean shouldContinue = true;
        while (shouldContinue) {
            shouldContinue = false;
            for (final Path path : result) {
                if (!checked.contains(path)) {
                    result.addAll(this.fromListener(this.sourceMap.get(path)));
                    checked.add(path);
                    shouldContinue = true;
                    break;
                }
            }
        }
        return result;
    }

    private Set<Path> fromListener(final String content) {
        final CharStream charStream = CharStreams.fromString(content);
        final OurGrammarLexer lexer = new OurGrammarLexer(charStream);
        final CommonTokenStream tokens = new CommonTokenStream(lexer);
        final OurGrammarParser parser = new OurGrammarParser(tokens);
        final ParseTree tree = parser.file();
        final SourceFlowListener listener = new SourceFlowListener(this.sdkObjectsMap);
        ParseTreeWalker.DEFAULT.walk(listener, tree);
        return listener.usedSources();
    }

}
