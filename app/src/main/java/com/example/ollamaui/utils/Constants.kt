package com.example.ollamaui.utils

import androidx.compose.ui.unit.dp

object Constants {
    const val OLLAMA_BASE_URL = "http://localhost:11434"
    const val OLLAMA_BASE_ENDPOINT = "/"
    const val OLLAMA_LIST_ENDPOINT = "/api/tags"
    const val OLLAMA_CHAT_ENDPOINT = "/api/chat"

    const val CHAT_DATABASE = "chat_db"
    const val CHAT_STATUS_DATABASE = "chat_status_db"

    const val USER_SETTING = "user_setting"
    const val IS_OLLAMA_ADDRESS_SET = "is_ollama_address_set"
    const val OLLAMA_ADDRESS = "ollama_address"

    //
    val TOP_BAR_HEIGHT = 100.dp
    const val USER_ID = -1
    const val USER_ROLE = "user"
    const val SYSTEM_ROLE = "system"
}