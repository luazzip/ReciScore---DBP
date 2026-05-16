package utec.reciscore.material.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import utec.reciscore.material.model.MaterialService;

@RestController
public class MaterialController {
    @Autowired
    private MaterialService materialService;
}
