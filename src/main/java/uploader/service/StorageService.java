package uploader.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import uploader.exception.StorageException;
import uploader.properties.StorageProperties;
import uploader.exception.StorageFileNotFoundException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

@Service
public class StorageService {

    private final Path uploadFolder;

    @Autowired
    public StorageService(StorageProperties properties) {
        this.uploadFolder = Paths.get(properties.getUploadFolder());
    }

    private Path load(String filename) {
        return uploadFolder.resolve(filename);
    }

    public void store(MultipartFile file) {
        String filename = StringUtils.cleanPath(file.getOriginalFilename());

        try {
//            if (!file.getContentType().contains("text/xml") || !file.getContentType().contains("application/xml")) {
//                throw new XmlException("File must be xml");
//            }

            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file " + filename);
            }

            if (filename.contains("..")) {
                throw new StorageException(
                        "Cannot store file with relative path outside current directory "
                                + filename);
            }

            Files.copy(file.getInputStream(), this.uploadFolder.resolve(filename),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new StorageException("Failed to store file " + filename, e);
        }
    }

    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.uploadFolder, 1)
                    .filter(path -> !path.equals(this.uploadFolder))
                    .map(path -> this.uploadFolder.relativize(path));
        }
        catch (IOException e) {
            throw new StorageException("Failed to read stored files", e);
        }
    }

    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            }
            else {
                throw new StorageFileNotFoundException(
                        "Could not read file: " + filename);

            }
        }
        catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read file: " + filename, e);
        }
    }

    public void deleteAll() {
        FileSystemUtils.deleteRecursively(uploadFolder.toFile());
    }

    public void init() {
        try {
            Files.createDirectories(uploadFolder);
        }
        catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
    }
}
