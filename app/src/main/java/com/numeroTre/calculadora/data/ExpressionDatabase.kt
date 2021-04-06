package com.numeroTre.calculadora.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Expression::class],
    version = 1
)
abstract class ExpressionDatabase : RoomDatabase() {

    abstract fun getExpressionDao() : ExpressionDao

    companion object{

        @Volatile private var instance: ExpressionDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance?: synchronized(LOCK){
            instance?: buildDatabase(context).also{ instance = it}
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,ExpressionDatabase::class.java, "expressiondatabase"
        ).build()
    }
}