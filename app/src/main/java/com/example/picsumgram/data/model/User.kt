package com.example.picsumgram.data.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

data class Geo(
    val lat: String,
    val lng: String
)

data class Address(
    val street: String,
    val suite: String,
    val city: String,
    val zipcode: String,
    @Embedded val geo: Geo
)

data class Company(
    val name: String,
    val catchPhrase: String,
    val bs: String
)

@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: Int,
    val name: String,
    val username: String,
    val email: String,
    @Embedded(prefix = "address_") val address: Address,
    val phone: String,
    val website: String,
    @Embedded(prefix = "company_") val company: Company
)