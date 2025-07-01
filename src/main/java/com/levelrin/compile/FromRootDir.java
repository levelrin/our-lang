package com.levelrin.compile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * It's responsible for reading source files from the project root directory.
 */
public final class FromRootDir implements Sources {

    /**
     * As is.
     */
    private final Path rootDir;

    /**
     * Constructor.
     *
     * @param rootDir As is.
     */
    public FromRootDir(final Path rootDir) {
        this.rootDir = rootDir;
    }

    /**
     * Read all `.ours` files, store them into a map, and return it.
     *
     * @return Key - the path to the source file.
     *         Value - the content of the file.
     */
    @Override
    public Map<Path, String> sourceMap() {
        final Map<Path, String> result = new HashMap<>();
        try (Stream<Path> paths = Files.walk(this.rootDir)) {
            paths.filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith(".ours"))
                .forEach(path -> {
                    try {
                        final String content = Files.readString(path, StandardCharsets.UTF_8);
                        result.put(path, content);
                    } catch (final IOException ex) {
                        throw new IllegalStateException(String.format("Failed to read a file. path: %s", path), ex);
                    }
                });
        } catch (final IOException ex) {
            throw new IllegalStateException(
                String.format("Failed to compile from the root directory: %s", this.rootDir), ex
            );
        }
        return result;
    }

}
