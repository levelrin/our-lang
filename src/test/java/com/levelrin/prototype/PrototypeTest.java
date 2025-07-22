package com.levelrin.prototype;

import com.levelrin.compile.Abouts;
import com.levelrin.compile.Classes;
import com.levelrin.compile.FromRootDir;
import com.levelrin.compile.SdkObjects;
import com.levelrin.compile.Settings;
import com.levelrin.compile.SourceFlow;
import com.levelrin.compile.WithResources;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

// Excluding the following PMD rules via `ruleSet.xml` didn't work, for some reason.
@SuppressWarnings({"PMD.TooManyMethods", "PMD.LinguisticNaming", "PMD.SystemPrintln", "UnusedLocalVariable"})
final class PrototypeTest {

    @Test
    void temp() throws URISyntaxException {
        System.out.println("sourceMap:");
        final Map<Path, String> sourceMap = new WithResources(
            new FromRootDir(
                Paths.get(
                    ClassLoader.getSystemResource("prototype").toURI()
                )
            )
        ).sourceMap();
        for (final Map.Entry<Path, String> entry : sourceMap.entrySet()) {
            System.out.println(entry.getKey().toAbsolutePath());
        }
        System.out.println("\naboutMap:");
        final Map<String, List<Path>> aboutMap = new Abouts(sourceMap).aboutMap();
        for (final Map.Entry<String, List<Path>> entry : aboutMap.entrySet()) {
            System.out.println(entry.getKey());
            for (final Path path : entry.getValue()) {
                System.out.println("\t" + path.toAbsolutePath());
            }
        }
        final Path settingsPath = aboutMap.get("settings").get(0);
        System.out.println("\npackageName:");
        final String packageName = new Settings(sourceMap, settingsPath).packageName();
        System.out.println(packageName);
        System.out.println("\nclassMap:");
        final List<Path> classPaths = aboutMap.get("class");
        final List<Path> methodPaths = aboutMap.get("native-method");
        final Map<Path, List<Path>> classMap = new Classes(sourceMap, classPaths, methodPaths).classMap();
        for (final Map.Entry<Path, List<Path>> entry : classMap.entrySet()) {
            System.out.println(entry.getKey().toAbsolutePath());
            for (final Path path : entry.getValue()) {
                System.out.println("\t" + path.toAbsolutePath());
            }
        }
        System.out.println("\nsdkObjects:");
        final Map<String, Path> sdkObjectsMap = new SdkObjects().sourceMap();
        for (final Map.Entry<String, Path> entry : sdkObjectsMap.entrySet()) {
            System.out.println(entry.getKey() + " - " + entry.getValue().toAbsolutePath());
        }
        System.out.println("\nsourceFlow:");
        final Path executablePath = aboutMap.get("executable").get(0);
        final Set<Path> sourceFlowSet = new SourceFlow(sourceMap, executablePath, sdkObjectsMap).set();
        for (final Path path : sourceFlowSet) {
            System.out.println(path.toAbsolutePath());
        }
    }

}
