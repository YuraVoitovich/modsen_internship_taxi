package io.voitovich.yura.rideservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication
@EnableFeignClients
class RideServiceApplication

fun main(args: Array<String>) {
	runApplication<RideServiceApplication>(*args)
}
