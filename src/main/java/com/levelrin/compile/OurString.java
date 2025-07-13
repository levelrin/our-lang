package com.levelrin.compile;

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

}
