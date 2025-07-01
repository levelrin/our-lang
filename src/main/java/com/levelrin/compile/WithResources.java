package com.levelrin.compile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

/**
 * It's a decorator to include `.ours` files in the `src/main/resources`.
 */
public final class WithResources implements Sources {

    /**
     * We will decorate this.
     */
    private final Sources origin;

    /**
     * Constructor.
     *
     * @param origin See {@link WithResources#origin}.
     */
    public WithResources(final Sources origin) {
        this.origin = origin;
    }

    // fixme: We should refactor this method to remove these suppressions.
    @SuppressWarnings({"CyclomaticComplexity", "NestedTryDepth", "NestedIfDepth", "PMD.AvoidDeeplyNestedIfStmts"})
    @Override
    public Map<Path, String> sourceMap() {
        final Map<Path, String> result = new HashMap<>(this.origin.sourceMap());
        try {
            final String rootPathInResources = "";
            final Enumeration<URL> urls = ClassLoader.getSystemClassLoader().getResources(rootPathInResources);
            while (urls.hasMoreElements()) {
                final URL url = urls.nextElement();
                final String externalPath = url.toExternalForm();
                // Ignore resources in the test directory.
                if (externalPath.contains("/build/resources/test/")) {
                    continue;
                }
                if ("file".equals(url.getProtocol())) {
                    final Path rootPath = Paths.get(url.toURI());
                    Files.walk(rootPath)
                        .filter(Files::isRegularFile)
                        .filter(path -> {
                            final Path fileName = path.getFileName();
                            return fileName != null && fileName.toString().endsWith(".ours");
                        })
                        .forEach(path -> {
                            try (InputStream inputStream = Files.newInputStream(path)) {
                                result.put(path, this.readToString(inputStream));
                            } catch (final IOException ex) {
                                throw new IllegalStateException(String.format("Failed to read the file. path: %s", path), ex);
                            }
                        });
                } else if ("jar".equals(url.getProtocol())) {
                    // todo: This needs to be tested with the actual jar file.
                    final String jarPath = url.getPath().substring(5, url.getPath().indexOf('!'));
                    try (JarFile jar = new JarFile(URLDecoder.decode(jarPath, StandardCharsets.UTF_8))) {
                        final Enumeration<JarEntry> entries = jar.entries();
                        while (entries.hasMoreElements()) {
                            final JarEntry entry = entries.nextElement();
                            final String name = entry.getName();
                            if (name.endsWith(".ours") && !entry.isDirectory()) {
                                try (InputStream inputStream = ClassLoader.getSystemResourceAsStream(name)) {
                                    if (inputStream != null) {
                                        final String content = this.readToString(inputStream);
                                        result.put(Paths.get(name), content);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (final IOException | URISyntaxException ex) {
            throw new IllegalStateException("Failed to load resources", ex);
        }
        return result;
    }

    private String readToString(final InputStream inputStream) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (final IOException ex) {
            throw new IllegalStateException("Failed to read a resource", ex);
        }
    }

}
