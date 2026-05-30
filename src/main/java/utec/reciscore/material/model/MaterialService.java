package utec.reciscore.material.model;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
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
    private final ModelMapper modelMapper;

    public MaterialResponse create(MaterialRequest request) {
        if (materialRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Ya existe un material con ese nombre.");
        }
        Material material = modelMapper.map(request, Material.class);
        return modelMapper.map(materialRepository.save(material), MaterialResponse.class);
    }

    public List<MaterialResponse> getAll() {
        return materialRepository.findAll()
                .stream()
                .map(m -> modelMapper.map(m, MaterialResponse.class))
                .toList();
    }

    public MaterialResponse getById(Long id) {
        Material material = materialRepository.findById(id)
                .orElseThrow(NoSuchElementException::new);
        return modelMapper.map(material, MaterialResponse.class);
    }

    public List<MaterialResponse> getByCategory(TipoMaterial category) {
        return materialRepository.findByCategory(category)
                .stream()
                .map(m -> modelMapper.map(m, MaterialResponse.class))
                .toList();
    }
}