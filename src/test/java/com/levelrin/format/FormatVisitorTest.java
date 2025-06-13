package com.levelrin.format;

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
final class FormatVisitorTest {

    @Test
    void shouldFormatHello() {
        this.compare("hello-before.ours", "hello-after.ours");
    }

    /**
     * Assert that the formatter formats the code written in the `before` file that matches with the code written in the `after` file.
     *
     * @param before The file name that has code before formatting.
     * @param after The file name that has code after formatting.
     */
    void compare(final String before, final String after) {
        try {
            final Path beforePath = Paths.get(
                ClassLoader.getSystemResource(
                    String.format("format/%s", before)
                ).toURI()
            );
            final String originalText = Files.readString(beforePath, StandardCharsets.UTF_8);
            final CharStream charStream = CharStreams.fromString(originalText);
            final OurGrammarLexer lexer = new OurGrammarLexer(charStream);
            final CommonTokenStream tokens = new CommonTokenStream(lexer);
            final OurGrammarParser parser = new OurGrammarParser(tokens);
            final ParseTree tree = parser.file();
            final FormatVisitor visitor = new FormatVisitor();
            final String result = visitor.visit(tree);
            final Path afterPath = Paths.get(
                ClassLoader.getSystemResource(
                    String.format("format/%s", after)
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
                    before,
                    after
                ),
                ex
            );
        }
    }

}
