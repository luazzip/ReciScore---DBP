package utec.reciscore.upload.model;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import utec.reciscore.exceptions.UploadException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UploadService {

    private final Cloudinary cloudinary;

    private static final List<String> ALLOWED_CONTENT_TYPES =
            List.of("image/jpeg", "image/png", "image/jpg", "image/webp");
    private static final long MAX_SIZE_BYTES = 8 * 1024 * 1024; // 8MB

    public String subirImagen(MultipartFile file) {
        validar(file);
        try {
            Map<?, ?> resultado = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap("folder", "reciscore/reciclajes")
            );
            return (String) resultado.get("secure_url");
        } catch (IOException e) {
            throw new UploadException("No se pudo subir la imagen. Intenta de nuevo.");
        }
    }

    private void validar(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new UploadException("Debes adjuntar una foto.");
        }
        if (file.getSize() > MAX_SIZE_BYTES) {
            throw new UploadException("La foto es muy pesada (máximo 8MB).");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new UploadException("Formato de imagen no soportado. Usa JPG, PNG o WEBP.");
        }
    }
}