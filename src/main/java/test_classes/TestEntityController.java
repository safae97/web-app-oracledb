package test_classes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
@RestController
public class TestEntityController {

    @Autowired
    private TestEntityService service;

    @PostMapping("/test-connection")
    public TestEntity testConnection(@RequestParam String name) {
        return service.saveTestEntity(name);  // Save a new TestEntity to the database
    }

    @GetMapping("/test-entities")
    public Iterable<TestEntity> getTestEntities() {
        return service.getAllTestEntities();  // Retrieve all TestEntities from the database
    }
}
