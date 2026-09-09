package com.pbrlm.abhaya.ui.screens.evacuate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pbrlm.abhaya.data.location.LocationService
import com.pbrlm.abhaya.domain.integration.EvacuationRoute
import com.pbrlm.abhaya.domain.integration.RoutingDataProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EvacuateUiState(
    val route: EvacuationRoute? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

@HiltViewModel
class EvacuateSafetyViewModel @Inject constructor(
    private val locationService: LocationService,
    private val routingDataProvider: RoutingDataProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(EvacuateUiState())
    val uiState: StateFlow<EvacuateUiState> = _uiState.asStateFlow()

    init { loadRoute() }

    fun loadRoute() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val location = locationService.getCurrentLocation()
                val route = routingDataProvider.getEvacuationRoute(
                    currentLatitude  = if (location.isAvailable) location.latitude  else 12.9123,
                    currentLongitude = if (location.isAvailable) location.longitude else 74.8512
                )
                if (route != null) {
                    _uiState.update { it.copy(route = route, isLoading = false) }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading    = false,
                            errorMessage = "Safe evacuation route is currently unavailable."
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading    = false,
                        errorMessage = "Could not load evacuation route. Please try again."
                    )
                }
            }
        }
    }
}
