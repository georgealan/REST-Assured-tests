package com.restassured.tests

import io.restassured.RestAssured
import io.restassured.http.Method
import io.restassured.response.Response
import io.restassured.response.ValidatableResponse
import org.hamcrest.Matchers
import org.junit.Assert
import org.junit.jupiter.api.Test

class TestHello {

    @Test
    fun `deve retornar status 200 quando consultar a url`() {
        val response: Response = RestAssured.request(Method.GET, "http://google.com")
        val validacao: ValidatableResponse = response.then()
        validacao.statusCode(200)
    }

    @Test
    fun `deve retornar status 200 quando consultar a url funcao mais enchuta`() {
        RestAssured.get("http://google.com")
                .then()
                .statusCode(200)
    }

    @Test
    fun `deve retornar status 200 quando consultar a url funcao mais enchuta 2`() {
        RestAssured.given()
                .`when`()
                .get("http://google.com")
                .then()
                .statusCode(200)
    }

    // Utilizando o Hamcrest junto com JUnit4, o assertThat só funciona no JUnit4, no 5 não tem.
    @Test
    fun `conhecendo matchers hamcrest`() {
        Assert.assertThat("Maria", Matchers.`is`("Maria"))
        Assert.assertThat(128, Matchers.isA(Int::class.java))
        Assert.assertThat(50.0, Matchers.isA(Double::class.java))
        Assert.assertThat("string", Matchers.isA(String::class.java))
        Assert.assertThat('A', Matchers.isA(Char::class.java))
        Assert.assertThat(50.0, Matchers.greaterThan(30.0))
        Assert.assertThat(50.0, Matchers.lessThan(70.0))
    }

    @Test
    fun `testando array list`() {
        val impares: List<Int> = arrayListOf(1, 3, 5, 7, 9)

        Assert.assertThat(impares, Matchers.hasSize(5))
        Assert.assertThat(impares, Matchers.contains(1, 3, 5, 7, 9))
        Assert.assertThat(impares, Matchers.containsInAnyOrder(5, 9, 1, 7, 3))
        Assert.assertThat(impares, Matchers.hasItem(1))
        Assert.assertThat(impares, Matchers.hasItems(1, 5))
    }

    @Test
    fun `George nao e irresponsavel`() {
        Assert.assertThat("George", Matchers.not("Irresponsável"))
    }

    @Test
    fun `teste de nomes com string aninhada`() {
        Assert.assertThat("George", Matchers.anyOf(Matchers.`is`("George"), Matchers.`is`("Alan")))

        Assert.assertThat("Joaquina", Matchers.allOf(Matchers.startsWith("Joa"),
                Matchers.endsWith("ina"), Matchers.containsString("qui")))
    }

    @Test
    fun `validando o retorno do body da requisicao`() {
        RestAssured.given()
                .`when`()
                .get("https://www.google.com/")
                .then()
                .statusCode(200)
                .body(Matchers.containsString("Pesquisa"))
                .body(Matchers.`is`(Matchers.not(Matchers.nullValue())))
    }

}