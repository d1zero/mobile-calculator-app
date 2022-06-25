package com.d1zero.calculator

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.pow

class MainActivity : AppCompatActivity() {
    private var canAddOperation = false
    private var canAddDecimal = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun operationAction(view: View) {
        if (view is Button && canAddOperation) {
            workingTV.append(view.text)
            canAddOperation = false
            canAddDecimal = true
        }
    }

    fun numberAction(view: View) {
        if (view is Button) {
            if (view.text == ".") {
                if (canAddDecimal)
                    workingTV.append(view.text)
                canAddDecimal = false
            } else
                workingTV.append(view.text)
            canAddOperation = true
        }
    }

    fun allClearAction(view: View) {
        workingTV.text = ""
        resultTV.text = ""
    }

    fun backSpaceAction(view: View) {
        val length = workingTV.length()
        if (length > 0)
            workingTV.text = workingTV.text.subSequence(0, length - 1)
    }

    fun equalAction(view: View) {
        resultTV.text = calculateResult()
    }

    private fun calculateResult(): String {
        val digitsOperators = digitOperators()
        if (digitsOperators.isEmpty()) return ""

        val timesDivision = timesDivisionCalculate(digitsOperators)
        if (timesDivision.isEmpty()) return ""

        val result = addSubstractCalculate(timesDivision)
        return result.toString()
    }

    private fun addSubstractCalculate(passedList: MutableList<Any>): Float {

        var result = passedList[0] as Float
        for (i in passedList.indices) {
            if (passedList[i] is Char && i != passedList.lastIndex) {
                val operator = passedList[i]
                val nextDigit = passedList[i + 1] as Float
                if (operator == '+')
                    result += nextDigit
                if (operator == '-')
                    result -= nextDigit
            }
        }

        return result
    }

    private fun timesDivisionCalculate(passedList: MutableList<Any>): MutableList<Any> {
        var list = passedList
        while (list.contains('*') || list.contains('/') || list.contains('^')) {
            list = calcTimesDiv(list)
        }
        return list
    }

    private fun calcTimesDiv(passedList: MutableList<Any>): MutableList<Any> {
        val newList = mutableListOf<Any>()
        var restartIndex = passedList.size

        for (i in passedList.indices) {
            if (passedList[i] is Char && i != passedList.lastIndex && i < restartIndex) {
                val operator = passedList[i]
                val prevDigit = passedList[i - 1] as Float
                val nextDigit = passedList[i + 1] as Float
                when (operator) {
                    '*' -> {
                        newList.add(prevDigit * nextDigit)
                        restartIndex = i + 1
                    }
                    '/' -> {
                        newList.add(prevDigit / nextDigit)
                        restartIndex = i + 1
                    }
                    '^' -> {
                        newList.add(prevDigit.pow(nextDigit))
                    }
                    else -> {
                        newList.add(prevDigit)
                        newList.add(operator)
                    }
                }
            }
            if (i > restartIndex) {
                newList.add(passedList[i])
            }
        }

        return newList
    }

    private fun digitOperators(): MutableList<Any> {
        val list = mutableListOf<Any>()
        var currentDigit = ""
        for (char in workingTV.text) {
            if (char.isDigit() || char == '.')
                currentDigit += char
            else {
                list.add(currentDigit.toFloat())
                currentDigit = ""
                list.add(char)
            }
        }

        if (currentDigit != "")
            list.add(currentDigit.toFloat())

        return list

    }
}