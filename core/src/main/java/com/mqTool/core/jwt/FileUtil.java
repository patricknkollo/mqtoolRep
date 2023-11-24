package com.mqTool.core.jwt;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileUtil {
    public static String readFromFile(String filename) throws IOException {
        File resource = new File(filename);
        return new String(Files.readAllBytes(resource.toPath()));
    }
}
