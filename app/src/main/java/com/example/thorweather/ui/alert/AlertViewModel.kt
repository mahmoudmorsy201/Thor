package com.example.thorweather.ui.alert


import androidx.lifecycle.ViewModel
import com.example.thorweather.data.model.AlertModel
import com.example.thorweather.data.repository.Repository
import com.example.thorweather.data.repository.RepositoryInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlertViewModel @Inject constructor(
    private val repository: RepositoryInterface
) : ViewModel() {

    fun getAlerts(): Flow<List<AlertModel>> {
        return repository.getAlerts()
    }

    fun deleteAlert(alert: AlertModel) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.deleteAlert(alert)
        }
    }


}