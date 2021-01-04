package ru.mad1337bat;

import org.junit.jupiter.api.BeforeAll;

import java.util.HashMap;
import java.util.Map;

public class AccountTests {
    static Map<String, String> headers = new HashMap<>();
    @BeforeAll
    static void setUp () {
        headers.put( "Authorization" , "Bearer 8d48d2e2befeb813e0ba322b7a711a2984bc3e0f" );
    }
}
