package com.example.ollamaui.ui.screen.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ollamaui.domain.model.ApiError
import com.example.ollamaui.domain.model.ChatInputModel
import com.example.ollamaui.domain.model.ChatModel
import com.example.ollamaui.domain.model.EmptyChatModel
import com.example.ollamaui.domain.model.EmptyChatResponse
import com.example.ollamaui.domain.model.MessageModel
import com.example.ollamaui.domain.model.MessagesModel
import com.example.ollamaui.domain.repository.OllamaRepository
import com.example.ollamaui.utils.Constants.OLLAMA_CHAT_ENDPOINT
import com.example.ollamaui.utils.Constants.USER_ROLE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val ollamaRepository: OllamaRepository,
):ViewModel() {

    private val _chatState = MutableStateFlow(ChatStates())
    val chatState = _chatState.asStateFlow()
    private val isRespondingList = mutableListOf<Int>()
    private val jobs = mutableMapOf<Int, Job>()

    //Public methods
    /*---------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------*/
    fun sendButton(text: String){
        val messages = chatState.value.chatModel.chatMessages.messageModels.toMutableList()
        val oldChatModel = chatState.value.chatModel
        messages.add(MessageModel(content = text, role = USER_ROLE))
        _chatState.update { it.copy(
            chatModel = ChatModel(
                chatId = oldChatModel.chatId,
                chatIcon = oldChatModel.chatIcon,
                chatTitle = oldChatModel.chatTitle,
                chatMessages = MessagesModel( messageModels = messages),
                modelName = oldChatModel.modelName,
                userName = oldChatModel.userName,
                botName = oldChatModel.botName,
            ),
            isDatabaseChanged = true
        )
        }
        ollamaPostMessage(messages = MessagesModel(messageModels = messages), chatId = oldChatModel.chatId)
    }
    fun retry(){
        if(!isRespondingList.contains(chatState.value.chatModel.chatId)) {
            isRespondingList.add(chatState.value.chatModel.chatId)
        }
        _chatState.update { it.copy(chatError = null, isRespondingList = isRespondingList, isSendingFailed = false) }
        ollamaPostMessage(messages = chatState.value.chatModel.chatMessages, chatId = chatState.value.chatModel.chatId)
    }
    fun stop(chatId: Int){
        jobs[chatId]?.cancel()
        jobs.remove(chatId)
        isRespondingList.remove(chatId)
        _chatState.update { it.copy(
            isSendingFailed = true,
            isRespondingList = isRespondingList,
            isDatabaseChanged = true,
            chatError = ApiError.UnknownError.message,
            chatModel = chatState.value.chatModel.copy(newMessageStatus = 2)
        ) }
        viewModelScope.launch {
            dieOllama()
        }
    }

    fun removeLastDialogFromDatabase(){
        val messages = chatState.value.chatModel.chatMessages.messageModels.toMutableList()
        messages.removeLast()
        _chatState.update {
            it.copy(
                chatModel = chatState.value.chatModel.copy(chatMessages = MessagesModel(messageModels = messages), newMessageStatus = 0),
                isDatabaseChanged = true,
                isSendingFailed = false,
                chatError = null
            )
        }
    }

    fun loadStates(chatModel: ChatModel, url: String) {
        _chatState.update {
            it.copy(
                chatModel = chatModel,
                ollamaBaseAddress = url,
                isSendingFailed = chatModel.newMessageStatus == 2
            )
        }
        viewModelScope.launch {
            if(chatModel.newMessageStatus !=2) {
                uploadChatToDatabase(chatModel = chatModel.copy(newMessageStatus = 0))
            }
        }
    }

    fun clearStates(){
        viewModelScope.launch {
            if(chatState.value.isDatabaseChanged && chatState.value.chatModel.newMessageStatus != 2) {
                uploadChatToDatabase(chatModel = chatState.value.chatModel.copy(newMessageStatus = 0))
            }else if(chatState.value.isDatabaseChanged && chatState.value.chatModel.newMessageStatus == 2){
                uploadChatToDatabase(chatModel = chatState.value.chatModel)
            }
            _chatState.update {
                it.copy(
                    chatModel = EmptyChatModel.empty,
                    chatResponse = EmptyChatResponse.empty,
                    chatError = null,
                    isDatabaseChanged = false
                )
            }
        }
    }

    //Private methods
    /*---------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------*/
    private fun ollamaPostMessage(messages: MessagesModel, chatId: Int){
        val oldMessages = chatState.value.chatModel.chatMessages.messageModels.toMutableList()
        val oldChatModel = chatState.value.chatModel
        val job = jobs[chatId] ?: Job()
        jobs[chatId] = job
        viewModelScope.launch(job) {
            if(!isRespondingList.contains(chatState.value.chatModel.chatId)) isRespondingList.add(chatId)
            _chatState.update { it.copy(isRespondingList = isRespondingList) }
            ollamaRepository.postOllamaChat(
                baseUrl = chatState.value.ollamaBaseAddress,
                chatEndpoint = OLLAMA_CHAT_ENDPOINT,
                chatInputModel = ChatInputModel(
                    model = chatState.value.chatModel.modelName,
                    messages = messages.messageModels,
                    keepAlive = 3600,
                    stream = false,
                )
            ).onRight { response ->
                oldMessages.add(MessageModel(content = response.message.content, role = response.message.role))
                isRespondingList.remove(chatId)
                _chatState.update { it.copy(isRespondingList = isRespondingList) }
                if(chatState.value.chatModel.chatId == oldChatModel.chatId) {
                    _chatState.update {
                        it.copy(
                            chatModel = ChatModel(
                                chatId = oldChatModel.chatId,
                                chatIcon = oldChatModel.chatIcon,
                                chatTitle = oldChatModel.chatTitle,
                                chatMessages = MessagesModel(messageModels = oldMessages),
                                modelName = oldChatModel.modelName,
                                userName = oldChatModel.userName,
                                botName = oldChatModel.botName
                            ),
                            chatResponse = response,
                            chatError = null
                        )
                    }
                }
                uploadChatToDatabase(
                    chatModel = ChatModel(
                        chatId = oldChatModel.chatId,
                        chatIcon = oldChatModel.chatIcon,
                        chatTitle = oldChatModel.chatTitle,
                        chatMessages = MessagesModel( messageModels = oldMessages),
                        modelName = oldChatModel.modelName,
                        userName = oldChatModel.userName,
                        botName = oldChatModel.botName,
                        newMessageStatus = if(chatState.value.chatModel.chatId == oldChatModel.chatId) 0 else 1
                    ),
                )
            }.onLeft { error ->
                isRespondingList.remove(chatId)
                _chatState.update { it.copy(isRespondingList = isRespondingList, isSendingFailed = true) }
                if(chatState.value.chatModel.chatId == oldChatModel.chatId) {
                    _chatState.update {
                        it.copy(
                            chatError = error.error.message,
                            chatResponse = EmptyChatResponse.empty
                        )
                    }
                }
                uploadChatToDatabase(oldChatModel.copy(newMessageStatus = 2))
            }
        }
    }

    private suspend fun uploadChatToDatabase(chatModel: ChatModel){
        ollamaRepository.updateDbItem(chatModel = chatModel)
    }

    private suspend fun dieOllama(){
        ollamaRepository.postOllamaChat(
            baseUrl = chatState.value.ollamaBaseAddress,
            chatEndpoint = OLLAMA_CHAT_ENDPOINT,
            chatInputModel = ChatInputModel(
                model = chatState.value.chatModel.modelName,
                messages = emptyList(),
                stream = false,
                keepAlive = 0
            )
        )
    }
}