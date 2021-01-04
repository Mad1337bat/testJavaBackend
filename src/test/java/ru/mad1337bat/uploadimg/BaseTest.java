package ru.mad1337bat.uploadimg;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public abstract class BaseTest {
    protected static Properties prop = new Properties();
    protected static String token;
    protected static String username;
    protected static Map<String, String> headers = new HashMap<>();

    @BeforeAll
    static void beforeAll() {
        loadProperties();
        token = prop.getProperty("token");
        headers.put("Authorization", token);
        RestAssured.baseURI = prop.getProperty("base.url");
        username = prop.getProperty("username");

        //RestAssured.enableLoggingOfRequestAndResponseIfValidationFails(); //логирование только при ошибке
    }


    //    --------------------------------------- service method --------------------------------------------- //
    private static void loadProperties() {
        try {
            prop.load(new FileInputStream("src\\test\\resources\\app.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
