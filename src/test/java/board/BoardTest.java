package board;

import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;

public class BoardTest {

  private final String key = "022025b4b327c076fa95079f685e018d";
  private final String token = "105026fec0fb815488115872acdcb02b00b430e6da013491a6f7d27ce23f7054";

  @Test
  public void createNewBoard(){

    Response response = given()
            .queryParam("key", key)
            .queryParam("token", token)
            .queryParam("name", "My second board")
            .contentType(ContentType.JSON)
            .when()
            .post("https://api.trello.com/1/boards/")
            .then()
            .statusCode(HttpStatus.SC_OK)
            .extract()
            .response();

    JsonPath json = response.jsonPath();

    Assertions.assertThat(json.getString("name")).isEqualTo("My second board");

    String boardId = json.get("id");

    given()
            .queryParam("key", key)
            .queryParam("token", token)
            .queryParam("name", "My second board")
            .contentType(ContentType.JSON)
            .when()
            .delete("https://api.trello.com/1/boards/" + boardId)
            .then()
            .statusCode(HttpStatus.SC_OK);
  }

  @Test    //wstawianie pustej warości w query param "name" i sprawdzanie statusu
  public void createBoardWithEmptyBoardName(){

    Response response = given()
            .queryParam("key", key)
            .queryParam("token", token)
            .queryParam("name", "")
            .contentType(ContentType.JSON)
            .when()
            .post("https://api.trello.com/1/boards/")
            .then()
            .statusCode(HttpStatus.SC_BAD_REQUEST)
            .extract()
            .response();
  }

  @Test
  public void createBoardWithoutDefaultList(){

    Response response = given()
            .queryParam("key", key)
            .queryParam("token", token)
            .queryParam("name", "Board without default lists")
            .queryParam("defaultLists", false)     // dodajemy domysle 3 listy które sa na false czyli ich nie dodajemy tak naprawde
            .contentType(ContentType.JSON)
            .when()
            .post("https://api.trello.com/1/boards/")
            .then()
            .statusCode(HttpStatus.SC_OK)
            .extract()
            .response();

    JsonPath json = response.jsonPath();

    Assertions.assertThat(json.getString("name")).isEqualTo("Board without default lists");

    String boardId = json.get("id");        // pobieramy id borda, potrzebne nam to do  wyszukiwania list które sie nie dodały

    Response responseList = given()
            .queryParam("key", key)
            .queryParam("token", token)
            .contentType(ContentType.JSON)
            .when()
            .get("https://api.trello.com/1/boards/" + boardId + "/lists")   // wyszykujemy listy których nie ma
            .then()
            .statusCode(HttpStatus.SC_OK)
            .extract()
            .response();

    JsonPath jsonlist = responseList.jsonPath();
    List<Object> idList = jsonlist.getList("id");    // twozyli liste po Id  listy

    Assertions.assertThat(idList).hasSize(0);

    given()
            .queryParam("key", key)
            .queryParam("token", token)
            .queryParam("name", "My second board")
            .contentType(ContentType.JSON)
            .when()
            .delete("https://api.trello.com/1/boards/" + boardId)
            .then()
            .statusCode(HttpStatus.SC_OK);
  }

  @Test // dodanie testu z 3 domyślnymi listami
  public void createBoardWithDefaultList() {

    Response response = given()
            .queryParam("key", key)
            .queryParam("token", token)
            .queryParam("name", "Board with default listsxxxx")
            .queryParam("defaultLists", true)     // dodajemy domysle 3 listy które sa na true
            .contentType(ContentType.JSON)
            .when()
            .post("https://api.trello.com/1/boards/")
            .then()
            .statusCode(HttpStatus.SC_OK)
            .extract()
            .response();

    JsonPath json = response.jsonPath();

    Assertions.assertThat(json.getString("name")).isEqualTo("Board with default listsxxxx");

    String boardId = json.get("id");        // pobieramy id borda, potrzebne nam to do  wyszukiwania list które sie nie dodały

    Response responseList = given()
            .queryParam("key", key)
            .queryParam("token", token)
            .contentType(ContentType.JSON)
            .when()
            .get("https://api.trello.com/1/boards/" + boardId + "/lists")   // wyszykujemy listy których nie ma
            .then()
            .statusCode(HttpStatus.SC_OK)
            .extract()
            .response();

  //  System.out.println(responseList.prettyPrint());  //  pobieranie list za pomoca pritty print

    JsonPath jsonlist = responseList.jsonPath();
    List<String> nameList = jsonlist.getList("name");

    Assertions.assertThat(nameList).hasSize(3).contains("To Do", "Doing", "Done");

    given()
            .queryParam("key", key)
            .queryParam("token", token)
            .queryParam("name", "My second board")
            .contentType(ContentType.JSON)
            .when()
            .delete("https://api.trello.com/1/boards/" + boardId)
            .then()
            .statusCode(HttpStatus.SC_OK);
  }
}
