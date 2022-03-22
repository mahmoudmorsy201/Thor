package com.example.thorweather.ui.alert

import androidx.lifecycle.ViewModel
import com.example.thorweather.data.model.AlertModel
import com.example.thorweather.data.repository.Repository
import com.example.thorweather.data.repository.RepositoryInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddingNewAlertDialogViewModel @Inject constructor(
    val repository: RepositoryInterface
) : ViewModel() {

    suspend fun insertAlert(alert: AlertModel): Long =
        repository.insertAlert(alert)


}