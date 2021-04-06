package com.numeroTre.calculadora

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.numeroTre.calculadora.data.Expression
import com.numeroTre.calculadora.data.ExpressionDatabase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {

    var incognito = false
    var loadFromHistory = false
    var historyExpressionObject : Expression? = null

    private lateinit var job: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        job = Job()

        val calculatorFragment = CalculatorFragment()

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.Frame, calculatorFragment)
            commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.MiHistory -> {

//               Check if history fragment doesn't exist already
                if(supportFragmentManager.findFragmentByTag("History") == null){

                    val historyFragment = HistoryFragment()
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.Frame, historyFragment, "History")
                        addToBackStack("History")
                        commit()
                    }
                }
            }
            R.id.MiIncognito -> {
                incognito = !incognito
                if (incognito){
                    item.setIcon(R.drawable.ic_incognito_violet)
                    Toast.makeText(this, "Incognito On", Toast.LENGTH_SHORT).show()
                }else{
                    item.setIcon(R.drawable.ic_incognito)
                    Toast.makeText(this, "Incognito Off", Toast.LENGTH_SHORT).show()
                }
            }
        }
        return true
    }

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    fun clearHistory(){
        launch{
            this@MainActivity.let {
                ExpressionDatabase(it).getExpressionDao().clearHistory()
            }
            Toast.makeText(this@MainActivity, "History cleared", Toast.LENGTH_LONG).show()
        }
    }
}
