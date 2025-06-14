package com.levelrin.node;

import com.levelrin.antlr.generated.OurGrammarLexer;
import com.levelrin.antlr.generated.OurGrammarParser;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

// Excluding the following PMD rules via `ruleSet.xml` didn't work, for some reason.
@SuppressWarnings({"PMD.TooManyMethods", "PMD.LinguisticNaming"})
final class NodeVisitorTest {

    @Test
    void shouldGenerateHelloLogic() {
        this.assertLogic("hello-logic.ours", "hello-logic.js");
    }

    @Test
    void shouldGenerateHelloObjects() {
        this.assertObjects("hello-objects.ours", "hello-objects.js");
    }

    @Test
    void shouldGenerateHelloFile() {
        this.assertFile("hello-file.ours", "hello-file.js");
    }

    /**
     * Assert that the logic section written in the `source` file is converted into the code written in the `after` file.
     *
     * @param source The file name that has code before the conversion.
     * @param target The file name that has code after the conversion.
     */
    void assertLogic(final String source, final String target) {
        try {
            final Path beforePath = Paths.get(
                ClassLoader.getSystemResource(
                    String.format("node/%s", source)
                ).toURI()
            );
            final String originalText = Files.readString(beforePath, StandardCharsets.UTF_8);
            final CharStream charStream = CharStreams.fromString(originalText);
            final OurGrammarLexer lexer = new OurGrammarLexer(charStream);
            final CommonTokenStream tokens = new CommonTokenStream(lexer);
            final OurGrammarParser parser = new OurGrammarParser(tokens);
            final ParseTree tree = parser.logic();
            final NodeVisitor visitor = new NodeVisitor();
            final String result = visitor.visit(tree);
            final Path afterPath = Paths.get(
                ClassLoader.getSystemResource(
                    String.format("node/%s", target)
                ).toURI()
            );
            final String expectedText = Files.readString(afterPath, StandardCharsets.UTF_8);
            MatcherAssert.assertThat(
                String.format("Result:%n%s", result),
                result,
                Matchers.equalTo(expectedText)
            );
        } catch (final URISyntaxException | IOException ex) {
            throw new IllegalStateException(
                String.format(
                    "Failed to read files. before: %s, after: %s",
                    source,
                    target
                ),
                ex
            );
        }
    }

    /**
     * Assert that the objects section written in the `source` file is converted into the code written in the `after` file.
     *
     * @param source The file name that has code before the conversion.
     * @param target The file name that has code after the conversion.
     */
    void assertObjects(final String source, final String target) {
        try {
            final Path beforePath = Paths.get(
                ClassLoader.getSystemResource(
                    String.format("node/%s", source)
                ).toURI()
            );
            final String originalText = Files.readString(beforePath, StandardCharsets.UTF_8);
            final CharStream charStream = CharStreams.fromString(originalText);
            final OurGrammarLexer lexer = new OurGrammarLexer(charStream);
            final CommonTokenStream tokens = new CommonTokenStream(lexer);
            final OurGrammarParser parser = new OurGrammarParser(tokens);
            final ParseTree tree = parser.objects();
            final NodeVisitor visitor = new NodeVisitor();
            final String result = visitor.visit(tree);
            final Path afterPath = Paths.get(
                ClassLoader.getSystemResource(
                    String.format("node/%s", target)
                ).toURI()
            );
            final String expectedText = Files.readString(afterPath, StandardCharsets.UTF_8);
            MatcherAssert.assertThat(
                String.format("Result:%n%s", result),
                result,
                Matchers.equalTo(expectedText)
            );
        } catch (final URISyntaxException | IOException ex) {
            throw new IllegalStateException(
                String.format(
                    "Failed to read files. before: %s, after: %s",
                    source,
                    target
                ),
                ex
            );
        }
    }

    /**
     * Assert that the file written in the `source` is converted into the code written in the `after` file.
     *
     * @param source The file name that has code before the conversion.
     * @param target The file name that has code after the conversion.
     */
    void assertFile(final String source, final String target) {
        try {
            final Path beforePath = Paths.get(
                ClassLoader.getSystemResource(
                    String.format("node/%s", source)
                ).toURI()
            );
            final String originalText = Files.readString(beforePath, StandardCharsets.UTF_8);
            final CharStream charStream = CharStreams.fromString(originalText);
            final OurGrammarLexer lexer = new OurGrammarLexer(charStream);
            final CommonTokenStream tokens = new CommonTokenStream(lexer);
            final OurGrammarParser parser = new OurGrammarParser(tokens);
            final ParseTree tree = parser.file();
            final NodeVisitor visitor = new NodeVisitor();
            final String result = visitor.visit(tree);
            final Path afterPath = Paths.get(
                ClassLoader.getSystemResource(
                    String.format("node/%s", target)
                ).toURI()
            );
            final String expectedText = Files.readString(afterPath, StandardCharsets.UTF_8);
            MatcherAssert.assertThat(
                String.format("Result:%n%s", result),
                result,
                Matchers.equalTo(expectedText)
            );
        } catch (final URISyntaxException | IOException ex) {
            throw new IllegalStateException(
                String.format(
                    "Failed to read files. before: %s, after: %s",
                    source,
                    target
                ),
                ex
            );
        }
    }

}
