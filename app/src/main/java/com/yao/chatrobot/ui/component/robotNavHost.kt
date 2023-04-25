package com.yao.chatrobot.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.yao.chatrobot.viewmodel.ApiKeyResult
import com.yao.chatrobot.viewmodel.ChatRobotViewModel


@Composable
fun RobotNavHost(
    modifier: Modifier = Modifier,
    apiKey: ApiKeyResult,
    navController: NavHostController,
    viewModel: ChatRobotViewModel
) {
    NavHost(
        modifier = modifier, navController = navController, startDestination = MessageList.route
    ) {
        composable(MessageList.route) {
            ChatRoomScreen(viewModel = viewModel, onUpdateClick = {
                navController.navigate(Setting.route)
            })
        }
        composable(Setting.route) {
            ApiKeyScreen(apiKey = getApiKeyValue(apiKey),
                onUpdateClick = {
                    viewModel.saveApiKey(it)
                    navController.popBackStack()
                },
                hasCloseButton = true,
                onClose = { navController.popBackStack() })
        }
    }

}

private fun getApiKeyValue(keyResult: ApiKeyResult) = when (keyResult) {
    ApiKeyResult.Initial -> ""
    ApiKeyResult.Empty -> ""
    is ApiKeyResult.ApiKey -> keyResult.value
}

interface RobotDestination {
    val route: String
}

object MessageList : RobotDestination {
    override val route = "message_list"
}

object Setting : RobotDestination {
    override val route = "setting"
}