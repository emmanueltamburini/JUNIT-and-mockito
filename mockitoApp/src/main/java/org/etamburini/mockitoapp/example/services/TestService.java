package org.etamburini.mockitoapp.example.services;

import org.etamburini.mockitoapp.example.models.SchoolTest;

public interface TestService {
    SchoolTest getTestByName(final String name);
}
