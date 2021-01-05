package ru.mad1337bat.uploadimg;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.Objects;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ImageActionTests extends BaseTest {

    String encodedImage;
    String uploadedImageHashCode;
    String returnedId;
    String title = "Hearth";
    String description = "This is an image of a heart outline.";

    @BeforeEach
    void setUp() {
        byte[] fileContent = getFileContentInBase64();
        encodedImage = Base64.getEncoder().encodeToString(fileContent);
        returnedId = given()
                .headers("Authorization", token)
                .multiPart("image", encodedImage)
                .multiPart("title", title)
                .multiPart("description", description)
                .when()
                .post("/image")
                .then()
                .statusCode(200)
                .extract().response().jsonPath().getString("data.id");
        uploadedImageHashCode = given()
                .headers("Authorization", token)
                .multiPart("image", encodedImage)
                .when()
                .get("/image/{imageHash}", returnedId)
                .then()
                .statusCode(200)
                .extract()
                .response()
                .jsonPath()
                .getString("data.deletehash");
    }

    @Test
    void validateOurImgIdTest() {
        String ImgId = given()
                .headers("Authorization", token)
                .when()
                .get("/image/{imageHash}", returnedId)
                .prettyPeek()
                .then()
                .statusCode(200)
                .extract()
                .response()
                .jsonPath()
                .getString("data.id");
        assertThat(ImgId, equalTo(returnedId));
        tearDown();
    }

    @Test
    void validateImgTitleTest() {
        String title_ = given()
                .headers("Authorization", token)
                .when()
                .get("/image/{imageHash}", returnedId)
                .prettyPeek()
                .then()
                .statusCode(200)
                .extract()
                .response()
                .jsonPath()
                .getString("data.title");
        assertThat(title_, equalTo(title));
        tearDown();
    }

    @Test
    void validateImgDescrTest() {
        String descr = given()
                .headers("Authorization", token)
                .when()
                .get("/image/{imageHash}", returnedId)
                .prettyPeek()
                .then()
                .statusCode(200)
                .extract()
                .response()
                .jsonPath()
                .getString("data.description");
        assertThat(descr, equalTo(description));
        tearDown();
    }

    @Test
    void validateImgTypePngTest() {
        String typePng = given()
                .headers("Authorization", token)
                .when()
                .get("/image/{imageHash}", returnedId)
                .prettyPeek()
                .then()
                .statusCode(200)
                .extract()
                .response()
                .jsonPath()
                .getString("data.type");
        assertThat(typePng, equalTo("image/png"));
        tearDown();
    }

    @Test
    void validateImgTypeJpegTest() {
        String type = given()
                .headers("Authorization", token)
                .when()
                .get("/image/{imageHash}", returnedId)
                .prettyPeek()
                .then()
                .statusCode(200)
                .extract()
                .response()
                .jsonPath()
                .getString("data.type");
        assertThat(type, equalTo("image/jpeg"));
        tearDown();
    }

    @Test
    void deletionImgViaDelhashTest() {
        String success = given()
                .headers("Authorization", token)
                .when()
                .delete("/image/{deleteHash}", uploadedImageHashCode)
                .prettyPeek()
                .then()
                .statusCode(200)
                .extract()
                .response()
                .jsonPath()
                .getString("success");
        assertThat(success, equalTo("true"));
    }

    @Test
    void deletionImgViaImgIdTest() {
        String success = given()
                .headers("Authorization", token)
                .when()
                .delete("/image/{imageHash}", returnedId)
                .prettyPeek()
                .then()
                .statusCode(200)
                .extract()
                .response()
                .jsonPath()
                .getString("success");
        assertThat(success, equalTo("true"));
    }


    @Test
    void updateImgTest() {
        String success = given()
                .headers("Authorization", token)
                .when()
                .post("/image/{deleteHash}", uploadedImageHashCode)
                .prettyPeek()
                .then()
                .statusCode(200)
                .extract()
                .response()
                .jsonPath()
                .getString("success");
        assertThat(success, equalTo("true"));
        tearDown();
    }


    @Test
    void updateImgInfoTest() {
        String success = given()
                .headers("Authorization", token)
                .multiPart("title", "Hearth_NEW")
                .multiPart("description", "This is an image of a heart outline._NEW")
                .when()
                .post("/image/{deleteHash}", uploadedImageHashCode)
                .prettyPeek()
                .then()
                .statusCode(200)
                .extract()
                .response()
                .jsonPath()
                .getString("success");
        assertThat(success, equalTo("true"));
        tearDown();
    }


    @Test
    void makeImgAsFavoriteNonfavorite() {
        String isFavorite = given()
                .headers("Authorization", token)
                .when()
                .post("/image/{imageHash}/favorite", returnedId)
                .prettyPeek()
                .then()
                .statusCode(200)
                .extract()
                .response()
                .jsonPath()
                .getString("data");
        assertThat(isFavorite, equalTo("favorited"));

        String isnotFavorite = given()
                .headers("Authorization", token)
                .when()
                .post("/image/{imageHash}/favorite", returnedId)
                .prettyPeek()
                .then()
                .statusCode(200)
                .extract()
                .response()
                .jsonPath()
                .getString("data");
        assertThat(isnotFavorite, equalTo("unfavorited"));
        tearDown();
    }

    @Test
    void imgNotAsFavorite() {
        String isFavorite = given()
                .headers("Authorization", token)
                .when()
                .get("/image/{imageHash}", returnedId)
                .prettyPeek()
                .then()
                .statusCode(200)
                .extract()
                .response()
                .jsonPath()
                .getString("data.favorite");
        assertThat(isFavorite, equalTo("false"));
        tearDown();
    }



// ------------------------------------------ service methods --------------------------------------------- //
    void tearDown() {
        given()
                .headers("Authorization", token)
                .when()
                .delete("account/{username}/image/{deleteHash}", username, uploadedImageHashCode)
                .prettyPeek()
                .then()
                .statusCode(200);
    }

    private byte[] getFileContentInBase64() {
        ClassLoader classLoader = getClass().getClassLoader();
        File inputFile = new File(Objects.requireNonNull(classLoader.getResource("picture7mb.jpg")).getFile());
        byte[] fileContent = new byte[0];
        try {
            fileContent = FileUtils.readFileToByteArray(inputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileContent;
    }
}
