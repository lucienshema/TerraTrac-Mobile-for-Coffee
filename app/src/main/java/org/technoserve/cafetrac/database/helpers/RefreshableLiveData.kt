
package com.example.cafetrac.database.helpers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

class RefreshableLiveData<T>(
    private val source: () -> LiveData<T>
) : MediatorLiveData<T>() {

    private var liveData = source()

    init {
        this.addSource(liveData, ::observer)
    }

    private fun observer(data: T) {
        value = data
    }
}