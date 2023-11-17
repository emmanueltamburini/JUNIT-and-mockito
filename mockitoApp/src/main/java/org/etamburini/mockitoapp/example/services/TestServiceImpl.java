package org.etamburini.mockitoapp.example.services;

import org.etamburini.mockitoapp.example.models.SchoolTest;
import org.etamburini.mockitoapp.example.repositories.TestRepository;

import java.util.Optional;

public class TestServiceImpl implements TestService {
    private TestRepository testRepository;

    public TestServiceImpl(TestRepository testRepository) {
        this.testRepository = testRepository;
    }

    @Override
    public SchoolTest getTestByName(String name) {
        final Optional<SchoolTest> testOptional = testRepository.findAll().stream().filter(schoolTest -> schoolTest.getName().contains(name))
                .findFirst();

        return testOptional.orElseThrow();
    }
}
