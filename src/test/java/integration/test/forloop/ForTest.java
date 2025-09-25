package integration.test.forloop;

import integration.test.NpmTest;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import org.junit.jupiter.api.Test;

final class ForTest {

    @Test
    void runForLoopTests() throws Exception {
        final Path rootDir = Paths.get(
            Objects.requireNonNull(
                this.getClass().getClassLoader().getResource("integration-test/for")
            ).toURI()
        );
        new com.levelrin.ourlang.Test(rootDir).generateJest();
        new NpmTest(rootDir).run();
    }

}
