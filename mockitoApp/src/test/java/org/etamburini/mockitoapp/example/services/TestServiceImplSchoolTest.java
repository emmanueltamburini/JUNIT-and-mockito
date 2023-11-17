package org.etamburini.mockitoapp.example.services;

import org.etamburini.mockitoapp.example.models.SchoolTest;
import org.etamburini.mockitoapp.example.repositories.TestRepository;
import org.etamburini.mockitoapp.example.repositories.TestRepositoryImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestServiceImplSchoolTest {

    @Test
    void getTestByName() {
        final TestRepository repository = new TestRepositoryImpl();
        final TestService service = new TestServiceImpl(repository);
        final SchoolTest test = service.getTestByName("Math");

        assertNotNull(test);
        assertEquals(5L, test.getId());
    }
}