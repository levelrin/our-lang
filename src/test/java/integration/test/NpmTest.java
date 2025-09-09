package integration.test;

import java.nio.file.Path;

public final class NpmTest {

    private final Path projectDir;

    public NpmTest(final Path projectDir) {
        this.projectDir = projectDir;
    }

    public void run() throws Exception {
        final int jestInstallExitCode = new ProcessBuilder("npm", "install", "--save-dev", "jest")
            .directory(this.projectDir.toFile())
            .inheritIO().start().waitFor();
        if (jestInstallExitCode != 0) {
            throw new RuntimeException("npm test failed with exit code " + jestInstallExitCode);
        }
        final int npmTestExitCode = new ProcessBuilder("npm", "test")
            .directory(this.projectDir.toFile())
            .inheritIO().start().waitFor();
        if (npmTestExitCode != 0) {
            throw new RuntimeException("npm test failed with exit code " + npmTestExitCode);
        }
    }

}
