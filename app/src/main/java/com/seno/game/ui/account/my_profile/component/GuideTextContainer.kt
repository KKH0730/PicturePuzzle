package com.seno.game.ui.account.my_profile.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.seno.game.R
import com.seno.game.extensions.textDp

@Composable
fun GuideTextContainer(isAnonymous: Boolean) {
    if (isAnonymous) {
        Text(
            text = stringResource(id = R.string.my_profile_guide),
            fontSize = 14.textDp,
            fontWeight = FontWeight.W600,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    } else {
        Box {}
    }
}