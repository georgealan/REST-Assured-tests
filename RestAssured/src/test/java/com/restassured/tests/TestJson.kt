package com.restassured.tests

import io.restassured.RestAssured
import io.restassured.http.Method
import io.restassured.path.json.JsonPath
import io.restassured.response.Response
import org.hamcrest.Matchers
import org.junit.Assert
import org.junit.jupiter.api.Test

class TestJson {

    @Test
    fun `deve verificar primeiro nivel do jason`() {
        RestAssured.given()
                .`when`()
                .get("https://jsonplaceholder.typicode.com/todos/1")
                .then()
                .statusCode(200)
                .body("id", Matchers.`is`(1))
                .body("title", Matchers.containsString("electus aut autem"))
    }

    @Test
    fun `deve verificar primeiro nivel de outras formas`() {
        val response: Response = RestAssured.request(Method.GET, "https://jsonplaceholder.typicode.com/todos/1")

        // Path
        println(response.path<String>("id")) // Veja o número ID que está no arquivo JSON

        Assert.assertEquals(1, response.path("id"))

        // Jsonpath
        val jpath: JsonPath = JsonPath(response.asString())
        Assert.assertEquals(1, jpath.getInt("id"))

        // From
        val id = JsonPath.from(response.asString()).getInt("id")
        Assert.assertEquals(1, id)
    }

    @Test
    fun `deve verificar segundo nivel do json`() {
        RestAssured.given()
                .`when`()
                .get("https://api.github.com/users/george/repos")
                .then()
                .statusCode(200)
                .body("name", Matchers.hasItem("active_admin"))
                .body("owner.login", Matchers.hasItem("george"))
    }

    @Test
    fun `deve verificar lista do json`() {
        RestAssured.given()
                .`when`()
                .get("https://api.github.com/users/george/repos")
                .then()
                .statusCode(200)
                .body("name[0]", Matchers.`is`(".janus"))
                .body("name[1]", Matchers.`is`("active_admin"))
                .body("name", Matchers.hasItems(".janus", "factory_girl_rails"))
    }

    @Test
    fun `deve retornar erro usuario inexistente`() {
        RestAssured.given()
                .`when`()
                .get("https://api.github.com/users/george/repos1")
                .then()
                .statusCode(404)
                .body("message", Matchers.`is`("Not Found"))
    }

    @Test
    fun `deve verificar lista na raiz`() {
        RestAssured.given()
                .`when`()
                .get("https://jsonplaceholder.typicode.com/users/1/posts")
                .then()
                .statusCode(200)
                .body("$", Matchers.hasSize<Int>(10)) // O $ é só uma convenção pode usar "" vazio que funciona.
    }
}