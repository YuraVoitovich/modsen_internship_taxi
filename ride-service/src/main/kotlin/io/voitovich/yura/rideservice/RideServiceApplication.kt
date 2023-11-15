package io.voitovich.yura.rideservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class RideServiceApplication

fun main(args: Array<String>) {
	runApplication<RideServiceApplication>(*args)
}
