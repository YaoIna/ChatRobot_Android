package com.yao.chatrobot.ui.component

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun JumpToBottomButton(
    modifier: Modifier = Modifier,
    enable: Boolean = false,
    onClick: () -> Unit
) {

    val offset by animateDpAsState(targetValue = if (enable) 32.dp else (-32).dp)
    if (offset > 0.dp) ExtendedFloatingActionButton(
        icon = {
            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                modifier = Modifier.height(18.dp),
                contentDescription = null
            )
        },
        text = {
            Text(text = "Jump to bottom")
        },
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary,
        modifier = modifier
            .offset(x = 0.dp, y = -offset)
            .height(36.dp)
    )

}

@Preview
@Composable
fun JumpToBottomButtonPreview() {
    JumpToBottomButton(enable = true, onClick = {})
}