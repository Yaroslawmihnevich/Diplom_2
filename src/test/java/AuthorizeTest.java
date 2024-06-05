import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class AuthorizeTest extends BaseTest {

    @Before
    public void setUp() {
        accessToken = getToken();
    }

    @After
    public void tearDown() {
        deleteUser();
    }

    @Test
    @DisplayName("Логин под существующим пользователем.")
    public void login_success() {
        var json = new File(LOGIN_VALID_USER_JSON);

        given().contentType("application/json")
                .body(json)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(200)
                .and()
                .log().body()
                .body("success", equalTo(true))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue())
                .body("user.email", equalTo("john_doe@delta.ru"))
                .body("user.name", equalTo("john_doe"));
    }

    @Test
    @DisplayName("Логин с неверным логином и паролем.")
    public void login_invalidCredentials() {
        File json = new File(LOGIN_INVALID_USER_JSON);

        given().contentType("application/json")
                .body(json)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }
}
