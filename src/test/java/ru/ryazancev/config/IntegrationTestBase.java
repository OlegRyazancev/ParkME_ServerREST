package ru.ryazancev.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;
import ru.ryazancev.config.initializer.Postgres;
import ru.ryazancev.parkingreservationsystem.util.exceptions.ResourceNotFoundException;

@ActiveProfiles("test")
@SpringBootTest
@ContextConfiguration(initializers = {
        Postgres.Initializer.class
})
@Transactional
public abstract class IntegrationTestBase {
    @BeforeAll
    static void init() {
        Postgres.container.start();
    }

    protected <T> T findObjectForTests(JpaRepository<T, Long> repository, Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource for tests not found"));
    }


}