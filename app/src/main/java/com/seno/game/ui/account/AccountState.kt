package com.seno.game.ui.account

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

class AccountState(
) {

}

@Composable
fun rememberAccountState(
) = remember { AccountState() }