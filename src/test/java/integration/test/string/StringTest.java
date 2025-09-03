package integration.test.string;

import com.levelrin.ourlang.Main;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import org.junit.jupiter.api.Test;

final class StringTest {

    @Test
    void runStringMain() throws URISyntaxException, IOException {
        final Path mainPath = Paths.get(
            Objects.requireNonNull(
                this.getClass().getClassLoader().getResource("integration-test/string/main.ours")
            ).toURI()
        );
        final Main main = new Main(mainPath);
        System.out.println(main.toJs());
    }

    @Test
    void runStringTests() throws URISyntaxException, IOException {
        final Path rootDir = Paths.get(
            Objects.requireNonNull(
                this.getClass().getClassLoader().getResource("integration-test/string")
            ).toURI()
        );
        new com.levelrin.ourlang.Test(rootDir).generateJest();
    }

}
