import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.dreamjob.controller.UserController;
import ru.job4j.dreamjob.model.User;
import ru.job4j.dreamjob.service.UserService;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {
    private UserService userService;
    private UserController controller;

    @BeforeEach
    void init() {
        userService = mock(UserService.class);
        controller = new UserController(userService);
    }

    @Test
    void whenRequestRegistrationPageThenGetRegistrationPage() {
        var view = controller.getRegistrationPage();
        assertThat(view).isEqualTo("users/register");
    }

    @Test
    void whenRequestLoginPageThenGetLoginPage() {
        var view = controller.getLoginPage();
        assertThat(view).isEqualTo("users/login");
    }

    @Test
    void whenPostUserRegisterThenSuccessAndRedirectToVacancies() {
        var user = new User(1, "test@mail.ru", "name", "qwerty");
        var userArgumentCaptor = ArgumentCaptor.forClass(User.class);

        when(userService.save(userArgumentCaptor.capture())).thenReturn(Optional.of(user));

        var model = new ConcurrentModel();
        var view = controller.register(user, model);
        var actualUser = userArgumentCaptor.getValue();

        assertThat(view).isEqualTo("redirect:/vacancies");
        assertThat(actualUser).usingRecursiveComparison().isEqualTo(user);
    }

    @Test
    void whenPostUstRegisterThenNotSuccessAndGetErrorPageWithMessage() {
        var expectedMessage = "Пользователь с такой почтой уже существует";

        when(userService.save(any(User.class))).thenReturn(Optional.empty());

        var model = new ConcurrentModel();
        var view = controller.register(new User(), model);
        var actualMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    @Test
    void whenPostLoginUserThenSuccessAndRedirectToVacancies() {
        var user = new User(1, "test@mail.ru", "name", "qwerty");

        when(userService.findByEmailAndPassword(user.getEmail(), user.getPassword())).thenReturn(Optional.of(user));

        var model = new ConcurrentModel();
        var httpRequest = new MockHttpServletRequest();
        var view = controller.loginUser(user, model, httpRequest);
        var session = httpRequest.getSession();

        var actualUser = session.getAttribute("user");

        assertThat(view).isEqualTo("redirect:/vacancies");
        assertThat(actualUser).usingRecursiveComparison().isEqualTo(user);
    }

    @Test
    void whenPostLoginUserThenNotSuccessAndGetLoginPageWithMessage() {
        var expectedMessage = "Почта или пароль введены неверно";

        when(userService.findByEmailAndPassword(any(String.class), any(String.class))).thenReturn(Optional.empty());

        var model = new ConcurrentModel();
        var httpRequest = new MockHttpServletRequest();
        var view = controller.loginUser(new User(), model, httpRequest);
        var actualMessage = model.getAttribute("error");

        assertThat(view).isEqualTo("users/login");
        assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    @Test
    void whenRequestLogoutThenClearSessionAndRedirectToLoginPage() {
        var session = new MockHttpSession();
        var view = controller.logout(session);
        assertThat(view).isEqualTo("redirect:/users/login");
        assertThat(session.isInvalid()).isTrue();
    }
}
