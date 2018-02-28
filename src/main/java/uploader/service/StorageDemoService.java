package uploader.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import uploader.properties.StorageProperties;
import uploader.exception.StorageFileNotFoundException;

import java.io.File;

@Service
public class StorageDemoService {

    private final String demoFile;

    @Autowired
    public StorageDemoService(StorageProperties properties) {
        this.demoFile = properties.getDemoFile();
    }

    public Resource loadResource() {
        File file = getFile();
        Resource resource = new FileSystemResource(file);

        if (resource.exists() || resource.isReadable()) {
            return resource;
        } else {
            throw new StorageFileNotFoundException(
                    "Could not read file: " + this.demoFile);
        }
    }

    private File getFile() {
        ClassLoader classLoader = getClass().getClassLoader();

        return new File(classLoader.getResource(this.demoFile).getFile());
    }
}
