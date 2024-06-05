import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class CreateOrderTest extends BaseTest {

    @After
    public void tearDown() {
        deleteUser();
    }

    @Test
    @DisplayName("Создание заказа без авторизации с ингредиентами.")
    public void createOrder_unauthorizedWithIngredients() {
        var menu = getMenu();
        var bun = menu.getData().stream().filter(x -> x.getType().equals("bun")).findFirst().get();
        var main = menu.getData().stream().filter(x -> x.getType().equals("main")).findFirst().get();
        var ingredients = Map.of("ingredients", List.of(bun, main));

        given().contentType("application/json")
                .body(ingredients)
                .when()
                .post("/api/orders")
                .then()
                .log().body()
                .statusCode(200)
                .and()
                .body("success", equalTo(true))
                .body("name", notNullValue())
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа с авторизацией с ингредиентами.")
    public void createOrder_authorizedWithIngredients() {
        var menu = getMenu();
        var bun = menu.getData().stream().filter(x -> x.getType().equals("bun")).findFirst().get();
        var main = menu.getData().stream().filter(x -> x.getType().equals("main")).findFirst().get();
        var ingredients = Map.of("ingredients", List.of(bun.getId(), main.getId()));
        accessToken = getToken();

        given().contentType("application/json")
                .auth().oauth2(accessToken)
                .body(ingredients)
                .when()
                .post("/api/orders")
                .then()
                .log().body()
                .statusCode(200)
                .and()
                .body("success", equalTo(true))
                .body("name", notNullValue())
                .body("order.ingredients._id", hasItems(bun.getId(), main.getId()))
                .body("order.owner.name", equalTo("john_doe"))
                .body("order.status", equalTo("done"))
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа с авторизацией без ингредиентов.")
    public void createOrder_authorizedWithoutIngredients() {
        accessToken = getToken();

        given().contentType("application/json")
                .auth().oauth2(accessToken)
                .when()
                .post("/api/orders")
                .then()
                .log().body()
                .and()
                .assertThat()
                .statusCode(400)
                .and()
                .assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с авторизацией с неверным хешем ингредиентов.")
    public void createOrder_invalidIngredientsHash() {
        var ingredients = Map.of("ingredients", List.of("11dfac0c5a71d1f82001bdaaa6f"));
        accessToken = getToken();

        given().contentType("application/json")
                .auth().oauth2(accessToken)
                .body(ingredients)
                .when()
                .post("/api/orders")
                .then()
                .log().body()
                .and()
                .assertThat()
                .statusCode(500);
    }
}
