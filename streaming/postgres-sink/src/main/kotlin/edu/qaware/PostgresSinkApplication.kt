package edu.qaware

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

/**
 * Basic sink to write string messages into a Postgres database.
 */
@SpringBootApplication
class PostgresSinkApplication

fun main(args: Array<String>) {
    SpringApplication.run(PostgresSinkApplication::class.java, *args)
}