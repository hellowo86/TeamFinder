package com.hellowo.teamfinder.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.hellowo.teamfinder.data.ChatFliterData

class FindViewModel : ViewModel() {
    var isOnfilter = MutableLiveData<Boolean>()

    init {
        isOnfilter.value = ChatFliterData.hashTagSet.isNotEmpty()
    }
}
