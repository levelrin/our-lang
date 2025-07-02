package com.levelrin.adhoc;

import com.levelrin.compile.Abouts;
import com.levelrin.compile.FromRootDir;
import com.levelrin.compile.WithResources;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

// Excluding the following PMD rules via `ruleSet.xml` didn't work, for some reason.
@SuppressWarnings({"PMD.TooManyMethods", "PMD.LinguisticNaming", "PMD.SystemPrintln"})
final class AdHocTest {

    @Test
    void temp() throws URISyntaxException {
        final Map<Path, String> sourceMap = new WithResources(
            new FromRootDir(
                Paths.get(
                    ClassLoader.getSystemResource("adhoc").toURI()
                )
            )
        ).sourceMap();
        final Map<String, List<Path>> aboutMap = new Abouts(sourceMap).aboutMap();
        final List<Path> executablePaths = aboutMap.get("executable");
        final List<Path> classPaths = aboutMap.get("class");
        final List<Path> methodPaths = aboutMap.get("native-method");
        System.out.println("executablePaths: " + executablePaths);
        System.out.println("classPaths: " + classPaths);
        System.out.println("methodPaths: " + methodPaths);
    }

}
