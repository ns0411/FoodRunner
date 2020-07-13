package com.example.foodrunner.model

data class OrderHistory (
    var orderId:String,
    var restaurantName:String,
    var totalCost:String,
    var orderPlacedAt:String
)