package com.pbrlm.abhaya.ui.screens.detail

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pbrlm.abhaya.domain.model.EmergencyRequest
import com.pbrlm.abhaya.domain.model.EmergencyStatus
import com.pbrlm.abhaya.domain.repository.EmergencyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DetailUiState(
    val request: EmergencyRequest? = null,
    val isLoading: Boolean = true,
    val showCancelDialog: Boolean = false,
    val cancelSuccess: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class EmergencyDetailViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val emergencyRepository: EmergencyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    fun load(emergencyId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val request = emergencyRepository.getEmergencyRequest(emergencyId)
            _uiState.update { it.copy(request = request, isLoading = false) }
        }
    }

    fun showCancelDialog()  { _uiState.update { it.copy(showCancelDialog = true) } }
    fun dismissCancelDialog(){ _uiState.update { it.copy(showCancelDialog = false) } }

    fun cancelRequest() {
        val id = _uiState.value.request?.emergencyRequestId ?: return
        viewModelScope.launch {
            dismissCancelDialog()
            val result = emergencyRepository.cancelEmergency(id)
            if (result.isSuccess) {
                // Reload
                val updated = emergencyRepository.getEmergencyRequest(id)
                _uiState.update { it.copy(request = updated, cancelSuccess = true) }
            } else {
                _uiState.update { it.copy(errorMessage = result.exceptionOrNull()?.message) }
            }
        }
    }

    fun callResponder() {
        val contact = _uiState.value.request?.responderContact ?: return
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$contact"))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}
