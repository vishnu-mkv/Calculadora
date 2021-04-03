package com.numeroTre.calculadora

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_calculator.*

/**
 * A simple [Fragment] subclass.
 * Use the [calculatorFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class calculatorFragment : Fragment(R.layout.fragment_calculator) {

    val maxmNumLength = 10

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        BtnOne.setOnClickListener{
            addToStringBuffer('1')
        }


    }

    private fun addToStringBuffer(c: Char){
//        add to string buffer from here

        checkEmptyBuffer()

        if (c.isDigit() and notExceededNumLength()){

            TvEquationBuffer.text = TvEquationBuffer.text.toString() + c
        }
    }

    private fun notExceededNumLength(): Boolean {
//        To stop user entering very long numbers

        val buffer = TvEquationBuffer.text.toString()
        var digitCount = 0


        for (i in buffer.reversed()){
            if(i.isDigit()) digitCount++
            else break
        }
        return digitCount<maxmNumLength
    }

    private fun checkEmptyBuffer(){
//        To remove the preset zero

        if (TvEquationBuffer.text.toString() == "0"){
            TvEquationBuffer.text = ""
        }
    }
}