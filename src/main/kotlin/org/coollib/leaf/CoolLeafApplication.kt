package org.coollib.leaf

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CoolLeafApplication

fun main(args: Array<String>) {
    runApplication<CoolLeafApplication>(*args)
}
