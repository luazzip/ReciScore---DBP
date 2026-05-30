package utec.reciscore.material;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import utec.reciscore.material.infrastructure.MaterialRepository;
import utec.reciscore.material.model.Material;
import utec.reciscore.material.model.TipoMaterial;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class MaterialRepositoryTest {
    @Autowired
    private MaterialRepository materialRepository;

    @BeforeEach
    void setUp() {
        Material plastico = Material.builder()
                .name("Botella PET")
                .pointsPerKg(10.0)
                .weight(0.5)
                .category(TipoMaterial.PLASTICO)
                .recyclable(true)
                .build();

        Material vidrio = Material.builder()
                .name("Botella de Vidrio")
                .pointsPerKg(5.0)
                .weight(0.8)
                .category(TipoMaterial.VIDRIO)
                .recyclable(true)
                .build();

        materialRepository.save(plastico);
        materialRepository.save(vidrio);
    }

    @Test
    void shouldReturnMaterialsWhenCategoryMatches() {
        List<Material> result = materialRepository.findByCategory(TipoMaterial.PLASTICO);
        assertEquals(1, result.size());
        assertEquals("Botella PET", result.get(0).getName());
    }

    @Test
    void shouldReturnEmptyListWhenCategoryHasNoMaterials() {
        List<Material> result = materialRepository.findByCategory(TipoMaterial.METAL);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnTrueWhenMaterialNameExists() {
        boolean exists = materialRepository.existsByName("Botella PET");
        assertTrue(exists);
    }

    @Test
    void shouldReturnFalseWhenMaterialNameDoesNotExist() {
        boolean exists = materialRepository.existsByName("Cartón");
        assertFalse(exists);
    }

    @Test
    void shouldSaveAndFindMaterialById() {
        Material papel = Material.builder()
                .name("Periódico")
                .pointsPerKg(3.0)
                .weight(0.3)
                .category(TipoMaterial.PAPEL)
                .recyclable(true)
                .build();
        Material saved = materialRepository.save(papel);
        assertTrue(materialRepository.findById(saved.getId()).isPresent());
    }
}