package com.levelrin.ourlang;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

public final class Test {

    private final Path rootDirPath;

    public Test(final Path rootDirPath) {
        this.rootDirPath = rootDirPath;
    }

    public void generateJest() throws IOException {
        final Path testDirPath = this.rootDirPath.resolve("test");
        final Output output = new Output();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(testDirPath)) {
            for (Path filePath : stream) {
                if (Files.isRegularFile(filePath)) {
                    final String content = Files.readString(filePath, StandardCharsets.UTF_8);
                    final OurGrammarListener listener = new OurGrammarListener(filePath, output);
                    new OurGrammarWalker(content, listener).walk();
                }
            }
        }
        Files.writeString(this.rootDirPath.resolve("package.json"), "{\"scripts\":{\"test\":\"jest\"}}", StandardCharsets.UTF_8);
        Files.writeString(this.rootDirPath.resolve("test.js"), output.toString(), StandardCharsets.UTF_8);
    }

}
