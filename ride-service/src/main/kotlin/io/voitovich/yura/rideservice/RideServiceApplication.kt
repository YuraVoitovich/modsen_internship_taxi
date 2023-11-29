package io.voitovich.yura.rideservice

import io.voitovich.yura.rideservice.properties.DefaultApplicationProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
class RideServiceApplication

fun main(args: Array<String>) {
	runApplication<RideServiceApplication>(*args)
}
