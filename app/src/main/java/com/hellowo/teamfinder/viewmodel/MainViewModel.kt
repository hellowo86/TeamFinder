package com.hellowo.teamfinder.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    enum class BottomTab {Find, Team, Profile}

    var bottomTab = MutableLiveData<BottomTab>()

    init {
        bottomTab.value = BottomTab.Find
    }
}
