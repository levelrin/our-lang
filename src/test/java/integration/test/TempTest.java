package integration.test;

import com.levelrin.ourlang.Main;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import org.junit.jupiter.api.Test;

final class TempTest {

    @Test
    void temp() throws URISyntaxException, IOException {
        final Path mainPath = Paths.get(
            Objects.requireNonNull(
                this.getClass().getClassLoader().getResource("integration-test/string/main.ours")
            ).toURI()
        );
        final Main main = new Main(mainPath);
        System.out.println(main.toJs());
    }

}
