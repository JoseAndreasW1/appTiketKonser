package com.example.apptiketkonser

data class Transaction(
    var id: Int,
    var userId: Int,
    var concertId: Int,
    var transactionDate: String
)
