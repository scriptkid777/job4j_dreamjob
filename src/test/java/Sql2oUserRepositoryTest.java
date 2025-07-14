import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sql2o.Sql2o;
import ru.job4j.dreamjob.configuration.DatasourceConfiguration;
import ru.job4j.dreamjob.model.User;
import ru.job4j.dreamjob.repository.Sql2oUserRepository;

import java.io.IOException;
import java.util.Properties;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class Sql2oUserRepositoryTest {
    private static Sql2oUserRepository sql2oUserRepository;

    private static Sql2o sql2o;

    @BeforeAll
    public static void initRepositories() throws IOException {
        var properties = new Properties();
        try (var inputStream = Sql2oCandidateRepositoryTest.class.getClassLoader().getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");
        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        sql2o = configuration.databaseClient(datasource);
        sql2oUserRepository = new Sql2oUserRepository(sql2o);
    }

    @AfterEach
    public void clearUsers() {
        try (var connection = sql2o.open()) {
            connection.createQuery("DELETE FROM users").executeUpdate();
        }
    }

    @Test
    public void whenSaveThenGetSame() {
        var user = sql2oUserRepository.save(new User(0, "@mail", "Name", "qwerty"));
        var savedUser = sql2oUserRepository.findByEmailAndPassword("@mail", "qwerty");
        assertThat(savedUser).usingRecursiveComparison().isEqualTo(user);
    }

    @Test
    public void whenSaveTwoUserThenGetSame() {
        var user1 = sql2oUserRepository.save(new User(0, "1@mail.ru", "1Name", "1qwerty"));
        var user2 = sql2oUserRepository.save(new User(0, "2@mail.ru", "2Name", "2qwerty"));
        var savedUser1 = sql2oUserRepository.findByEmailAndPassword("1@mail.ru", "1qwerty");
        var savedUser2 = sql2oUserRepository.findByEmailAndPassword("2@mail.ru", "2qwerty");
        assertThat(savedUser1).usingRecursiveComparison().isEqualTo(user1);
        assertThat(savedUser2).usingRecursiveComparison().isEqualTo(user2);
    }

    @Test
    public void whenSaveSameEmailThenNothingSave() {
        var user1 = sql2oUserRepository.save(new User(0, "@mail", "1Name", "1qwerty"));
        var user2 = sql2oUserRepository.save(new User(0, "@mail", "2Name", "2qwerty"));
        var savedUser1 = sql2oUserRepository.findByEmailAndPassword("@mail", "1qwerty");
        var savedUser2 = sql2oUserRepository.findByEmailAndPassword("@mail", "2qwerty");
        assertThat(savedUser1).isPresent();
        assertThat(savedUser2).isEmpty();
    }

}
