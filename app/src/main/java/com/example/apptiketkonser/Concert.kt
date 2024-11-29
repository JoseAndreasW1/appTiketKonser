package com.example.apptiketkonser

data class Concert(
    var id: Int,
    var name: String,
    var startPreOrderDate: String,
    var endPreOrderDate: String,
    var startConcertDate: String,
    var description: String,
    var imageUrl: String,
    var price: Int,
    var numberOfTickets: Int,
    var venue: String
)
