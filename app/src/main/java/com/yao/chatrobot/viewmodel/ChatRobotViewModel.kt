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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(BetaOpenAI::class)
class ChatRobotViewModel(
    private val apiKeyRepository: ApiKeyRepository
) : ViewModel() {
    val apiKeyFlow: StateFlow<ApiKeyResult> = apiKeyRepository.getApiKey().map {
        if (it.isEmpty()) ApiKeyResult.Empty else ApiKeyResult.ApiKey(it)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), ApiKeyResult.Initial)

    private val _robotStateFlow = MutableStateFlow<UiState>(UiState.Initial)
    val robotStateFlow = _robotStateFlow.asStateFlow()

    private val _robotSharedFlow = MutableSharedFlow<UiState>(replay = 0)
    val robotSharedFlow = _robotSharedFlow.asSharedFlow()

    private val _scrollBehavior = MutableStateFlow(0)
    val scrollBehavior: StateFlow<Int>
        get() = _scrollBehavior


    private val _messageList = mutableStateListOf<Message>()
    val messageList: List<Message>
        get() = _messageList

    private var openAI: OpenAI? = null

    init {
        observeApiKey()
    }

    fun saveApiKey(apiKey: String) {
        if (apiKey.isEmpty()) return
        viewModelScope.launch(Dispatchers.IO) {
            apiKeyRepository.saveApiKey(apiKey)
        }
    }

    fun sendMessage(messageContent: String) {
        if (messageContent.isEmpty()) return
        openAI?.also {
            _messageList.add(0, Message(messageContent, role = Role.User))
            _scrollBehavior.value = getScrollTrigger(_scrollBehavior.value)
            chatWithRobot(messageContent, it)
        } ?: run {
            viewModelScope.launch {
                _robotSharedFlow.emit(UiState.Error("openAI is null"))
            }
        }

    }

    private fun chatWithRobot(message: String, openAiIns: OpenAI) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    openAiIns.chatCompletion(buildChatRequest(message)).let {
                        val msg = it.choices.firstOrNull()?.message
                            ?: return@let UiState.Error("there's no message")
                        val role = if (msg.role == ChatRole.User) Role.User else Role.Robot
                        val chatMessage = Message(msg.content, role)
                        _messageList.add(0, chatMessage)
                        _scrollBehavior.value = getScrollTrigger(_scrollBehavior.value)
                    }
                } catch (e: Exception) {
                    viewModelScope.launch {
                        _robotSharedFlow.emit(UiState.Error(e.message ?: "unknown error"))
                    }
                }
            }
        }
    }

    private fun buildChatRequest(message: String) = ChatCompletionRequest(
        model = ModelId("gpt-3.5-turbo"), messages = listOf(
            ChatMessage(
                role = ChatRole.User, content = message
            )
        )
    )

    private fun observeApiKey() {
        viewModelScope.launch {
            apiKeyRepository.getApiKey().collect {
                openAI = if (it.isEmpty()) null else OpenAI(it)
            }
        }
    }

    private fun getScrollTrigger(current: Int): Int {
        if (current == 0) return 1
        return -current
    }
}


sealed interface ApiKeyResult {
    object Initial : ApiKeyResult
    class ApiKey(val value: String) : ApiKeyResult
    object Empty : ApiKeyResult
}


sealed class UiState {
    object Initial : UiState()
    object Loading : UiState()
    class Success<T>(val data: T) : UiState()
    class Error(val message: String) : UiState()
}
