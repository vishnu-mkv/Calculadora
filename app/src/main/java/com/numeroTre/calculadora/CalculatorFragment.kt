package com.numeroTre.calculadora

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import kotlinx.android.synthetic.main.fragment_calculator.*
import android.util.Log

/**
 * A simple [Fragment] subclass.
 * Use the [CalculatorFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CalculatorFragment : Fragment(R.layout.fragment_calculator) {

    val maxmNumLength = 10
    val acceptedSymbols = listOf<Char>('.', '+', '-', '*', '/', '(', ')')
    var bracketDepth = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        inputOnClickListener(BtnZero, '0')
        inputOnClickListener(BtnOne, '1')
        inputOnClickListener(BtnTwo, '2')
        inputOnClickListener(BtnThree, '3')
        inputOnClickListener(BtnFour, '4')
        inputOnClickListener(BtnFive, '5')
        inputOnClickListener(BtnSix, '6')
        inputOnClickListener(BtnSeven, '7')
        inputOnClickListener(BtnEight, '8')
        inputOnClickListener(BtnNine, '9')
        inputOnClickListener(BtnDecimal, '.')
        inputOnClickListener(BtnAdd, '+')
        inputOnClickListener(BtnMinus, '-')
        inputOnClickListener(BtnMultiply, '*')
        inputOnClickListener(BtnDivide, '/')
        inputOnClickListener(BtnOpenBracket, '(')
        inputOnClickListener(BtnCloseBracket, ')')
        inputOnClickListener(BtnAC, 'A')

        BtnBackspace.setOnClickListener{
            removeLastCharacter()
            setLiveResult(calculate())
        }

        BtnEqualTo.setOnClickListener{
            val result = tvLiveResult.text.toString()
            TvEquationBuffer.text = result
            clearLiveResult()
        }
    }

    private fun calculate():String{
        var equation = EquationSolver(TvEquationBuffer.text.toString())
        return equation.getResult()
    }

    private fun inputOnClickListener(button: View, input: Char){

        if((input.isDigit()) or (acceptedSymbols.contains(input))){
            button.setOnClickListener{
                addToStringBuffer(input)
            }
        }
        if (input == 'A'){
            button.setOnClickListener {
                setPresetZero()
                clearLiveResult()
                bracketDepth = 0
            }
        }
    }

    private fun addToStringBuffer(c: Char){
//        add to string buffer from here

        if (c.isDigit() and notExceededNumLength()){
            this.clearIfPresetZero()
            if (checkTypeAtPosition(-1)!="CloseBracket") appendToBuffer(c)
        }

        if(c in acceptedSymbols){
            when(c) {
                '*', '/' -> {
                    when(checkTypeAtPosition(-1)){
                        "Digit", "CloseBracket" -> appendToBuffer(c)
                        "Operator" -> {
                            removeLastCharacter()
                            appendToBuffer(c)
                        }
                    }
                }
                '+', '-' -> {
                    when(checkTypeAtPosition(-1)){
                        "Digit", "CloseBracket", "OpenBracket" -> appendToBuffer(c)

//                        For inputting signed Numbers
                        "Operator" -> {
                            when(checkTypeAtPosition(-2)) {
                                "Digit", "CloseBracket" -> appendToBuffer(c)
                            }
                        }
                    }
                }
                '.' -> if (checkTypeAtPosition(-1) == "Digit") appendToBuffer(c)
                '(' -> if ((checkTypeAtPosition(-1) == "Operator") or checkForPresetZero()) {
                    clearIfPresetZero()
                    appendToBuffer(c)
                    bracketDepth++
                }
                ')' -> {
                    Log.d("bracketDepth", bracketDepth.toString())
                    if (bracketDepth > 0) {
                        when (checkTypeAtPosition(-1)) {
                            "Digit", "OpenBracket" -> {
                                appendToBuffer(c)
                                bracketDepth--
                            }
                        }
                    }
                }
            }
        }
        setLiveResult(calculate())
    }

    private fun appendToBuffer(c:Char){
        TvEquationBuffer.text = TvEquationBuffer.text.toString() + c
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

    private fun setPresetZero(){
        TvEquationBuffer.text = "0"
    }

    private fun clearIfPresetZero(){
//        To remove the preset zero
        if (checkForPresetZero()){
            TvEquationBuffer.text = ""
        }
    }

    private fun checkForPresetZero(): Boolean {
        return TvEquationBuffer.text.toString() == "0"
    }

    private fun checkTypeAtPosition(pos: Int):String{

        var index =pos
        if(pos<0) index = TvEquationBuffer.text.toString().length + pos

        val lastChar:Char
        try{
            lastChar = TvEquationBuffer.text.toString().elementAt(index)
        }catch (e:IndexOutOfBoundsException){
            return "Null"
        }
        if (lastChar.isDigit()) return "Digit"
        if (lastChar == '.') return "DecimalPoint"
        if (lastChar == '(') return "OpenBracket"
        if (lastChar == ')') return "CloseBracket"
        if (acceptedSymbols.contains(lastChar)) {
            return "Operator"
        }
        return ""
    }

    private fun removeLastCharacter(){
        var text = TvEquationBuffer.text.toString()
        if (text.isNotBlank()) {
            TvEquationBuffer.text = text.subSequence(0, text.lastIndex)
        }
        if (TvEquationBuffer.text.toString().isBlank()) setPresetZero()
    }

    private fun setLiveResult(result:String){
        tvLiveResult.text = result
    }

    private fun clearLiveResult(){
        tvLiveResult.text = ""
    }
}