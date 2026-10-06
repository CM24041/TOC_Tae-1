package com.example.viewmodel

import androidx.lifecycle.ViewModel
import com.example.model.AutomataConverter
import com.example.model.AutomataPreset
import com.example.model.AutomataPresets
import com.example.model.MealyTransition
import com.example.model.SimulationResult
import com.example.model.StateNode
import com.example.model.Transition
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class AppScreen {
    HOME,
    BUILD_MACHINE,
    CONVERT,
    AUTOMATON,
    THEORY,
    PRACTICE,
    ABOUT
}

data class AutomataUiState(
    val currentScreen: AppScreen = AppScreen.HOME,
    val isDarkMode: Boolean = true,
    val isSidebarExpanded: Boolean = true,
    val states: List<StateNode> = AutomataPresets.collegeExample.states,
    val transitions: List<Transition> = AutomataPresets.collegeExample.transitions,
    val initialState: String = "q0",
    val mealyTransitions: List<MealyTransition> = emptyList(),
    val isConverted: Boolean = false,
    val selectedStateName: String? = null,
    val highlightedTransitionId: String? = null,
    val conversionStepIndex: Int = 0,
    val isConversionAnimating: Boolean = false,
    // Simulator states
    val simulationInput: String = "0101",
    val simulationResultMoore: SimulationResult? = null,
    val simulationResultMealy: SimulationResult? = null,
    val activeSimulationIndex: Int = -1,
    // Practice Quiz
    val currentQuizIndex: Int = 0,
    val quizSelectedOption: Int? = null,
    val quizIsAnswered: Boolean = false,
    val quizScore: Int = 0,
    // Toast notification
    val toastMessage: String? = null
)

class AutomataViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AutomataUiState())
    val uiState: StateFlow<AutomataUiState> = _uiState.asStateFlow()

    init {
        // Pre-convert initial college example so Convert and Automaton screens are ready
        performConversion()
    }

    fun navigateTo(screen: AppScreen) {
        _uiState.update { it.copy(currentScreen = screen) }
    }

    fun toggleDarkMode() {
        _uiState.update { it.copy(isDarkMode = !it.isDarkMode) }
    }

    fun toggleSidebar() {
        _uiState.update { it.copy(isSidebarExpanded = !it.isSidebarExpanded) }
    }

    fun addState(name: String, output: String = "0") {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return
        if (_uiState.value.states.any { it.name.equals(trimmed, ignoreCase = true) }) {
            showToast("State '$trimmed' already exists!")
            return
        }
        val newState = StateNode(trimmed, output.ifBlank { "0" })
        _uiState.update { current ->
            val updated = current.states + newState
            val init = if (current.initialState.isEmpty()) trimmed else current.initialState
            current.copy(
                states = updated,
                initialState = init,
                isConverted = false
            )
        }
        showToast("Added state $trimmed")
        performConversion()
    }

    fun removeState(stateName: String) {
        _uiState.update { current ->
            val updatedStates = current.states.filterNot { it.name == stateName }
            val updatedTransitions = current.transitions.filterNot {
                it.fromState == stateName || it.toState == stateName
            }
            val newInit = if (current.initialState == stateName) {
                updatedStates.firstOrNull()?.name ?: ""
            } else current.initialState

            current.copy(
                states = updatedStates,
                transitions = updatedTransitions,
                initialState = newInit,
                selectedStateName = if (current.selectedStateName == stateName) null else current.selectedStateName,
                isConverted = false
            )
        }
        showToast("Removed state $stateName")
        performConversion()
    }

    fun updateStateOutput(stateName: String, newOutput: String) {
        _uiState.update { current ->
            val updated = current.states.map {
                if (it.name == stateName) it.copy(output = newOutput) else it
            }
            current.copy(states = updated, isConverted = false)
        }
        performConversion()
    }

    fun addTransition(from: String, symbol: String, to: String) {
        if (from.isBlank() || symbol.isBlank() || to.isBlank()) return
        val cleanSymbol = symbol.trim()

        // Check if transition from 'from' with 'symbol' already exists
        val exists = _uiState.value.transitions.any { it.fromState == from && it.inputSymbol == cleanSymbol }
        if (exists) {
            // Replace existing transition for deterministic machine
            _uiState.update { current ->
                val filtered = current.transitions.filterNot { it.fromState == from && it.inputSymbol == cleanSymbol }
                val newTrans = Transition(fromState = from, inputSymbol = cleanSymbol, toState = to)
                current.copy(transitions = filtered + newTrans, isConverted = false)
            }
            showToast("Updated: $from -- $cleanSymbol --> $to")
        } else {
            val newTrans = Transition(fromState = from, inputSymbol = cleanSymbol, toState = to)
            _uiState.update { current ->
                current.copy(transitions = current.transitions + newTrans, isConverted = false)
            }
            showToast("Added: $from -- $cleanSymbol --> $to")
        }
        performConversion()
    }

    fun removeTransition(transitionId: String) {
        _uiState.update { current ->
            current.copy(
                transitions = current.transitions.filterNot { it.id == transitionId },
                isConverted = false
            )
        }
        showToast("Transition deleted")
        performConversion()
    }

    fun setInitialState(stateName: String) {
        _uiState.update { it.copy(initialState = stateName) }
    }

    fun selectState(stateName: String?) {
        _uiState.update { it.copy(selectedStateName = stateName) }
    }

    fun loadPreset(preset: AutomataPreset) {
        _uiState.update {
            it.copy(
                states = preset.states,
                transitions = preset.transitions,
                initialState = preset.initialState,
                selectedStateName = null,
                isConverted = false,
                conversionStepIndex = 0
            )
        }
        performConversion()
        showToast("Loaded '${preset.name}'")
    }

    fun resetMachine() {
        _uiState.update {
            it.copy(
                states = emptyList(),
                transitions = emptyList(),
                initialState = "",
                mealyTransitions = emptyList(),
                isConverted = false,
                selectedStateName = null,
                simulationResultMoore = null,
                simulationResultMealy = null
            )
        }
        showToast("Machine reset. Add states to begin.")
    }

    fun performConversion() {
        val states = _uiState.value.states
        val transitions = _uiState.value.transitions
        val mealy = AutomataConverter.convertToMealy(states, transitions)
        _uiState.update {
            it.copy(
                mealyTransitions = mealy,
                isConverted = true
            )
        }
    }

    fun startConversionAnimation() {
        performConversion()
        _uiState.update {
            it.copy(
                isConversionAnimating = true,
                conversionStepIndex = 0
            )
        }
    }

    fun nextConversionStep() {
        _uiState.update {
            val nextIndex = (it.conversionStepIndex + 1).coerceAtMost(it.mealyTransitions.size - 1)
            it.copy(conversionStepIndex = nextIndex)
        }
    }

    fun previousConversionStep() {
        _uiState.update {
            val prevIndex = (it.conversionStepIndex - 1).coerceAtLeast(0)
            it.copy(conversionStepIndex = prevIndex)
        }
    }

    fun setSimulationInput(input: String) {
        _uiState.update { it.copy(simulationInput = input) }
    }

    fun runSimulation() {
        val s = _uiState.value
        if (s.initialState.isEmpty() || s.states.isEmpty()) {
            showToast("Please add states first!")
            return
        }
        val mooreRes = AutomataConverter.simulateMoore(
            s.simulationInput,
            s.initialState,
            s.states,
            s.transitions
        )
        val mealyRes = AutomataConverter.simulateMealy(
            s.simulationInput,
            s.initialState,
            s.mealyTransitions
        )
        _uiState.update {
            it.copy(
                simulationResultMoore = mooreRes,
                simulationResultMealy = mealyRes,
                activeSimulationIndex = 0
            )
        }
        showToast("Simulation complete!")
    }

    fun stepSimulation() {
        val s = _uiState.value
        val maxSteps = s.simulationInput.length
        if (s.activeSimulationIndex < maxSteps - 1) {
            _uiState.update { it.copy(activeSimulationIndex = it.activeSimulationIndex + 1) }
        }
    }

    fun selectQuizOption(index: Int) {
        _uiState.update { it.copy(quizSelectedOption = index, quizIsAnswered = true) }
    }

    fun nextQuizQuestion(totalQuestions: Int) {
        _uiState.update {
            val next = (it.currentQuizIndex + 1) % totalQuestions
            it.copy(
                currentQuizIndex = next,
                quizSelectedOption = null,
                quizIsAnswered = false
            )
        }
    }

    fun showToast(msg: String) {
        _uiState.update { it.copy(toastMessage = msg) }
    }

    fun dismissToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }
}
