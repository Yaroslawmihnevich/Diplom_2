import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.BeforeClass;

import java.io.File;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;

public abstract class BaseTest {

    protected static final String CREATE_NEW_USER_JSON = "src/test/resources/create_user/new_user.json";
    protected static final String CREATE_INVALID_USER_JSON = "src/test/resources/create_user/missing_required_field.json";
    protected static final String LOGIN_VALID_USER_JSON = "src/test/resources/login_user/existing_user.json";
    protected static final String LOGIN_INVALID_USER_JSON = "src/test/resources/login_user/invalid_user.json";
    protected static final String UPDATE_USERNAME_JSON = "src/test/resources/update_user/update_name.json";
    protected static final String UPDATE_EMAIL_JSON = "src/test/resources/update_user/update_email.json";

    protected String accessToken;

    @BeforeClass
    public static void beforeClass() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }

    protected Response login() {
        return login(true);
    }

    protected Response login(boolean showLog) {
        File json = new File(LOGIN_VALID_USER_JSON);

        Response response = given().contentType("application/json")
                .body(json)
                .when()
                .post("/api/auth/login");
        if (showLog) {
            response.then().log().body();
        }
        return response;
    }

    protected Response register() {
        return register(true);
    }

    protected Response register(boolean showLog) {
        File json = new File(CREATE_NEW_USER_JSON);

        Response response = given().contentType("application/json")
                .body(json)
                .when()
                .post("/api/auth/register");
        if (showLog) {
            response.then().log().body();
        }
        return response;
    }

    protected boolean deleteUser() {
        return deleteUser(true);
    }

    protected boolean deleteUser(boolean showLog) {
        if (accessToken == null) {
            return false;
        }
        Response response = given().auth().oauth2(accessToken)
                .when()
                .delete("/api/auth/user");
        if (showLog) {
            response.then().log().body();
        }
        return response.statusCode() == 202;
    }

    protected String extractToken(Response response) {
        String temp = response.path("accessToken");
        return temp.substring(7);
    }

    protected String getToken() {
        return getToken(true);
    }

    protected String getToken(boolean showLog) {
        var response = login(showLog);
        if (response.statusCode() == 200) {
            return extractToken(response);
        }
        response = register(showLog);
        if (response.statusCode() == 200) {
            return extractToken(response);
        }
        throw new IllegalStateException("Невозможно авторизоваться и зарегистрироваться.");
    }

    protected MenuDto getMenu() {

        return when()
                .get("/api/ingredients")
                .then()
                .extract().response()
                .as(MenuDto.class);
    }
}
