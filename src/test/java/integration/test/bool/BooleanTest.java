package integration.test.bool;

import integration.test.NpmTest;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import org.junit.jupiter.api.Test;

final class BooleanTest {

    @Test
    void runNumberTests() throws Exception {
        final Path rootDir = Paths.get(
            Objects.requireNonNull(
                this.getClass().getClassLoader().getResource("integration-test/boolean")
            ).toURI()
        );
        new com.levelrin.ourlang.Test(rootDir).generateJest();
        new NpmTest(rootDir).run();
    }

}
