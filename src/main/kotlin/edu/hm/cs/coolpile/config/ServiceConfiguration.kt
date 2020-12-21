package edu.hm.cs.coolpile.config

import edu.hm.cs.coolpile.exception.ServiceNotFoundException

class ServiceConfiguration(val services: Array<AvailableService>) {
    fun getServiceByName(serviceName: String) = services.firstOrNull { it.name == serviceName }
            ?: throw ServiceNotFoundException("Service '$serviceName' not found.")
}

class AvailableService(
        val name: String,
        val description: String,
        val install: String,
        val cmd: String,
        val params: String,
        val inputFileSuffix: String,
        val outputFileSuffix: String
)