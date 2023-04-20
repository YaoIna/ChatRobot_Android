package com.yao.chatrobot.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.yao.chatrobot.data.Message
import com.yao.chatrobot.data.Role
import com.yao.chatrobot.repo.ApiKeyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(BetaOpenAI::class)
class ChatRobotViewModel(
    private val apiKeyRepository: ApiKeyRepository, private val openAI: OpenAI
) : ViewModel() {
    // API_KEY=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
    val apiKeyFlow: StateFlow<ApiKeyResult> = apiKeyRepository.getApiKey().map {
        if (it.isEmpty()) ApiKeyResult.Empty else ApiKeyResult.ApiKey(it)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), ApiKeyResult.Initial)

    private val _robotStateFlow = MutableStateFlow<UiState>(UiState.Loading)
    val robotStateFlow = _robotStateFlow.asStateFlow()

    private val _messageList = mutableStateListOf<Message>()
    val messageList: List<Message>
        get() = _messageList


    fun saveApiKey(apiKey: String) = viewModelScope.launch(Dispatchers.IO) {
        apiKeyRepository.saveApiKey(apiKey)
    }

    fun sendMessage(messageContent: String) {
        if (messageContent.isEmpty()) return
        _messageList.add(Message(messageContent, role = Role.User))
        chatWithRobot(messageContent)
    }

    private fun chatWithRobot(message: String) {
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
                try {
                    openAI.chatCompletion(buildChatRequest(message)).let {
                        val msg = it.choices.firstOrNull()?.message
                            ?: return@let UiState.Error("there's no message")
                        val role = if (msg.role == ChatRole.User) Role.User else Role.Robot
                        val chatMessage = Message(msg.content, role)
                        _messageList.add(chatMessage)
                        UiState.Success(chatMessage)
                    }
                } catch (e: Exception) {
                    return@withContext UiState.Error(e.message ?: "unknown error")
                }
            }
            _robotStateFlow.value = response
        }
    }

    private fun buildChatRequest(message: String) = ChatCompletionRequest(
        model = ModelId("gpt-3.5-turbo"), messages = listOf(
            ChatMessage(
                role = ChatRole.User, content = message
            )
        )
    )

}


sealed interface ApiKeyResult {
    object Initial : ApiKeyResult
    class ApiKey(val value: String) : ApiKeyResult
    object Empty : ApiKeyResult
}

sealed class UiState {
    object Loading : UiState()
    class Success<T>(val data: T) : UiState()
    class Error(val message: String) : UiState()
}
