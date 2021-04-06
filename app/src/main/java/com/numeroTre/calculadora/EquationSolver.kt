package com.numeroTre.calculadora

class EquationSolver(private val equation: String) {

    private var expressionList = mutableListOf<String>()

    init {
        expressionList = """(?<=^|[-*+/(])[+-]\d+\.\d+E?\d*|(?<=^|[-*+/(])[+-]\d+|\d+\.\d+E?\d*|\d+|[-+*/()]"""
                .toRegex().findAll(equation).map { it.value }.toMutableList()
        removeHangingElements()
    }

    fun getResult(): String {

        var postfixArray: MutableList<Any>
        try {
            postfixArray = getPostfix()
        }catch (e:java.lang.NumberFormatException){
            return "ERROR"
        }
        val resultStack = mutableListOf<Double>()

        while (postfixArray.isNotEmpty()) {
            val expression = postfixArray.removeAt(0)
            if (expression is Double) {
                resultStack.add(expression)
            } else if (expression is String) {
                if (resultStack.size < 2) return "ERROR"
                val rightOperand = resultStack.removeAt(resultStack.size - 1)
                val leftOperand = resultStack.removeAt(resultStack.size - 1)

                when (expression) {
                    "+" -> {
                        resultStack.add(leftOperand + rightOperand)
                    }
                    "-" -> {
                        resultStack.add(leftOperand - rightOperand)
                    }
                    "*" -> {
                        resultStack.add(leftOperand * rightOperand)
                    }
                    "/" -> {
                        if (rightOperand.equals(0.0)) return "DIVIDE BY ZERO ERROR"
                        resultStack.add(leftOperand / rightOperand)
                    }
                }
            }
        }

        if (resultStack.size != 1) return "ERROR"
        val result = resultStack.last()
        return if (result.toInt().toDouble().equals(result)) result.toInt().toString()
        else result.toString()

    }

    private fun getPostfix(): MutableList<Any> {
        val postfixStack = mutableListOf<Any>()
        val tempStack = mutableListOf<Any>()

        while (expressionList.isNotEmpty()) {
            val expression = expressionList.removeAt(0)
            when (checkForExpressionType(expression)) {
                "String" -> {
                    processStackElement(postfixStack, tempStack, expression)
                }
                "Int", "Double" -> {
                    try {
                        val convertedExp = expression.toDouble()
                        processStackElement(postfixStack, convertedExp)
                    }catch (exception: NumberFormatException){
                        throw exception
                    }
                }
            }
        }
        while (tempStack.isNotEmpty()) {
            if (tempStack.last() == "(") {
                tempStack.removeAt(tempStack.size - 1)
            } else {
                postfixStack.add(tempStack.removeAt(tempStack.size - 1))
            }
        }
        return postfixStack
    }

    private fun checkForExpressionType(str: String): String {
        when (str) {
            "+", "-", "*", "/", "(", ")" -> return "String"
        }
        if (str.matches("""[+-]?\d+""".toRegex())) return "Int"
        if (str.matches("""[+-]?\d+\.\d+E?\d*""".toRegex())) return "Double"
        return "Error"
    }

    private fun processStackElement(postfixStack: MutableList<Any>, tempStack: MutableList<Any>, expression: String) {

        if (tempStack.isEmpty()) {
            tempStack.add(expression)
            return
        }

        when (expression) {
            "+", "-" -> {
                when (tempStack.last()) {
                    "+", "-", "*", "/" -> {
                        while (tempStack.isNotEmpty()) {
                            if (tempStack.last() != "(") postfixStack.add(tempStack.removeAt(tempStack.size - 1))
                            else break
                        }
                        tempStack.add(expression)
                    }
                    "(" -> {
                        tempStack.add(expression)
                    }
                }
            }
            "*", "/" -> {
                when (tempStack.last()) {
                    "+", "-" -> {
                        tempStack.add(expression)
                    }
                    "*", "/" -> {
                        while (tempStack.isNotEmpty()) {
                            if ((tempStack.last() == "(") or (tempStack.last() == "+") or (tempStack.last() == "-")) break
                            postfixStack.add(tempStack.removeAt(tempStack.size - 1))
                        }
                        tempStack.add(expression)
                    }
                    "(" -> {
                        tempStack.add(expression)
                    }
                }
            }
            "(" -> {
                tempStack.add(expression)
            }
            ")" -> {
                while (tempStack.isNotEmpty()) {
                    if (tempStack.last() != "(") postfixStack.add(tempStack.removeAt(tempStack.size - 1))
                    else break
                }
                if (tempStack.isNotEmpty())
                    if (tempStack.last() == "(") tempStack.removeAt(tempStack.size - 1)
            }
        }
    }

    private fun processStackElement(postfixStack: MutableList<Any>, expression: Double) {
        postfixStack.add(expression)
    }

    private fun removeHangingElements(){
        when (expressionList.last()){
            "+", "-", "*", "/", "(" -> expressionList.removeAt(expressionList.size-1)
        }
    }
}
