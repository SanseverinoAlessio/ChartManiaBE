package com.chartmania.infrastructure;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;

@Component
public class LocalStorageManager {
   public Resource getFile(String absolutePath, String fileName) throws MalformedURLException, NoSuchFileException {
    if (absolutePath == null || absolutePath.isBlank()) {
        throw new IllegalArgumentException("absolutePath is null or blank");
    }
    if (fileName == null || fileName.isBlank()) {
        throw new IllegalArgumentException("fileName is null or blank");
    }

    Path root = Paths.get(absolutePath).toAbsolutePath().normalize();
    if (!Files.exists(root) || !Files.isDirectory(root)) {
        throw new IllegalArgumentException("Root path does not exist or is not a directory: " + root);
    }

    Path keyPath = Paths.get(fileName);
    if (keyPath.isAbsolute()) {
        throw new SecurityException("Absolute fileName is not allowed");
    }

    Path filePath = root.resolve(keyPath).normalize();

    if (!filePath.startsWith(root)) {
        throw new SecurityException("Invalid file path (path traversal attempt)");
    }

    if (!Files.exists(filePath)) {
        throw new NoSuchFileException(filePath.toString());
    }
    if (!Files.isRegularFile(filePath)) {
        throw new IllegalArgumentException("Requested path is not a regular file: " + filePath);
    }
    if (!Files.isReadable(filePath)) {
        throw new SecurityException("File is not readable: " + filePath);
    }



    
    Resource resource = new UrlResource(filePath.toUri());
    if (!resource.exists() || !resource.isReadable()) {
        throw new NoSuchFileException(filePath.toString());
    }

    return resource;
}

    public String saveFile(byte[] file, String ext, String path) throws IOException {
        Path root = Paths.get(path);
        this.verifyIfExistsOrCreate(root);
        ByteArrayInputStream fileInputStream = new ByteArrayInputStream(file); 
        String fileName = RandomStringUtils.randomAlphanumeric(20) + '.' + ext;
        Path destination = root.resolve(fileName);
        Files.copy(fileInputStream, destination, StandardCopyOption.REPLACE_EXISTING);

        return fileName;
    }

    private void verifyIfExistsOrCreate(Path root) throws IOException{
        if(!Files.exists(root)){
            Files.createDirectories(root);
        }
    }

    public void deleteFile(String absolutePath) throws IOException{
        Path path = Paths.get(absolutePath);
        Files.deleteIfExists(path);
    }


}
