package base;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;

public class BaseTest {

    protected final static String BASE_URL = "https://api.trello.com/1";
    protected final static String BOARDS = "boards";
    protected final static String LISTS = "lists";
    protected final static String CARDS = "cards";

    protected final static String KEY = "022025b4b327c076fa95079f685e018d";
    protected final static String TOKEN = "105026fec0fb815488115872acdcb02b00b430e6da013491a6f7d27ce23f7054";

    protected static RequestSpecBuilder reqBuilder;
    protected static RequestSpecification reqSpec;

    @BeforeAll
    public static void beforeAll(){

        reqBuilder = new RequestSpecBuilder();
        reqBuilder.addQueryParam("key", KEY);
        reqBuilder.addQueryParam("token", TOKEN);
        reqBuilder.setContentType(ContentType.JSON);

        reqSpec = reqBuilder.build();
    }

}
