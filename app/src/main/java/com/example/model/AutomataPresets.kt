package com.example.model

object AutomataPresets {
    val collegeExample = AutomataPreset(
        name = "College Practical Example",
        description = "Standard 3-state Moore machine with 0/1 inputs and outputs",
        states = listOf(
            StateNode("q0", "0"),
            StateNode("q1", "1"),
            StateNode("q2", "0")
        ),
        transitions = listOf(
            Transition(fromState = "q0", inputSymbol = "0", toState = "q1"),
            Transition(fromState = "q0", inputSymbol = "1", toState = "q0"),
            Transition(fromState = "q1", inputSymbol = "0", toState = "q2"),
            Transition(fromState = "q1", inputSymbol = "1", toState = "q0"),
            Transition(fromState = "q2", inputSymbol = "0", toState = "q2"),
            Transition(fromState = "q2", inputSymbol = "1", toState = "q1")
        ),
        initialState = "q0"
    )

    val onesComplement = AutomataPreset(
        name = "1's Complement Generator",
        description = "Inverts binary sequence (0 -> 1, 1 -> 0)",
        states = listOf(
            StateNode("q0", "0"),
            StateNode("q1", "1")
        ),
        transitions = listOf(
            Transition(fromState = "q0", inputSymbol = "0", toState = "q1"),
            Transition(fromState = "q0", inputSymbol = "1", toState = "q0"),
            Transition(fromState = "q1", inputSymbol = "0", toState = "q1"),
            Transition(fromState = "q1", inputSymbol = "1", toState = "q0")
        ),
        initialState = "q0"
    )

    val seqDetector101 = AutomataPreset(
        name = "Sequence Detector 101",
        description = "Detects binary sequence '101' and produces 1 at completion",
        states = listOf(
            StateNode("q0", "0"),
            StateNode("q1", "0"),
            StateNode("q2", "0"),
            StateNode("q3", "1")
        ),
        transitions = listOf(
            Transition(fromState = "q0", inputSymbol = "0", toState = "q0"),
            Transition(fromState = "q0", inputSymbol = "1", toState = "q1"),
            Transition(fromState = "q1", inputSymbol = "0", toState = "q2"),
            Transition(fromState = "q1", inputSymbol = "1", toState = "q1"),
            Transition(fromState = "q2", inputSymbol = "0", toState = "q0"),
            Transition(fromState = "q2", inputSymbol = "1", toState = "q3"),
            Transition(fromState = "q3", inputSymbol = "0", toState = "q2"),
            Transition(fromState = "q3", inputSymbol = "1", toState = "q1")
        ),
        initialState = "q0"
    )
}

data class QuizQuestion(
    val id: Int,
    val prompt: String,
    val fromState: String,
    val inputSymbol: String,
    val toState: String,
    val toStateOutput: String,
    val fromStateOutput: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

val practiceQuestions = listOf(
    QuizQuestion(
        id = 1,
        prompt = "q0 ── 1 ──→ q1\nOutput of q1 = 0, Output of q0 = 1",
        fromState = "q0",
        inputSymbol = "1",
        toState = "q1",
        toStateOutput = "0",
        fromStateOutput = "1",
        options = listOf("1/1", "1/0", "0/1", "q1/0"),
        correctIndex = 1,
        explanation = "Nice! We use the output of q1 (0) because q1 is the destination state. So input 1 gives output 0 → 1/0."
    ),
    QuizQuestion(
        id = 2,
        prompt = "q1 ── 0 ──→ q2\nOutput of q2 = 1, Output of q1 = 0",
        fromState = "q1",
        inputSymbol = "0",
        toState = "q2",
        toStateOutput = "1",
        fromStateOutput = "0",
        options = listOf("0/0", "1/0", "0/1", "q2/1"),
        correctIndex = 2,
        explanation = "Spot on! The destination state q2 has output 1. Thus, transition is 0/1."
    ),
    QuizQuestion(
        id = 3,
        prompt = "q2 ── 0 ──→ q2 (Self-loop)\nOutput of q2 = 0",
        fromState = "q2",
        inputSymbol = "0",
        toState = "q2",
        toStateOutput = "0",
        fromStateOutput = "0",
        options = listOf("0/0", "0/1", "1/0", "1/1"),
        correctIndex = 0,
        explanation = "Correct! Even with a self-loop, the destination is q2 which outputs 0. So 0/0."
    ),
    QuizQuestion(
        id = 4,
        prompt = "q2 ── 1 ──→ q0\nOutput of q0 = 1, Output of q2 = 0",
        fromState = "q2",
        inputSymbol = "1",
        toState = "q0",
        toStateOutput = "1",
        fromStateOutput = "0",
        options = listOf("1/0", "1/1", "0/1", "0/0"),
        correctIndex = 1,
        explanation = "Great job! Destination state q0 outputs 1, so the transition input 1 pairs with output 1 → 1/1."
    )
)
