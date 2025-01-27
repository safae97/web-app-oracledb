package test_classes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestEntityService {

    @Autowired
    private TestEntityRepository repository;

    // Method to save a new TestEntity
    public TestEntity saveTestEntity(String name) {
        TestEntity entity = new TestEntity();
        entity.setName(name);
        return repository.save(entity);
    }

    // Method to retrieve all TestEntity records
    public Iterable<TestEntity> getAllTestEntities() {
        return repository.findAll();
    }
}

