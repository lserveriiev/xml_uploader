package uploader.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("storage")
public class StorageProperties {

    private String uploadFolder;

    private String demoFile;

    public String getUploadFolder() {
        return uploadFolder;
    }

    public void setUploadFolder(String uploadFolder) {
        this.uploadFolder = uploadFolder;
    }

    public String getDemoFile() {
        return demoFile;
    }

    public void setDemoFile(String demoFile) {
        this.demoFile = demoFile;
    }
}
