import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

public class UpdateUserTest extends BaseTest {

    @Before
    public void setUp() {
        accessToken = getToken();
    }

    @After
    public void tearDown() {
        deleteUser();
    }

    @Test
    @DisplayName("Изменение имени пользователя с авторизацией.")
    public void updateUserName_success() {
        File json = new File(UPDATE_USERNAME_JSON);

        given().contentType("application/json")
                .auth().oauth2(accessToken)
                .and()
                .body(json)
                .when()
                .patch("/api/auth/user")
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .body("success", equalTo(true))
                .body("user.email", equalTo("john_doe@delta.ru"))
                .body("user.name", equalTo("samuel_doe"))
                .log().body();
    }

    @Test
    @DisplayName("Изменение пароля пользователя с авторизацией.")
    public void updateUserPassword_success() {
        File json = new File(UPDATE_EMAIL_JSON);

        given().contentType("application/json")
                .auth().oauth2(accessToken)
                .and()
                .body(json)
                .when()
                .patch("/api/auth/user")
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .body("success", equalTo(true))
                .body("user.email", equalTo("samuel_doe@delta.ru"))
                .body("user.name", equalTo("john_doe"))
                .log().body();
    }

    @Test
    @DisplayName("Изменение имени пользователя без авторизации.")
    public void updateUserNameNotAuthorized_error() {
        File json = new File(UPDATE_USERNAME_JSON);

        given().contentType("application/json")
                .and()
                .body(json)
                .when()
                .patch("/api/auth/user")
                .then()
                .assertThat()
                .statusCode(401)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("You should be authorised"))
                .and()
                .log().body();
    }
}
