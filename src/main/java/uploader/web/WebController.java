package uploader.web;

import java.io.File;
import java.io.IOException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import uploader.data.Xml;
import uploader.service.StorageDemoService;
import uploader.service.StorageService;
import uploader.service.XmlReaderService;
import uploader.exception.StorageFileNotFoundException;

@Controller
@RequestMapping("/")
public class WebController {

    private final StorageService storageService;
    private final StorageDemoService storageDemoService;
    private final XmlReaderService xmlReaderService;

    @Autowired
    public WebController(StorageService storageService, StorageDemoService storageDemoService, XmlReaderService xmlReaderService) {
        this.storageService = storageService;
        this.storageDemoService = storageDemoService;
        this.xmlReaderService = xmlReaderService;
    }

    @GetMapping()
    public String listUploadedFiles(Model model) {
        model.addAttribute("files", storageService.loadAll().map(
                path -> path.getFileName().toString()
        )
        .collect(Collectors.toList()));

        model.addAttribute("demoFile", MvcUriComponentsBuilder.fromMethodName(WebController.class,"downloadDemoFile").build());

        return "list";
    }

    @GetMapping("/uploadFile")
    public String uploadForm() {
        return "uploadFile";
    }

    @GetMapping("/files/download/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {
        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @GetMapping("/files/view/{filename:.+}")
    public String viewFile(@PathVariable String filename, Model model) throws IOException {
        File file = storageService.loadAsResource(filename).getFile();
        Xml xml = xmlReaderService.loadXml(file);

        model.addAttribute("xml", xml);
        model.addAttribute("filename", filename);

        return "view";
    }

    @GetMapping("/download_demo_file")
    @ResponseBody
    public ResponseEntity<Resource> downloadDemoFile() {
        Resource resource = storageDemoService.loadResource();

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + resource.getFilename() + "\"").body(resource);
    }

    @PostMapping("/uploadFile")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        try {
            storageService.store(file);

            return "redirect:/";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }

        return "uploadFile";
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }
}
