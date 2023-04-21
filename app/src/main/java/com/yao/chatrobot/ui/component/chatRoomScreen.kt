package com.yao.chatrobot.ui.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.yao.chatrobot.R
import com.yao.chatrobot.data.Message
import com.yao.chatrobot.data.Role
import com.yao.chatrobot.viewmodel.ChatRobotViewModel
import kotlinx.coroutines.launch


private val JumpToBottomThreshold = 56.dp

@Composable
fun RobotMessageCard(msg: Message) {
    Row(
        modifier = Modifier
            .padding(all = 8.dp)
            .fillMaxWidth()
    ) {
        Image(
            painter = painterResource(R.drawable.robot_pic),
            contentDescription = "Contact profile picture",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .border(1.5.dp, MaterialTheme.colorScheme.secondary, CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        var isExpanded by remember {
            mutableStateOf(true)
        }
        Surface(
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .animateContentSize()
                .padding(1.dp)
                .clickable { isExpanded = !isExpanded },
            color = MaterialTheme.colorScheme.secondary
        ) {
            Text(
                text = msg.message,
                modifier = Modifier.padding(all = 8.dp),
                maxLines = if (isExpanded) Int.MAX_VALUE else 1,
            )
        }

    }
}


@Composable
fun UserMessageCardTest(msg: Message) {
    Row(
        modifier = Modifier
            .padding(all = 8.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        var isExpanded by remember {
            mutableStateOf(true)
        }
        Surface(
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .animateContentSize()
                .padding(1.dp)
                .weight(0.9f, false)
                .clickable { isExpanded = !isExpanded },
            color = MaterialTheme.colorScheme.primary
        ) {
            Text(
                text = msg.message,
                modifier = Modifier.padding(all = 8.dp),
                maxLines = if (isExpanded) Int.MAX_VALUE else 1,
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Image(
            painter = painterResource(R.drawable.user_pic),
            contentDescription = "Contact profile picture",
            modifier = Modifier
                .size(40.dp)
                .weight(0.1f)
                .clip(CircleShape)
                .border(1.5.dp, MaterialTheme.colorScheme.secondary, CircleShape)
        )

    }
}

@Composable
fun MessageList(modifier: Modifier = Modifier, messages: List<Message>) {
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    Box(modifier = modifier) {
        LazyColumn(modifier = Modifier.fillMaxSize(), state = scrollState, reverseLayout = true) {
            items(messages) {
                if (it.role == Role.Robot) {
                    RobotMessageCard(it)
                } else {
                    UserMessageCardTest(it)
                }
            }
            coroutineScope.launch {
                scrollState.scrollToItem(0)
            }
        }
        val jumpThreshold = with(LocalDensity.current) {
            JumpToBottomThreshold.toPx()
        }
        val jumpToBottomEnable by remember {
            derivedStateOf {
                scrollState.firstVisibleItemIndex != 0 || scrollState.firstVisibleItemScrollOffset > jumpThreshold
            }
        }
        JumpToBottomButton(modifier = Modifier.align(Alignment.BottomCenter),
            enable = jumpToBottomEnable,
            onClick = {
                coroutineScope.launch {
                    scrollState.animateScrollToItem(0)
                }
            })
    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun MessageInput(modifier: Modifier = Modifier, onSendClick: (String) -> Unit) {
    var text by remember {
        mutableStateOf("")
    }
    val keyboardController = LocalSoftwareKeyboardController.current
    Row(
        modifier = modifier
            .heightIn(min = 50.dp, max = 150.dp)
            .background(Color.LightGray)
            .fillMaxWidth()
    ) {
        OutlinedTextField(modifier = Modifier
            .padding(8.dp)
            .heightIn(min = 50.dp)
            .fillMaxWidth(0.7f),
            colors = TextFieldDefaults.textFieldColors(containerColor = Color.White),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text, imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { keyboardController?.hide() },
            ),
            value = text,
            onValueChange = { text = it })
        Box(
            modifier = Modifier
                .padding(top = 8.dp, bottom = 8.dp)
                .fillMaxWidth()
                .align(Alignment.CenterVertically),
        ) {
            Button(modifier = Modifier.align(Alignment.Center), onClick = {
                onSendClick(text)
                text = ""
                keyboardController?.hide()
            }) {
                Text(text = "send")
            }
        }
    }
}

@Composable
fun ChatRoomScreen(viewModel: ChatRobotViewModel) {
    ConstraintLayout(
        Modifier.fillMaxSize()
    ) {
        val (messageList, messageInput) = createRefs()
        MessageInput(Modifier.constrainAs(messageInput) {
            bottom.linkTo(parent.bottom)
        }, onSendClick = {
            viewModel.sendMessage(it)
        })

        MessageList(
            Modifier
                .fillMaxWidth()
                .constrainAs(messageList) {
                    bottom.linkTo(messageInput.top)
                    top.linkTo(parent.top)
                    height = Dimension.fillToConstraints
                }, messages = viewModel.messageList
        )

    }

}


@Preview
@Composable
fun PreviewMessageInput() {
    MessageInput(onSendClick = {})
}

