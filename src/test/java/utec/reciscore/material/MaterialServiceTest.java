package utec.reciscore.material;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import utec.reciscore.material.dto.MaterialRequest;
import utec.reciscore.material.dto.MaterialResponse;
import utec.reciscore.material.infrastructure.MaterialRepository;
import utec.reciscore.material.model.Material;
import utec.reciscore.material.model.MaterialService;
import utec.reciscore.material.model.TipoMaterial;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MaterialServiceTest {
    @Mock private MaterialRepository materialRepository;
    @InjectMocks private MaterialService materialService;

    private Material material;
    private MaterialRequest request;

    @BeforeEach
    void setUp() {
        material = Material.builder().id(1L).name("Botella PET")
                .pointsPerKg(10.0).weight(0.5).category(TipoMaterial.PLASTICO).recyclable(true).build();

        request = new MaterialRequest();
        request.setName("Botella PET");
        request.setPointsPerKg(10.0);
        request.setWeight(0.5);
        request.setCategory(TipoMaterial.PLASTICO);
        request.setRecyclable(true);
    }

    @Test
    void shouldCreateMaterialWhenNameIsUnique() {
        when(materialRepository.existsByName(anyString())).thenReturn(false);
        when(materialRepository.save(any())).thenReturn(material);

        MaterialResponse response = materialService.create(request);

        assertNotNull(response);
        assertEquals("Botella PET", response.getName());
        verify(materialRepository).save(any());
    }

    @Test
    void shouldThrowExceptionWhenMaterialNameAlreadyExists() {
        when(materialRepository.existsByName(anyString())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> materialService.create(request));
        verify(materialRepository, never()).save(any());
    }

    @Test
    void shouldReturnMaterialWhenIdExists() {
        when(materialRepository.findById(1L)).thenReturn(Optional.of(material));

        MaterialResponse response = materialService.getById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void shouldThrowExceptionWhenMaterialIdDoesNotExist() {
        when(materialRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> materialService.getById(99L));
    }

    @Test
    void shouldReturnAllMaterialsWhenRepositoryIsNotEmpty() {
        when(materialRepository.findAll()).thenReturn(List.of(material));

        List<MaterialResponse> response = materialService.getAll();

        assertEquals(1, response.size());
        assertEquals("Botella PET", response.get(0).getName());
    }

    @Test
    void shouldReturnMaterialsFilteredWhenCategoryMatches() {
        when(materialRepository.findByCategory(TipoMaterial.PLASTICO)).thenReturn(List.of(material));

        List<MaterialResponse> response = materialService.getByCategory(TipoMaterial.PLASTICO);

        assertEquals(1, response.size());
        assertEquals(TipoMaterial.PLASTICO, response.get(0).getCategory());
    }
}