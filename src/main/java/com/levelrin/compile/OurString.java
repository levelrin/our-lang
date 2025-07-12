package com.levelrin.compile;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * It represents the string type in our-lang.
 */
public final class OurString {

    /**
     * Raw string literal.
     */
    private final String raw;

    /**
     * Constructor.
     *
     * @param raw See {@link OurString}.
     */
    public OurString(final String raw) {
        this.raw = raw;
    }

    /**
     * Remove the surrounding back quotes.
     *
     * @return String content without the surrounding back quotes.
     */
    public String content() {
        return this.raw.substring(1, this.raw.length() - 1);
    }

    /**
     * It assumes the string is the `class-uri` in the metadata.
     *
     * @return Path to the class-uri file.
     */
    public Path classUriPath() {
        final String content = this.content();
        final Path result;
        if (content.startsWith("sdk://")) {
            try {
                result = Paths.get(
                    ClassLoader.getSystemResource("sdk/" + content.substring(6)).toURI()
                );
            } catch (final URISyntaxException ex) {
                throw new IllegalStateException(String.format("Failed to load the class-uri: %s", content), ex);
            }
        } else {
            throw new UnsupportedOperationException("non-native-method is not implemented yet.");
        }
        return result;
    }

}
