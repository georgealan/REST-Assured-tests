package com.restassured.tests

import io.restassured.RestAssured
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test

/**
 * Trabalhando com XML no REST Assured.
 */
class TestXml {

    /**
     * Para podermos alcançar os atributos de uma tag xml utilizamos o @ para chegar no atributo.
     */
    @Test
    fun `trabalhando com xml`() {
        RestAssured.given()
                .`when`()
                .get("https://restapi.wcaquino.me/usersXML/3")
                .then()
                .statusCode(200)
                .body("user.name", Matchers.`is`("Ana Julia"))
                .body("user.@id", Matchers.`is`("3"))
    }
}