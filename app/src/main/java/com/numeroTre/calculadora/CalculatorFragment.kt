package com.numeroTre.calculadora

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.numeroTre.calculadora.data.Expression
import com.numeroTre.calculadora.data.ExpressionDatabase
import kotlinx.android.synthetic.main.fragment_calculator.*
import kotlinx.android.synthetic.main.item_recent_expressions.*
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 * Use the [CalculatorFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CalculatorFragment : BaseFragment(R.layout.fragment_calculator), RecentExpressionAdapter.OnItemClickListener {

    private val maxNumLength = 10
    private val acceptedSymbols = listOf<Char>('.', '+', '-', '*', '/', '(', ')')
    private var bracketDepth = 0
    private var expressionList = mutableListOf<Expression>()
    private var adapter: RecentExpressionAdapter? = RecentExpressionAdapter(expressionList, this)
    private var layoutManager: LinearLayoutManager? = LinearLayoutManager(activity)

    private var lastExpression : Expression? = null

    override fun onPause() {
        super.onPause()

//        Save the last buffer to load later
        lastExpression = Expression(TvEquationBuffer.text.toString(), tvLiveResult.text.toString())
    }

    override fun onResume() {
        super.onResume()

//        Load from history
//        if user clicked on a expression
        val activity = activity as MainActivity

        if (activity.loadFromHistory){
            loadFromExpressionObject(activity.historyExpressionObject)
            activity.loadFromHistory = false
            return
        }

        loadFromExpressionObject(lastExpression)
        lastExpression
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvRecentExpressions.adapter = adapter

//        Check if manager is already set
//        As this is called after onResume
//        else app will crash
        if (rvRecentExpressions.layoutManager == null) {
            layoutManager = LinearLayoutManager(activity)
            rvRecentExpressions.layoutManager = layoutManager
        }
        layoutManager?.stackFromEnd = true

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

        BtnBackspace.setOnClickListener {
            removeLastCharacter()
            setLiveResult(calculate())
        }

        BtnEqualTo.setOnClickListener {
            val result = tvLiveResult.text.toString()
            val expression = TvEquationBuffer.text.toString()
            val expressionObject = Expression(expression, result)

            if ((expression.isNotBlank()) and (result.isNotBlank())) {
                TvEquationBuffer.text = result

//                Save to database
                if (!(activity as MainActivity).incognito) {
                    launch {
                        context?.let {
                            ExpressionDatabase(it).getExpressionDao()
                                    .addExpression(expressionObject)
                        }
                    }
                }

                clearLiveResult()
                addToRecent(expressionObject)
            }
        }
    }

    private fun loadFromExpressionObject(expression : Expression?){
        if (expression != null){
            TvEquationBuffer.text = expression.expression
            tvLiveResult.text = expression.result
            bracketDepth = expression.expression.filter { it == '(' }.count() - expression.expression.filter { it==')' }.count()
        }
    }

    private fun addToRecent(expression: Expression) {
        expressionList.add(expression)
        adapter!!.notifyItemInserted(expressionList.size - 1)
        rvRecentExpressions.scrollToPosition(adapter!!.itemCount - 1)
    }

    private fun calculate(): String {
        val equation = EquationSolver(TvEquationBuffer.text.toString())
        return equation.getResult()
    }

    private fun inputOnClickListener(button: View, input: Char) {

        if ((input.isDigit()) or (acceptedSymbols.contains(input))) {
            if ((input.isDigit()) or (input == '(')) {

//            To go for a new calculation after pressing equalTo
                button.setOnClickListener {
                    if (isLiveResultEmpty()) {
                        setPresetZero()
                    }
                    addToStringBuffer(input)
                }
            } else {

//            To continue with the obtained result
                button.setOnClickListener {
                    addToStringBuffer(input)
                }
            }
        }
        if (input == 'A') {
            button.setOnClickListener {

                if (checkForPresetZero()) {
                    expressionList.clear()
                    adapter!!.notifyDataSetChanged()
                } else {
                    setPresetZero()
                    clearLiveResult()
                    bracketDepth = 0
                }
            }
        }
    }

    private fun addToStringBuffer(c: Char) {
//        add to string buffer from here

        if (c.isDigit() and notExceededNumLength()) {
            this.clearIfPresetZero()
            if (checkTypeAtPosition(-1) != "CloseBracket") appendToBuffer(c)
        }

        if (c in acceptedSymbols) {
            when (c) {
                '*', '/' -> {
                    when (checkTypeAtPosition(-1)) {
                        "Digit", "CloseBracket" -> appendToBuffer(c)
                        "Operator" -> {
                            removeLastCharacter()
                            appendToBuffer(c)
                        }
                    }
                }
                '+', '-' -> {
                    when (checkTypeAtPosition(-1)) {
                        "Digit", "CloseBracket", "OpenBracket" -> appendToBuffer(c)

//                        For inputting signed Numbers
                        "Operator" -> {
                            when (checkTypeAtPosition(-2)) {
                                "Digit", "CloseBracket" -> appendToBuffer(c)
                            }
                        }
                    }
                }
                '.' -> if ((checkTypeAtPosition(-1) == "Digit") and (isDecimalNotUsed())) appendToBuffer(c)
                '(' -> if ((checkTypeAtPosition(-1) == "Operator") or checkForPresetZero()) {
                    clearIfPresetZero()
                    appendToBuffer(c)
                    bracketDepth++
                }
                ')' -> {

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

    private fun appendToBuffer(c: Char) {
        TvEquationBuffer.text = TvEquationBuffer.text.toString() + c
    }

    private fun notExceededNumLength(): Boolean {
//        To stop user entering very long numbers

        val buffer = TvEquationBuffer.text.toString()
        var digitCount = 0

        for (i in buffer.reversed()) {
            if (i.isDigit()) digitCount++
            else break
        }
        return digitCount < maxNumLength
    }

    private fun isDecimalNotUsed(): Boolean {
//        To check whether user already has decimal in a number
        val buffer = TvEquationBuffer.text.toString()
        for (i in buffer.reversed()) {
            if (i.isDigit()) {
//                Do nothing continue checking
            } else if (i == '.') return false
            else if (i in acceptedSymbols) return true
        }
        return true
    }

    private fun setPresetZero() {
        TvEquationBuffer.text = "0"
    }

    private fun clearIfPresetZero() {
//        To remove the preset zero
        if (checkForPresetZero()) {
            TvEquationBuffer.text = ""
        }
    }

    private fun checkForPresetZero(): Boolean {
        return TvEquationBuffer.text.toString() == "0"
    }

    private fun checkTypeAtPosition(pos: Int): String {

        var index = pos
        if (pos < 0) index = TvEquationBuffer.text.toString().length + pos

        val lastChar: Char
        try {
            lastChar = TvEquationBuffer.text.toString().elementAt(index)
        } catch (e: IndexOutOfBoundsException) {
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

    private fun removeLastCharacter() {
        var text = TvEquationBuffer.text.toString()
        if (text.isNotBlank()) {
            TvEquationBuffer.text = text.subSequence(0, text.lastIndex)
        }
        if (TvEquationBuffer.text.toString().isBlank()) setPresetZero()
    }

    private fun setLiveResult(result: String) {
        tvLiveResult.text = result
    }

    private fun clearLiveResult() {
        tvLiveResult.text = ""
    }

    private fun isLiveResultEmpty(): Boolean {
        val text = tvLiveResult.text.toString()
        if (text.isNullOrBlank()) return true
        return false
    }

    override fun onItemClick(position: Int) {
        loadFromExpressionObject(expressionList[position])
    }
}