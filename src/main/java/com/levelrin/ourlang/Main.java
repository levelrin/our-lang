package com.levelrin.ourlang;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class Main {

    private final Path path;

    public Main(final Path path) {
        this.path = path;
    }

    public String toJs() throws IOException {
        final String content = Files.readString(this.path, StandardCharsets.UTF_8);
        final Output output = new Output();
        final OurGrammarListener listener = new OurGrammarListener(this.path, output);
        new OurGrammarWalker(content, listener).walk();
        return output.toString();
    }

}
