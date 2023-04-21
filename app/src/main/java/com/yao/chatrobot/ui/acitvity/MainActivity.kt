package com.yao.chatrobot.ui.acitvity

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yao.chatrobot.repo.ApiKeyRepository
import com.yao.chatrobot.store.dataStore
import com.yao.chatrobot.ui.component.ApiKeyScreen
import com.yao.chatrobot.ui.component.ChatRoomScreen
import com.yao.chatrobot.ui.component.LoadingScreen
import com.yao.chatrobot.ui.theme.ChatRobotTheme
import com.yao.chatrobot.viewmodel.ApiKeyResult
import com.yao.chatrobot.viewmodel.ChatRobotViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChatRobotTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    App(viewModel = getViewModel(LocalContext.current))
                }
            }
        }
    }
}

@Composable
fun getViewModel(context: Context) = viewModel {
    ChatRobotViewModel(ApiKeyRepository(context.dataStore))
}

@Composable
fun App(viewModel: ChatRobotViewModel) {
    val apiKey by viewModel.apiKeyFlow.collectAsStateWithLifecycle()
    when (apiKey) {
        ApiKeyResult.Initial -> LoadingScreen()
        ApiKeyResult.Empty -> ApiKeyScreen(apiKey = "", onUpdateClick = {
            viewModel.saveApiKey(it)
        })

        is ApiKeyResult.ApiKey -> ChatRoomScreen(viewModel = viewModel)
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ChatRobotTheme {
        App(viewModel = getViewModel(LocalContext.current))
    }
}