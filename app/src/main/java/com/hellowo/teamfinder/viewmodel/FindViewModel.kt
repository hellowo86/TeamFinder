package com.hellowo.teamfinder.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel

class FindViewModel : ViewModel() {
    enum class Tab {Team, Instant}

    var tab = MutableLiveData<Tab>()

    init {
        tab.value = Tab.Team
    }
}
