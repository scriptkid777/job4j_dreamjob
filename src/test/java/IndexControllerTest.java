import org.junit.jupiter.api.Test;
import ru.job4j.dreamjob.controller.IndexController;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class IndexControllerTest {
    @Test
    void whenGetIndexThenGetIndexPage() {
        var controller = new IndexController();
        var view = controller.getIndex();
        assertThat(view).isEqualTo("index");
    }
}
