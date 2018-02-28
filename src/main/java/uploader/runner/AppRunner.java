package uploader.runner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import uploader.service.StorageService;

@Component
public class AppRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(AppRunner.class);

    private final StorageService storageService;

    public AppRunner(StorageService storageService) {
        this.storageService = storageService;
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("------- Delete all files -------");
        storageService.deleteAll();
        logger.info("------- Create upload dir -------");
        storageService.init();
    }
}
