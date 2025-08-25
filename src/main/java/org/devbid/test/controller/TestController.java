package org.devbid.test.controller;

import org.devbid.test.entity.TestEntity;
import org.devbid.test.repository.TestRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tests")
public class TestController {

    private final TestRepository repo;

    public TestController(TestRepository repo) {
        this.repo = repo;
    }

    @PostMapping
    public TestEntity create(@RequestParam String name) {
        TestEntity t = new TestEntity();
        t.setName(name);
        return repo.save(t);
    }

    @GetMapping
    public List<TestEntity> all() {
        return repo.findAll();
    }
}
