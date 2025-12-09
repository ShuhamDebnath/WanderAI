package com.shuham.wanderai.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shuham.wanderai.data.model.TripRequest
import com.shuham.wanderai.data.model.TripResponse
import com.shuham.wanderai.domain.repository.TripRepository
import com.shuham.wanderai.util.NetworkResult
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// One-time events for Navigation
sealed interface HomeEvent {
    data class NavigateToTripDetails(val tripId: String) : HomeEvent
}

class HomeViewModel(
    private val repository: TripRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    private val _events = Channel<HomeEvent>()
    val events = _events.receiveAsFlow()

    fun onAction(action: HomeAction) {
        when (action) {
            is HomeAction.OnDestinationChanged -> onDestinationChanged(action.index, action.newValue)
            HomeAction.OnAddDestination -> onAddDestination()
            is HomeAction.OnRemoveDestination -> onRemoveDestination(action.index)
            is HomeAction.OnTravelerTypeSelected -> onTravelerTypeSelected(action.type)
            is HomeAction.OnDateTypeToggle -> onDateTypeToggle(action.isFlexible)
            is HomeAction.OnDurationChanged -> onDurationChanged(action.newDuration)
            is HomeAction.OnBudgetSelected -> onBudgetSelected(action.budget)
            is HomeAction.OnDietSelected -> onDietSelected(action.diet)
            is HomeAction.OnPaceChanged -> onPaceChanged(action.newPace)
            is HomeAction.OnInterestSelected -> onInterestSelected(action.interest)
            HomeAction.OnPlanTripClicked -> onPlanTripClicked()
            HomeAction.OnErrorDismissed -> onErrorDismissed()
        }
    }

    private fun onDestinationChanged(index: Int, newValue: String) {
        val currentDestinations = _state.value.destinations.toMutableList()
        if (index in currentDestinations.indices) {
            currentDestinations[index] = newValue
            _state.update { it.copy(destinations = currentDestinations) }
        }
    }

    private fun onAddDestination() {
        val currentDestinations = _state.value.destinations.toMutableList()
        currentDestinations.add("")
        _state.update { it.copy(destinations = currentDestinations) }
    }

    private fun onRemoveDestination(index: Int) {
        val currentDestinations = _state.value.destinations.toMutableList()
        if (currentDestinations.size > 1) { // Keep at least one
            currentDestinations.removeAt(index)
            _state.update { it.copy(destinations = currentDestinations) }
        }
    }

    private fun onTravelerTypeSelected(type: TravelerType) {
        _state.update { it.copy(selectedTravelerType = type) }
    }

    private fun onDateTypeToggle(isFlexible: Boolean) {
        _state.update { it.copy(isFlexibleDate = isFlexible) }
    }

    private fun onDurationChanged(newDuration: Int) {
        if (newDuration >= 1) {
            _state.update { it.copy(tripDurationDays = newDuration) }
        }
    }

    private fun onBudgetSelected(budget: BudgetTier) {
        _state.update { it.copy(selectedBudget = budget) }
    }

    private fun onDietSelected(diet: DietOption) {
        val currentDiet = _state.value.selectedDiet.toMutableList()
        if (currentDiet.contains(diet)) {
            currentDiet.remove(diet)
        } else {
            currentDiet.add(diet)
        }
        _state.update { it.copy(selectedDiet = currentDiet) }
    }
	
    private fun onPaceChanged(newPace: Float) {
        _state.update { it.copy(pace = newPace) }
    }

    private fun onInterestSelected(interest: Interest) {
        val currentInterests = _state.value.selectedInterests.toMutableList()
        if (currentInterests.contains(interest)) {
            currentInterests.remove(interest)
        } else {
            currentInterests.add(interest)
        }
        _state.update { it.copy(selectedInterests = currentInterests) }
    }
    
    private fun onPlanTripClicked() {
        if (_state.value.isLoading) return
        
        _state.update { it.copy(isLoading = true, errorMessage = null) }

        val currentState = _state.value
        val request = TripRequest(
            destination = currentState.destinations.joinToString(", "),
            budget = currentState.selectedBudget.label,
            travelers = currentState.selectedTravelerType.label,
            duration = currentState.tripDurationDays,
            interests = currentState.selectedInterests.map { it.label },
            diet = currentState.selectedDiet.map { it.label }
        )
        
        viewModelScope.launch {
            when (val result = repository.generateTrip(request)) {
                is NetworkResult.Success -> {
                    result.data?.let {
                        _state.update { state -> state.copy(isLoading = false) }
                        _events.send(HomeEvent.NavigateToTripDetails(it.id))
                    } ?: _state.update { it.copy(isLoading = false, errorMessage = "Received empty response from API.") }
                }
                is NetworkResult.Error -> {
                    _state.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
                is NetworkResult.Loading -> { /* Handled by initial state update */ }
            }
        }
    }
    
    private fun onErrorDismissed() {
        _state.update { it.copy(errorMessage = null) }
    }
}
