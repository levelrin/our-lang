package com.levelrin.compile;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.Resource;
import io.github.classgraph.ResourceList;
import io.github.classgraph.ScanResult;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

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

    @SuppressWarnings("PMD.LooseCoupling")
    @Override
    public Map<Path, String> sourceMap() {
        final Map<Path, String> result = new HashMap<>(this.origin.sourceMap());
        try (
            ScanResult scanResult = new ClassGraph().acceptPaths("our-lang/sdk").scan();
            // PMD complains this line for PMD.LooseCoupling, but this cannot be handled because its super type is not resolvable outside the library.
            ResourceList oursFiles = scanResult.getAllResources().filter(resource -> resource.getPath().endsWith(".ours"))
        ) {
            for (final Resource resource : oursFiles) {
                try (resource) {
                    result.put(
                        Paths.get(resource.getPath()),
                        resource.getContentAsString()
                    );
                } catch (final IOException ex) {
                    throw new IllegalStateException("Failed to read the content of the SDK file. path: " + resource.getPath(), ex);
                }
            }
        }
        return result;
    }

}
