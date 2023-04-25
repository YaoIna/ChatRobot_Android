package com.yao.chatrobot.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiKeyScreen(
    modifier: Modifier = Modifier,
    apiKey: String,
    onUpdateClick: (key: String) -> Unit,
    hasCloseButton: Boolean = false,
    onClose: () -> Unit = {}
) {
    var text: String by remember { mutableStateOf("") }
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxHeight()
    ) {
        if (hasCloseButton) {
            IconButton(modifier = Modifier.align(Alignment.End), onClick = onClose) {
                Icon(imageVector = Icons.Filled.Close, contentDescription = "Close")
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
        Text(text = "Current API key:", fontWeight = FontWeight.Bold)
        SelectionContainer {
            Text(text = apiKey)
        }
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = text,
            label = {
                Text("New API key")
            },
            onValueChange = { text = it },
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(modifier = Modifier.align(Alignment.End), onClick = {
            if (text.isEmpty())
                return@Button
            onUpdateClick(text)
        }) {
            Text(text = "Update")
        }
    }
}

@Preview
@Composable
fun ApiKeyScreenPreview() {
    ApiKeyScreen(apiKey = "dfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfddddddddddddfdfdfdfdfdjjjdfjhdjfhjdhfjdhfdhfkdhfkdhfkjdhfjkdhfjdkhfjdhfjdhfjdhfjdhfjdhfjdhfjdhf",
        onUpdateClick = { },
        onClose = {})
}