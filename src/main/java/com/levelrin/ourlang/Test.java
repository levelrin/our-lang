package com.levelrin.ourlang;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public final class Test {

    private final Path rootDirPath;

    public Test(final Path rootDirPath) {
        this.rootDirPath = rootDirPath;
    }

    public void generateJest() throws IOException {
        final Path testDirPath = this.rootDirPath.resolve("test");
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(testDirPath)) {
            for (Path filePath : stream) {
                if (Files.isRegularFile(filePath)) {
                    final String content = Files.readString(filePath, StandardCharsets.UTF_8);
                    final StringBuilder js = new StringBuilder();
                    final OurGrammarListener listener = new OurGrammarListener(filePath, js, new ArrayList<>(), new ArrayList<>());
                    new OurGrammarWalker(content, listener).walk();
                    System.out.println(js);
                }
            }
        }
    }

}
