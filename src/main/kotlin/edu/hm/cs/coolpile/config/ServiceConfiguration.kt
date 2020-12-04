package edu.hm.cs.coolpile.config

import edu.hm.cs.coolpile.exception.ServiceNotFoundException

class ServiceConfiguration(val services: Array<AvailableService>) {
    fun getServiceByName(serviceName: String) = services.firstOrNull { it.name == serviceName }
            ?: throw ServiceNotFoundException("Service '$serviceName' not found.")
}

class AvailableService(val name: String, val cmd: String, val fileSuffix: String, val outputFileSuffix: String)