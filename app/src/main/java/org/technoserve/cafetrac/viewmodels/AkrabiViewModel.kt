package org.technoserve.cafetrac.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import org.technoserve.cafetrac.database.AppDatabase
import com.example.cafetrac.database.dao.AkrabiDao

import kotlinx.coroutines.launch
import org.technoserve.cafetrac.database.models.Akrabi

class AkrabiViewModel(application: Application) : AndroidViewModel(application) {
    private val akrabiDao: AkrabiDao = AppDatabase.getInstance(application).akrabiDao()

    // LiveData for observing the list of Akrabi items
    private val _akrabis = MutableLiveData<List<Akrabi>>()
    val akrabis: LiveData<List<Akrabi>> get() = _akrabis

    init {
        // Load the list of Akrabi when ViewModel is initialized
        viewModelScope.launch {
            akrabiDao.getAllAkrabis().observeForever { akrabiList ->
                _akrabis.value = akrabiList
            }
        }
    }

    fun insertAkrabi(akrabi: Akrabi) = viewModelScope.launch {
        akrabiDao.insertAkrabi(akrabi)
    }

    fun updateAkrabi(akrabi: Akrabi) = viewModelScope.launch {
        akrabiDao.updateAkrabi(akrabi)
    }

    fun deleteAkrabi(akrabi: Akrabi) = viewModelScope.launch {
        akrabiDao.deleteAkrabi(akrabi)
        // No need to manually update _akrabis as LiveData will automatically update the list
    }

    fun getAkrabiById(akrabiId: Long): LiveData<Akrabi?> {
        return akrabiDao.getAkrabiById(akrabiId)
    }
}



class AkrabiViewModelFactory(
    private val application: Application,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        if (modelClass.isAssignableFrom(AkrabiViewModel::class.java)) {
            return AkrabiViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

