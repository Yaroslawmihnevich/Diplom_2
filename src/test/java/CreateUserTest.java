import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

public class CreateUserTest extends BaseTest {

    @Before
    public void setUp() {
        accessToken = getToken();
        deleteUser();
    }

    @After
    public void tearDown() {
        deleteUser();
    }

    @Test
    @DisplayName("Создание уникального пользователя.")
    public void createUser_success() {
        var json = new File(CREATE_NEW_USER_JSON);

        accessToken = given().contentType("application/json")
                .body(json)
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(200)
                .extract()
                .path("accessToken");

        accessToken = accessToken.substring(7);
    }

    @Test
    @DisplayName("Создание пользователя, который уже зарегистрирован.")
    public void createUser_alreadyExists() {
        createUser_success();

        var json = new File(CREATE_NEW_USER_JSON);

        given().contentType("application/json")
                .body(json)
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Создание пользователя и не заполнить одно из обязательных полей.")
    public void createUser_requiredFieldMissing() {
        var json = new File(CREATE_INVALID_USER_JSON);

        given().contentType("application/json")
                .body(json)
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }
}
