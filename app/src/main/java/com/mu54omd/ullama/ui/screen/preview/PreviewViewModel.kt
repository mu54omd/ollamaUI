package com.mu54omd.ullama.ui.screen.preview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mu54omd.ullama.domain.model.objectbox.StableFile
import com.mu54omd.ullama.ui.common.splitAndKeepDot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PreviewViewModel @Inject constructor(): ViewModel() {

    private val _selectedFile = MutableStateFlow(StableFile())
    val selectedFile = _selectedFile.asStateFlow()

    private val _output = MutableStateFlow(emptyList<String>())
    val output = _output.asStateFlow()

    fun selectFile(file: StableFile){
        _selectedFile.update { file }
    }

    fun prepareFile (file: StableFile){
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val list = file.attachResult.splitAndKeepDot()
                _output.update { list }
            }
        }
    }
}