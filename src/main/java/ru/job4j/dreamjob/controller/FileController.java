package ru.job4j.dreamjob.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.job4j.dreamjob.service.FileService;
@RestController
@RequestMapping("/files")
public class FileController {
    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable int id) {
        var contentOptional = fileService.getFileById(id);
        if (contentOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
       return ResponseEntity.ok(contentOptional.get().getContent());
    }

}
