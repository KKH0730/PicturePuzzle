package com.seno.game.ui.component

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@SuppressLint("RememberInComposition", "ModifierParameter")
@Composable
fun LiquidStyledBox(
    liquidColor: Color = Color.White,
    isUseStroke: Boolean,
    radius: Dp,
    strokeAlpha: Float = 0.8f,
    containerAlpha: Float = 0.65f,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    boxScope: @Composable BoxScope.() -> Unit
) {
    if (isUseStroke) {
        modifier.border(
            border = BorderStroke(
                width = 1.dp,
                color = liquidColor.copy(alpha = strokeAlpha)
            ),
            shape = RoundedCornerShape(size = radius)
        )
    }
    Card(
        shape = RoundedCornerShape(size = radius),
        colors = CardDefaults.cardColors(
            containerColor = liquidColor.copy(alpha = containerAlpha)
        ),
        modifier = modifier
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = null,
                onClick = onClick
            )
    ) {
        Box(content = boxScope)
    }
}