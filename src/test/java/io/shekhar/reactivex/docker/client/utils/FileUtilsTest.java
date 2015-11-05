package io.shekhar.reactivex.docker.client.utils;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtilsTest {

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    @Test
    public void shouldTestIfFileExistsInsideDirectory() throws Exception {
        Path pathToExportTo = tmp.newFolder().toPath();
        System.out.println(pathToExportTo);
        Path tempFile = Files.createTempFile(pathToExportTo, "abc", "def");
        System.out.println(tempFile);
        Files.newDirectoryStream(pathToExportTo, p -> p.toFile().isFile()).forEach(System.out::println);
    }
}
