package utec.reciscore.Material.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import utec.reciscore.Material.infraestructure.MaterialRepository;

@Service
public class MaterialService {
    @Autowired
    private MaterialRepository materialRepository;
}
