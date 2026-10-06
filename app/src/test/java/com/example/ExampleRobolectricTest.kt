package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.AutomataConverter
import com.example.model.AutomataPresets
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AutomataConverterTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Moore Mealy", appName)
    }

    @Test
    fun `test moore to mealy conversion logic`() {
        val collegePreset = AutomataPresets.collegeExample
        val mealyTransitions = AutomataConverter.convertToMealy(
            collegePreset.states,
            collegePreset.transitions
        )

        // q0 --0--> q1, q1 output is 1 => mealy should be q0 --0/1--> q1
        val t1 = mealyTransitions.find { it.fromState == "q0" && it.inputSymbol == "0" }
        assertEquals("q1", t1?.toState)
        assertEquals("1", t1?.outputSymbol)

        // q0 --1--> q0, q0 output is 0 => mealy should be q0 --1/0--> q0
        val t2 = mealyTransitions.find { it.fromState == "q0" && it.inputSymbol == "1" }
        assertEquals("q0", t2?.toState)
        assertEquals("0", t2?.outputSymbol)
    }

    @Test
    fun `test simulation outputs`() {
        val collegePreset = AutomataPresets.collegeExample
        val mealy = AutomataConverter.convertToMealy(collegePreset.states, collegePreset.transitions)

        val mooreSim = AutomataConverter.simulateMoore("01", "q0", collegePreset.states, collegePreset.transitions)
        // Initial state q0 has output 0.
        // On input 0 -> goes to q1 (output 1).
        // On input 1 -> goes to q0 (output 0).
        // Moore output: "0" + "1" + "0" = "010"
        assertEquals("010", mooreSim.outputString)

        val mealySim = AutomataConverter.simulateMealy("01", "q0", mealy)
        // Step 1: q0 with 0 goes to q1 producing 1.
        // Step 2: q1 with 1 goes to q0 producing 0.
        // Mealy output: "1" + "0" = "10"
        assertEquals("10", mealySim.outputString)
    }
}
