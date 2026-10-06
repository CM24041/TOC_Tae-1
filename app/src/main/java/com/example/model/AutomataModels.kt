package com.example.model

data class StateNode(
    val name: String,
    val output: String = "0"
)

data class Transition(
    val id: String = java.util.UUID.randomUUID().toString(),
    val fromState: String,
    val inputSymbol: String,
    val toState: String
)

data class MealyTransition(
    val fromState: String,
    val inputSymbol: String,
    val toState: String,
    val outputSymbol: String,
    val explanation: String
)

data class AutomataPreset(
    val name: String,
    val description: String,
    val states: List<StateNode>,
    val transitions: List<Transition>,
    val initialState: String
)
