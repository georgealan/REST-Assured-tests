package com.restassured.tests

import io.restassured.RestAssured
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.collections.ArrayList

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
                .body("user.filhos.name.size()", Matchers.`is`(2))
                .body("user.filhos.name[0]", Matchers.`is`("Zezinho"))
                .body("user.filhos.name[1]", Matchers.`is`("Luizinho"))
                .body("user.filhos.name", Matchers.hasItem("Luizinho"))
                .body("user.filhos.name", Matchers.hasItems("Luizinho", "Zezinho"))
    }

    /**
     * Podemos utilizar encurtadores de caminho para as propriedades pai do xml, evitando inserir código repetido.
     */
    @Test
    fun `encurtando o rootPath para evitar usar nomes das tags pais de forma repetitiva`() {
        RestAssured.given()
                .`when`()
                .get("https://restapi.wcaquino.me/usersXML/3")
                .then()
                .statusCode(200)

                .rootPath("user")
                .body("name", Matchers.`is`("Ana Julia"))
                .body("@id", Matchers.`is`("3"))

                .rootPath("user.filhos")
                .body("name.size()", Matchers.`is`(2))

                .detachRootPath("filhos")
                .body("filhos.name[0]", Matchers.`is`("Zezinho"))
                .body("filhos.name[1]", Matchers.`is`("Luizinho"))

                .appendRootPath("filhos")
                .body("name", Matchers.hasItem("Luizinho"))
                .body("name", Matchers.hasItems("Luizinho", "Zezinho"))
    }

    /**
     * Utilizando o .collect para fazer modificações em tempo real nos dados.
     */
    @Test
    fun `fazendo pesquisas avancadas com xml`() {
        RestAssured.given()
                .`when`()
                .get("https://restapi.wcaquino.me/usersXML")
                .then()
                .statusCode(200)
                .rootPath("users")
                .body("user.size()", Matchers.`is`(3))
                .body("user.findAll{it.age.toInteger() <= 25}.size()", Matchers.`is`(2))
                .body("user.@id", Matchers.hasItems("1", "2", "3"))
                .body("user.find{it.age == 25}.name", Matchers.`is`("Maria Joaquina"))
                .body("user.findAll{it.name.toString().contains('n')}.name", Matchers.hasItems("Maria Joaquina", "Ana Julia"))
                .body("user.salary.find{it != null}.toDouble()", Matchers.`is`(1234.5678))
                .body("user.age.collect{it.toInteger() * 2}", Matchers.hasItems(40, 50, 60))
                .body("user.name.findAll{it.toString().startsWith('Maria')}.collect{it.toString().toUpperCase()}", Matchers.`is`("MARIA JOAQUINA"))
    }

    @Test
    fun `fazendo pesquisas avancadas com xmlPath unindo Kotlin`() {
        val path: Any = RestAssured.given()
                .`when`()
                .get("https://restapi.wcaquino.me/usersXML")
                .then()
                .statusCode(200)
                .extract().path("users.user.name.findAll{it.toString().startsWith('Maria')}")

        println(path.toString())
    }

}