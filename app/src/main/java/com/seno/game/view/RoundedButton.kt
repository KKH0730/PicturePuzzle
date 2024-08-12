package com.seno.game.view

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.seno.game.extensions.noRippleClickable
import com.seno.game.extensions.textDp

@SuppressLint("ModifierParameter")
@Composable
fun RoundedButton(
    text: String = "",
    textSize: TextUnit = 12.textDp,
    textColor: Color = Color.White,
    fontWeight: FontWeight = FontWeight.W400,
    paddingValues: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
    backgroundColor: Color,
    radius: Dp = 10.dp,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(size = radius),
        backgroundColor = backgroundColor,
        modifier = modifier
            .noRippleClickable { onClick?.invoke() }
    ) {
        Text(
            text = text,
            fontSize = textSize,
            color = textColor,
            fontWeight = fontWeight,
            modifier = Modifier.padding(paddingValues)
        )
    }
}