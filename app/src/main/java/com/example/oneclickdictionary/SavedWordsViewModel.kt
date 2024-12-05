package com.example.oneclickdictionary

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SavedWordsViewModel : ViewModel() {
    private val _savedWords = MutableLiveData<MutableMap<String, MutableList<String>>>()
    val savedWords: LiveData<MutableMap<String, MutableList<String>>> = _savedWords

    fun addWord(word: String, definitions: MutableList<String>) {
        val currentMap = _savedWords.value?.toMutableMap() ?: mutableMapOf()
        currentMap[word] = definitions
        _savedWords.value = currentMap
    }
}