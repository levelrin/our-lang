package com.levelrin.adhoc;

import com.levelrin.compile.FromRootDir;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import org.junit.jupiter.api.Test;

// Excluding the following PMD rules via `ruleSet.xml` didn't work, for some reason.
@SuppressWarnings({"PMD.TooManyMethods", "PMD.LinguisticNaming", "PMD.SystemPrintln"})
final class AdHocTest {

    @Test
    void temp() throws URISyntaxException {
        final Path rootPath = Paths.get(
            ClassLoader.getSystemResource("adhoc").toURI()
        );
        final Map<Path, String> sourceMap = new FromRootDir(rootPath).sourceMap();
        for (final Map.Entry<Path, String> entry : sourceMap.entrySet()) {
            System.out.println("Path: " + entry.getKey());
            System.out.println("Content:\n" + entry.getValue());
        }
    }

}
