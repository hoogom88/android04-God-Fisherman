package com.android04.godfisherman.ui.camera.upload

import android.graphics.Bitmap
import android.text.Editable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android04.godfisherman.data.repository.UploadRepository
import com.android04.godfisherman.ui.camera.CameraActivity
import com.android04.godfisherman.ui.stopwatch.StopwatchViewModel
import com.android04.godfisherman.utils.RepoResponseImpl
import com.android04.godfisherman.utils.convertCentiMeter
import com.android04.godfisherman.utils.roundBodySize
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class UploadViewModel @Inject constructor(private val repository: UploadRepository) : ViewModel() {
    private val _fishTypeList: MutableLiveData<List<String>> by lazy { MutableLiveData<List<String>>() }
    val fishTypeList: LiveData<List<String>> = _fishTypeList

    private val _isFetchSuccess: MutableLiveData<Boolean?> by lazy { MutableLiveData<Boolean?>(null) }
    val isFetchSuccess: LiveData<Boolean?> = _isFetchSuccess

    private val _isUploadSuccess: MutableLiveData<Boolean?> by lazy { MutableLiveData<Boolean?>(null) }
    val isUploadSuccess: LiveData<Boolean?> = _isUploadSuccess

    private val _isInputCorrect: MutableLiveData<Boolean?> by lazy { MutableLiveData<Boolean?>(null) }
    val isInputCorrect: MutableLiveData<Boolean?> = _isInputCorrect

    private val _isLoading: MutableLiveData<Boolean?> by lazy { MutableLiveData<Boolean?>(null) }
    val isLoading: MutableLiveData<Boolean?> = _isLoading

    var fishTypeSelected: String? = null
    var bodySize: Double? = null
    var bodySizeCentiMeter : String? = null
    lateinit var fishThumbnail: Bitmap

    fun fetchInitData(size: Double) {
        bodySize = roundBodySize(size)
        bodySizeCentiMeter = convertCentiMeter(size)
        fishThumbnail = CameraActivity.captureImage!!
    }

    fun fetchFishTypeList() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val callback = RepoResponseImpl<Unit>()

                callback.addFailureCallback {
                    _isFetchSuccess.postValue(false)
                }

                _fishTypeList.postValue(repository.fetchFishTypeList(callback))
            }
        }
    }

    fun setFishTypeSelected(selected: Editable) {
        fishTypeSelected = selected.toString()
    }

    fun saveFishingRecord() {
        if (fishTypeSelected != null && bodySize != null && ::fishThumbnail.isInitialized) {
            _isInputCorrect.value = true
            _isLoading.value = true
            if (StopwatchViewModel.isTimeLine) {
                viewModelScope.launch(Dispatchers.IO) {
                    val callback = RepoResponseImpl<Unit>()

                    callback.addSuccessCallback {
                        _isUploadSuccess.postValue(true)
                        _isLoading.postValue(false)
                    }

                    callback.addFailureCallback {
                        _isUploadSuccess.postValue(false)
                        _isLoading.postValue(false)
                    }

                    repository.saveTmpTimeLineRecord(
                        fishThumbnail,
                        bodySize!!,
                        fishTypeSelected!!,
                        callback
                    )
                }
            } else {
                viewModelScope.launch {
                    val callback = RepoResponseImpl<Unit>()

                    callback.addSuccessCallback {
                        _isUploadSuccess.postValue(true)
                        _isLoading.postValue(false)
                    }

                    callback.addFailureCallback {
                        _isUploadSuccess.postValue(false)
                        _isLoading.postValue(false)
                    }

                    repository.saveImageType(fishThumbnail, bodySize!!, fishTypeSelected!!, callback)
                }
            }
        } else {
            _isInputCorrect.value = false
        }
    }
}
