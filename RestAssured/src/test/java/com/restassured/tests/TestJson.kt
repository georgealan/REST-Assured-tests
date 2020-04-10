package com.restassured.tests

import io.restassured.RestAssured
import io.restassured.http.Method
import io.restassured.path.json.JsonPath
import io.restassured.response.Response
import org.hamcrest.Matchers
import org.junit.Assert
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.math.MathContext
import java.util.*
import kotlin.collections.ArrayList

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
                .get("https://restapi.wcaquino.me/users")
                .then()
                .statusCode(200)
                .body("$", Matchers.hasSize<Int>(3)) // O $ é só uma convenção pode usar "" vazio que funciona.
                .body("name", Matchers.hasItems("João da Silva", "Maria Joaquina", "Ana Júlia"))
                .body("age[1]", Matchers.`is`(25))
                .body("filhos.name", Matchers.hasItem(Arrays.asList("Zezinho", "Luizinho")))
                .body("salary", Matchers.contains(1234.5678f, 2500, null))
    }

    @Test
    fun `deve fazer verificacoes avancadas`() {
        RestAssured.given()
                .`when`()
                .get("https://restapi.wcaquino.me/users")
                .then()
                .statusCode(200)
                .body("$", Matchers.hasSize<Int>(3))
                .body("age.findAll{it <= 25}.size()", Matchers.`is`(2))
                .body("age.findAll{it <= 25 && it > 20}.size()", Matchers.`is`(1))
                .body("findAll{it.age <= 25 && it.age > 20}.name", Matchers.hasItem("Maria Joaquina"))
                .body("findAll{it.age <= 25}[0].name", Matchers.`is`("Maria Joaquina"))
                .body("findAll{it.age <= 25}[-1].name", Matchers.`is`("Ana Júlia"))
                .body("find{it.age <= 25}.name", Matchers.`is`("Maria Joaquina"))
                .body("findAll{it.name.contains('n')}.name", Matchers.hasItems("Maria Joaquina", "Ana Júlia"))
                .body("findAll{it.name.length() > 10}.name", Matchers.hasItems("João da Silva", "Maria Joaquina"))
                .body("name.collect{it.toUpperCase()}", Matchers.hasItem("MARIA JOAQUINA"))
                .body("name.findAll{it.startsWith('Maria')}.collect{it.toUpperCase()}", Matchers.hasItem("MARIA JOAQUINA"))
                .body("name.findAll{it.startsWith('Maria')}.collect{it.toUpperCase()}.toArray()", Matchers.allOf(Matchers.arrayContaining("MARIA JOAQUINA"), Matchers.arrayWithSize(1)))
                .body("age.collect{it * 2}", Matchers.hasItems(60, 50, 40))
                .body("id.max()", Matchers.`is`(3))
                .body("salary.min()", Matchers.`is`(1234.5678f))
                .body("salary.findAll{it != null}.sum()", Matchers.allOf(Matchers.greaterThan(3000.0), Matchers.lessThan(5000.0)))
                //.body("salary.findAll{it != null}.sum()", Matchers.`is`(Matchers.closeTo(3734.5678f, 0.001))) // Problemas com conversão de tipo Java.
    }

    /**
     * Podemos unir o poder das consiltas avançadas com o JsonPath para facilitar, pois
     * unindo podemos quebrar o retorno em um tipo de objeto/atributo para poder utilizar
     * com o Assert do Junit de forma mais simples.
     * Abaixo quebramos uma consulta e tipamos o retorno como um ArrayList(String) e com isso podemos trabalhar
     * em cima desse atributo para comparação.
     */
    @Test
    fun `unindo jsonPath com Kotlin`() {
        val names: ArrayList<String> = RestAssured.given()
                .`when`()
                .get("https://restapi.wcaquino.me/users")
                .then()
                .statusCode(200)
                .extract().path("name.findAll{it.startsWith('Maria')}")

        Assert.assertEquals(1, names.size)
        Assert.assertTrue(names.get(0).equals("Maria Joaquina"))
        Assert.assertEquals(names.get(0).toUpperCase(), "maria joaquina".toUpperCase())
    }
}