package com.seno.game.ui.main.home.game.diff_picture.multi.entry.join.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.seno.game.R
import com.seno.game.extensions.noRippleClickable

@Composable
fun QRScanHeader(
    onClickBack: () -> Unit
) {

    Box(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height = 100.dp)
                .padding(horizontal = 20.dp)
                .align(alignment = Alignment.Center)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_arrow_left_white),
                contentDescription = null,
                modifier = Modifier
                    .size(size = 18.dp)
                    .align(alignment = Alignment.CenterStart)
                    .noRippleClickable { onClickBack.invoke() }
            )
        }
    }
}