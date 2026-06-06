package com.way_ne.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Component
public class LocalHeadStorage {
    private static final DateTimeFormatter DIR_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM");

    private final Path rootDir;

    public LocalHeadStorage(@Value("${upload.head-dir:}") String configuredHeadDir) {
        this.rootDir = resolveRootDir(configuredHeadDir);
    }

    public String store(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.lastIndexOf('.') != -1) {
            extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
        }

        String dir = LocalDate.now().format(DIR_FORMATTER);
        String newFileName = UUID.randomUUID() + extension;
        Path targetDir = rootDir.resolve(dir).normalize();
        Files.createDirectories(targetDir);

        Path targetFile = targetDir.resolve(newFileName).normalize();
        file.transferTo(targetFile);

        return "/head/" + dir.replace(File.separatorChar, '/') + "/" + newFileName;
    }

    public Path getRootDir() {
        return rootDir;
    }

    private Path resolveRootDir(String configuredHeadDir) {
        if (StringUtils.hasText(configuredHeadDir)) {
            return Paths.get(configuredHeadDir).toAbsolutePath().normalize();
        }

        Path current = Paths.get(System.getProperty("user.dir")).toAbsolutePath().normalize();
        while (current != null) {
            Path candidate = current.resolve("head");
            if (Files.isDirectory(candidate)) {
                return candidate;
            }
            current = current.getParent();
        }

        return Paths.get(System.getProperty("user.dir"), "head").toAbsolutePath().normalize();
    }
}
