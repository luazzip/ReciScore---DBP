package utec.reciscore.upload.application;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import utec.reciscore.upload.dto.UploadResponseDTO;
import utec.reciscore.upload.model.UploadService;

@RestController
@RequestMapping("/upload")
@RequiredArgsConstructor
public class UploadController {

    private final UploadService uploadService;

    @PostMapping
    public ResponseEntity<UploadResponseDTO> subirImagen(
            @RequestParam("file") MultipartFile file) {
        String url = uploadService.subirImagen(file);
        return ResponseEntity.ok(new UploadResponseDTO(url));
    }
}
