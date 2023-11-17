package org.etamburini.mockitoapp.example.repositories;

import org.etamburini.mockitoapp.example.models.SchoolTest;

import java.util.List;

public interface TestRepository {
    List<SchoolTest> findAll();
}
