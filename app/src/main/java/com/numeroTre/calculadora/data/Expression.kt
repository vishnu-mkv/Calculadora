package com.numeroTre.calculadora.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Expression(
        val expression: String,
        val result: String
){
        @PrimaryKey(autoGenerate = true)
        var id: Int = 0
}
