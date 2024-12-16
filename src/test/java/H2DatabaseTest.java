import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class H2DatabaseTest {

    @Autowired
    private EntityManager entityManager;

    @Test
    void h2DatabaseConnectionTest() {
        String result = (String) entityManager.createNativeQuery("SELECT 1").getSingleResult();
        assertThat(result).isEqualTo("1");
    }
}
