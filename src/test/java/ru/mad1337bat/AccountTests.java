package ru.mad1337bat;

import org.junit.jupiter.api.Test;
import ru.mad1337bat.uploadimg.BaseTest;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class AccountTests extends BaseTest {



    @Test
    void getAccountInfoTest() {
        given()
               // .headers(headers)
                .headers("Authorization", token)
//                .log()
//                .all() //.method()  //.uri()   //в зависимости от того, что требуется отобразить при тесте
                .when()
//                .get("/account/" + username)
                .get("/account/{username}", username)
                .prettyPeek()
                .then()
                .statusCode(200);
    }

    @Test
    void getAccountInfoWithLoggingTest() {
        given()
                .headers(headers)
                .log()
                .all()
                .when()
                .get("/account/{username}", username)
                .prettyPeek()
                .then()
                .statusCode(200);
    }

    @Test
    void getAccountInfoWithoutToken() {
        when()
                .get("/account/{username}", username)
                .then()
                .statusCode(200);
    }

    @Test
    void getAccountInfoVerifyUrlTest() {
        String url = given()
                .headers(headers)
                .log()
                .uri()
                .when()
                .get("/account/{username}", username)
                //   .prettyPeek()
                .then()
                .statusCode(200)
                .contentType("application/json")
                .log()
                .status()
                .extract()
                .response()
                .jsonPath()
                .getString("data.url");
        assertThat(url, equalTo("smaula"));
    }

    @Test
    void getAccountInfoVerifyUrlInGivenPartTest() {
        given()
                .headers(headers)
                .log()
                .uri()
                .expect()
                .body("success", is(true))
                .body("data.url", is("smaula"))
                .when()
                .get("/account/{username}", username)
                //   .prettyPeek()
                .then()
                .statusCode(200)
                .contentType("application/json")
                .log()
                .status();
    }



}
