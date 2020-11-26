package edu.hm.cs.coolpile

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CoolpileApplication

fun main(args: Array<String>) {
	runApplication<CoolpileApplication>(*args)
}
