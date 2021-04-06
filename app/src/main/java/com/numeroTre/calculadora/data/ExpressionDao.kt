package com.numeroTre.calculadora.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ExpressionDao {

    @Insert
    suspend fun addExpression(expression:Expression)

    @Query("SELECT * FROM EXPRESSION")
    suspend fun getAllExpressions(): List<Expression>

    @Query("DELETE FROM EXPRESSION")
    suspend fun clearHistory()
}