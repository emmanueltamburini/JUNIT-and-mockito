package org.etamburini.mockitoapp.example.repositories;

import org.etamburini.mockitoapp.example.models.SchoolTest;

import java.util.Arrays;
import java.util.List;

public class TestRepositoryImpl implements  TestRepository{
    @Override
    public List<SchoolTest> findAll() {
        return Arrays.asList(
                new SchoolTest(5L, "Math"),
                new SchoolTest(2L, "Languages"),
                new SchoolTest(3L, "History")
        );
    }
}
