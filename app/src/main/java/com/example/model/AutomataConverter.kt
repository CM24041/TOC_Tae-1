package com.example.model

object AutomataConverter {

    /**
     * Converts a Moore Machine into its equivalent Mealy Machine.
     * Core Rule:
     * For every transition (fromState --inputSymbol--> toState):
     * The Mealy transition output is the Moore output of `toState` (destination state).
     *
     * Example:
     * q0 --0--> q1, where q1 has Moore output = 1
     * Mealy transition becomes: q0 --0/1--> q1
     */
    fun convertToMealy(
        states: List<StateNode>,
        transitions: List<Transition>
    ): List<MealyTransition> {
        val stateOutputMap = states.associate { it.name to it.output }

        return transitions.map { t ->
            val destOutput = stateOutputMap[t.toState] ?: "0"
            val explanation = "${t.fromState} with input ${t.inputSymbol} goes to ${t.toState}. " +
                    "Destination state ${t.toState} has Moore output $destOutput. " +
                    "So ${t.inputSymbol} → ${t.inputSymbol}/$destOutput."

            MealyTransition(
                fromState = t.fromState,
                inputSymbol = t.inputSymbol,
                toState = t.toState,
                outputSymbol = destOutput,
                explanation = explanation
            )
        }
    }

    /**
     * Runs an input string on the Moore machine and returns step-by-step trace
     */
    fun simulateMoore(
        inputString: String,
        initialState: String,
        states: List<StateNode>,
        transitions: List<Transition>
    ): SimulationResult {
        val stateOutputMap = states.associate { it.name to it.output }
        var currentState = initialState
        val stateHistory = mutableListOf(currentState)
        val outputSequence = StringBuilder()
        
        // Initial state output
        outputSequence.append(stateOutputMap[currentState] ?: "0")

        val steps = mutableListOf<SimulationStep>()

        for (symbol in inputString) {
            val trans = transitions.find { it.fromState == currentState && it.inputSymbol == symbol.toString() }
            if (trans != null) {
                val nextState = trans.toState
                val out = stateOutputMap[nextState] ?: "0"
                outputSequence.append(out)
                steps.add(
                    SimulationStep(
                        fromState = currentState,
                        input = symbol.toString(),
                        toState = nextState,
                        outputProduced = out,
                        machineType = "Moore"
                    )
                )
                currentState = nextState
                stateHistory.add(currentState)
            } else {
                // No transition defined for this symbol
                break
            }
        }

        return SimulationResult(
            input = inputString,
            finalState = currentState,
            outputString = outputSequence.toString(),
            stateSequence = stateHistory,
            steps = steps
        )
    }

    /**
     * Runs an input string on the Mealy machine
     */
    fun simulateMealy(
        inputString: String,
        initialState: String,
        mealyTransitions: List<MealyTransition>
    ): SimulationResult {
        var currentState = initialState
        val stateHistory = mutableListOf(currentState)
        val outputSequence = StringBuilder()
        val steps = mutableListOf<SimulationStep>()

        for (symbol in inputString) {
            val trans = mealyTransitions.find { it.fromState == currentState && it.inputSymbol == symbol.toString() }
            if (trans != null) {
                val nextState = trans.toState
                val out = trans.outputSymbol
                outputSequence.append(out)
                steps.add(
                    SimulationStep(
                        fromState = currentState,
                        input = symbol.toString(),
                        toState = nextState,
                        outputProduced = out,
                        machineType = "Mealy"
                    )
                )
                currentState = nextState
                stateHistory.add(currentState)
            } else {
                break
            }
        }

        return SimulationResult(
            input = inputString,
            finalState = currentState,
            outputString = outputSequence.toString(),
            stateSequence = stateHistory,
            steps = steps
        )
    }
}

data class SimulationStep(
    val fromState: String,
    val input: String,
    val toState: String,
    val outputProduced: String,
    val machineType: String
)

data class SimulationResult(
    val input: String,
    val finalState: String,
    val outputString: String,
    val stateSequence: List<String>,
    val steps: List<SimulationStep>
)
