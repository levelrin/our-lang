package com.levelrin.compile;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.Resource;
import io.github.classgraph.ResourceList;
import io.github.classgraph.ScanResult;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * We want to know the path to the source file using the object name from SDK.
 */
@SuppressWarnings("MissingCtor")
public final class SdkObjects {

    /**
     * Key - object name.
     * Value - path to the corresponding source.
     *
     * @return Already explained.
     */
    @SuppressWarnings("PMD.LooseCoupling")
    public Map<String, Path> sourceMap() {
        final Map<String, Path> result = new HashMap<>();
        try (
            ScanResult scanResult = new ClassGraph().acceptPaths("sdk").scan();
            // PMD complains this line for PMD.LooseCoupling, but this cannot be handled because its super type is not resolvable outside the library.
            ResourceList oursFiles = scanResult.getAllResources().filter(resource -> resource.getPath().endsWith(".ours"))
        ) {
            for (final Resource resource : oursFiles) {
                try (resource) {
                    final Path path = Paths.get(resource.getPath());
                    final Path fileNamePath = path.getFileName();
                    if (fileNamePath == null) {
                        throw new IllegalStateException("The file name does not exist. path: " + path.toAbsolutePath());
                    }
                    final String fileName = fileNamePath.toString();
                    final int dotIndex = fileName.lastIndexOf('.');
                    final String nameWithoutExtension;
                    if (dotIndex == -1) {
                        nameWithoutExtension = fileName;
                    } else {
                        nameWithoutExtension = fileName.substring(0, dotIndex);
                    }
                    final Path parent = path.getParent();
                    if (parent != null) {
                        final Path parentFileNamePath = parent.getFileName();
                        if (parentFileNamePath == null) {
                            throw new IllegalStateException("The parent file name does not exist. path: " + parent.toAbsolutePath());
                        }
                        final String parentDirName = parentFileNamePath.toString();
                        if (nameWithoutExtension.equals(parentDirName)) {
                            result.put(nameWithoutExtension, path);
                        }
                    }
                }
            }
        }
        return result;
    }

}
