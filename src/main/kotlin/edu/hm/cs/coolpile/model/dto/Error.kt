package edu.hm.cs.coolpile.model.dto

data class Error(
        val timestamp: String,
        val status: String,
        val error: String,
        val message: String,
)