package utec.reciscore.material.model;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import utec.reciscore.material.dto.MaterialRequest;
import utec.reciscore.material.dto.MaterialResponse;
import utec.reciscore.material.infrastructure.MaterialRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class MaterialService {

    private final MaterialRepository materialRepository;

    public MaterialResponse create(MaterialRequest request) {
        if (materialRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Ya existe un material con ese nombre.");
        }
        Material material = Material.builder()
                .name(request.getName())
                .pointsPerKg(request.getPointsPerKg())
                .weight(request.getWeight())
                .category(request.getCategory())
                .recyclable(request.getRecyclable())
                .build();
        return toResponse(materialRepository.save(material));
    }

    public List<MaterialResponse> getAll() {
        return materialRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public MaterialResponse getById(Long id) {
        Material material = materialRepository.findById(id)
                .orElseThrow(NoSuchElementException::new);
        return toResponse(material);
    }

    public List<MaterialResponse> getByCategory(TipoMaterial category) {
        return materialRepository.findByCategory(category)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private MaterialResponse toResponse(Material material) {
        return new MaterialResponse(
                material.getId(),
                material.getName(),
                material.getPointsPerKg(),
                material.getWeight(),
                material.getCategory(),
                material.getRecyclable()
        );
    }
}