package ca.aequilibrium.base.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.aequilibrium.base.networking.TransformerService
import ca.aequilibrium.base.util.Preferences
import com.orhanobut.logger.Logger
import kotlinx.coroutines.launch

class AppViewModel(private val service: TransformerService) : ViewModel() {
    fun allSpark(){
        if (Preferences.isNewUser) {
            viewModelScope.launch {
                Preferences.token = service.getAllSpark()
                Logger.d("Token: ${Preferences.token}")
            }
        }
    }

    suspend fun getVersion(): String{
        return service.getVesion()
    }
}
