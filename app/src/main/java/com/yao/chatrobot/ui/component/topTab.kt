package com.yao.chatrobot.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun MessageTab(modifier: Modifier = Modifier, onUpdateClick: () -> Unit) {
    Box(
        modifier = modifier
            .background(Color.LightGray)
            .height(TabHeight)
            .fillMaxWidth()
    ) {
        Button(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp),
            onClick = {
                onUpdateClick()
            }) {
            Text(text = "Update Key")
        }
    }
}

@Preview
@Composable
fun MessageTabPreview() {
    MessageTab(onUpdateClick = {})
}

private val TabHeight = 56.dp
