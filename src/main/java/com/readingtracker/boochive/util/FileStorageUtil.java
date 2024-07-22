package com.readingtracker.boochive.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
@Slf4j
public class FileStorageUtil {

    private final Path rootLocation = Paths.get("uploads");

    /**
     * 파일 업로드 (바이너리 > 저장 경로)
     */
    public String upload(String directory, MultipartFile file) throws IOException {
        String filename = generateUniqueFileName(file.getOriginalFilename());

        // 파일 저장 경로
        Path destinationDir = rootLocation.resolve(directory).normalize();
        Path destinationFile = destinationDir.resolve(filename).normalize();

        // 디렉토리 없을 경우, 생성
        if (!Files.exists(destinationDir)) {
            Files.createDirectories(destinationDir);
        }

        log.info("Root location: " + rootLocation);
        log.info("Destination dir: " + destinationDir);
        log.info("Destination file: " + destinationFile);

        // 저장
        file.transferTo(destinationFile);
        return "/" + destinationFile.toString().replace("\\", "/");
    }

    /**
     * 디렉토리 삭제
     */
    public void clearDirectory(String directory) throws IOException {
        Path dir = rootLocation.resolve(directory).normalize();
        File[] files = dir.toFile().listFiles();
        if (files != null) {
            for (File file : files) {
                file.delete();
            }
        }
    }

    /**
     * (공통 메서드) 파일명 생성
     */
    private String generateUniqueFileName(String originalFileName) {
        // 확장자
        String extension = "";
        int dotIndex = originalFileName.lastIndexOf('.');
        if (dotIndex > 0) {
            extension = originalFileName.substring(dotIndex);
        }

        // Unique UUID 파일명 생성
        String fileName = UUID.randomUUID().toString();

        return fileName + extension;
    }
}
