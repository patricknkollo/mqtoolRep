package com.mqTool.jwt;

import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;

public class FileUtils {
    public static String readFromFile(String filename) throws IOException {
        File resource = new File(filename);
        return new String(Files.readAllBytes(resource.toPath()));
    }
}
