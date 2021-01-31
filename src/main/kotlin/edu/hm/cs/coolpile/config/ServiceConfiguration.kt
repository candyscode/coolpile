package edu.hm.cs.coolpile.config

import edu.hm.cs.coolpile.exception.ServiceNotFoundException

class ServiceConfiguration(val services: Array<CompilationService>) {
    fun getServiceByName(serviceName: String) = services.firstOrNull { it.name == serviceName }
            ?: throw ServiceNotFoundException("Service '$serviceName' not found.")
}

class CompilationService(
        val name: String,
        val description: String,
        val image: String,
        val cmd: String,
        val params: String,
        val inputFileSuffix: String,
        val outputFileSuffix: String
)

class ServiceDescription(val name: String, val description: String)