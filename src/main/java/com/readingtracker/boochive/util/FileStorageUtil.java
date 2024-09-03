package com.readingtracker.boochive.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Value;
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

    private final Path rootLocation;

    public FileStorageUtil(@Value("${file.upload-dir}") String rootLocation) {
        this.rootLocation = Paths.get(rootLocation);
    }

    /**
     * 파일 업로드 (바이너리 > 저장 경로)
     */
    public String upload(String directory, MultipartFile file) throws IOException {
        String filename = generateUniqueFileName(file.getOriginalFilename());

        // 파일 저장 경로
        Path destinationDir = rootLocation.resolve(directory).normalize();
        Path destinationFile = destinationDir.resolve(filename).normalize().toAbsolutePath();

        // 디렉토리 없을 경우, 생성
        if (!Files.exists(destinationDir)) {
            Files.createDirectories(destinationDir);
        }

        log.info("Root location: " + rootLocation);
        log.info("Destination dir: " + destinationDir);
        log.info("Destination file: " + destinationFile.toFile());

        // 저장
        file.transferTo(destinationFile.toFile());

        // 상대경로 반환
        return rootLocation.toAbsolutePath().relativize(destinationFile).toString();
    }

    /**
     * 파일 이동
     */
    public String moveFile(String fileUrl, String targetDirectory) throws IOException {
//        Path filePath = Paths.get(fileUrl).normalize();
        Path filePath = rootLocation.resolve(fileUrl).normalize();

        // 파일 이동 경로
        Path targetDir = rootLocation.resolve(targetDirectory).normalize();
        Path targetPath = targetDir.resolve(filePath.getFileName()).normalize().toAbsolutePath();

        // 디렉토리 없을 경우, 생성
        if (!Files.exists(targetDir)) {
            Files.createDirectories(targetDir);
        }

        // 파일 이동
        Files.move(filePath, targetPath);

        log.info("Moved file from {} to {}", filePath, targetPath);

        // 상대경로 반환
        return rootLocation.toAbsolutePath().relativize(targetPath).toString();
    }

    /**
     * 파일 삭제
     */
    public void deleteFile(String fileUrl) throws IOException {
//        Path filePath = Paths.get(fileUrl).normalize();
        Path filePath = rootLocation.resolve(fileUrl).normalize();

        if (Files.exists(filePath)) {
            FileUtils.forceDelete(filePath.toFile());
            log.info("Deleted file: {}", filePath);
        } else {
            log.warn("File not found for deletion: {}", filePath);
        }
    }

    /**
     * 디렉토리 삭제
     */
    public void deleteDirectory(String directory) throws IOException {
        Path dir = rootLocation.resolve(directory).normalize();
        File directoryToDelete = dir.toFile();

        if (directoryToDelete.exists() && directoryToDelete.isDirectory()) {
            FileUtils.deleteDirectory(directoryToDelete);
            log.info("Deleted directory: {}", dir);
        } else {
            log.warn("Directory not found for deletion: {}", dir);
        }
    }

    /**
     * 디렉토리 내 파일 초기화
     */
    public void clearDirectory(String directory) throws IOException {
        Path dir = rootLocation.resolve(directory).normalize();

        if (Files.exists(dir) && Files.isDirectory(dir)) {
            File[] files = dir.toFile().listFiles();
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
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
