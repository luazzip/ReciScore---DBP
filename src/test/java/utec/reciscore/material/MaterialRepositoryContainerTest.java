package utec.reciscore.material;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import utec.reciscore.material.infrastructure.MaterialRepository;
import utec.reciscore.material.model.Material;
import utec.reciscore.material.model.TipoMaterial;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MaterialRepositoryContainerTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("reciscore_test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MaterialRepository materialRepository;

    @BeforeEach
    void setUp() {
        materialRepository.deleteAll();

        materialRepository.save(Material.builder()
                .name("Botella PET")
                .pointsPerKg(10.0)
                .weight(0.5)
                .category(TipoMaterial.PLASTICO)
                .recyclable(true)
                .build());

        materialRepository.save(Material.builder()
                .name("Lata de aluminio")
                .pointsPerKg(8.0)
                .weight(0.2)
                .category(TipoMaterial.METAL)
                .recyclable(true)
                .build());
    }

    @Test
    void shouldReturnMaterialsWhenCategoryMatchesInPostgres() {
        List<Material> result = materialRepository.findByCategory(TipoMaterial.PLASTICO);
        assertEquals(1, result.size());
        assertEquals("Botella PET", result.get(0).getName());
    }

    @Test
    void shouldReturnTrueWhenMaterialNameExistsInPostgres() {
        assertTrue(materialRepository.existsByName("Botella PET"));
    }

    @Test
    void shouldReturnEmptyWhenCategoryHasNoMaterialsInPostgres() {
        List<Material> result = materialRepository.findByCategory(TipoMaterial.VIDRIO);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldPersistAndRetrieveMaterialByIdInPostgres() {
        Material saved = materialRepository.save(Material.builder()
                .name("Periódico")
                .pointsPerKg(3.0)
                .weight(0.3)
                .category(TipoMaterial.PAPEL)
                .recyclable(true)
                .build());
        assertTrue(materialRepository.findById(saved.getId()).isPresent());
    }

    @Test
    void shouldDeleteMaterialAndNotFindItInPostgres() {
        Material m = materialRepository.findAll().get(0);
        materialRepository.deleteById(m.getId());
        assertFalse(materialRepository.findById(m.getId()).isPresent());
    }
}