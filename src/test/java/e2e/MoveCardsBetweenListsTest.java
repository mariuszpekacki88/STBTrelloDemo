package e2e;
import base.BaseTest;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MoveCardsBetweenListsTest extends BaseTest {

    private static String boardId;
    private static String firstListId;
    private static String secondListId;
    private static String idCard;

    @Test
    @Order(1)
    public void createNewBoard() {

        Response response = given()
                .spec(reqSpec)
                .queryParam("name", "My e2e board")
                .queryParam("defaultLists", false)
                .when()
                .post(BASE_URL + "/" + BOARDS)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();

        JsonPath json = response.jsonPath();

        Assertions.assertThat(json.getString("name")).isEqualTo("My e2e board");

        boardId = json.getString("id");
    }

    @Test
    @Order(2)
    public void createFirstList() {

        Response response = given()
                .spec(reqSpec)
                .queryParam("name", "My first list")
                .when()
                .post(BASE_URL + "/" + BOARDS + "/" + boardId + "/" + LISTS)
                .then()
                .statusCode(200)
                .extract()
                .response();

        JsonPath json = response.jsonPath();

        Assertions.assertThat(json.getString("name")).isEqualTo("My first list");

        firstListId = json.getString("id");

    }

    @Test
    @Order(3)
    public void createSecondList() {

        Response response = given()
                .spec(reqSpec)
                .queryParam("name", "My second list")
                .when()
                .post(BASE_URL + "/" + BOARDS + "/" + boardId + "/" + LISTS)
                .then()
                .statusCode(200)
                .extract()
                .response();

        JsonPath json = response.jsonPath();

        Assertions.assertThat(json.getString("name")).isEqualTo("My second list");

        secondListId = json.getString("id");
    }

    @Test
    @Order(4)
    public void addCardToFirstList(){

        Response response = given()
                .spec(reqSpec)
                .queryParam("idList", firstListId)
                .queryParam("name", "My e2e car")
                .when()
                .post(BASE_URL + "/" + CARDS)
                .then()
                .statusCode(200)
                .extract()
                .response();

        JsonPath json = response.jsonPath();

        Assertions.assertThat(json.getString("name")).isEqualTo("My e2e car");

        idCard = json.getString("id");
    }

    @Test
    @Order(5)
    public void moveCardToSecondList() {

        Response response = given()
                .spec(reqSpec)
                .queryParam("idList", secondListId)
                .when()
                .put(BASE_URL + "/" + CARDS + "/" + idCard)
                .then()
                .statusCode(200)
                .extract()
                .response();

        JsonPath json = response.jsonPath();

        Assertions.assertThat(json.getString("idList")).isEqualTo(secondListId);
        Assertions.assertThat(json.getString("name")).isEqualTo("My e2e car");
    }

    @Test
    @Order(6)
    public void deleteBoard() {

        given()
                .spec(reqSpec)
                .when()
                .delete(BASE_URL + "/" + BOARDS + "/" + boardId)
                .then()
                .statusCode(200);
    }
}