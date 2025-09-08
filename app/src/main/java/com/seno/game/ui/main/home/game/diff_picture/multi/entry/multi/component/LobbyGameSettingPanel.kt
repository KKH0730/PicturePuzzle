package com.seno.game.ui.main.home.game.diff_picture.multi.entry.multi.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.seno.game.R
import com.seno.game.extensions.noRippleClickable
import com.seno.game.extensions.textDp
import com.seno.game.ui.main.home.game.diff_picture.multi.entry.multi.Difficulty
import com.seno.game.ui.main.home.game.diff_picture.multi.entry.multi.GameRounds
import com.seno.game.ui.main.home.game.diff_picture.multi.entry.multi.GameTimeLimit

@Composable
fun LobbyGameSettingPanel(
    gameTimeLimit: GameTimeLimit,
    gameRounds: GameRounds,
    selectedGameDifficulty: Difficulty,
    onClickTimeLimit: (GameTimeLimit) -> Unit,
    onClickGameRound: (GameRounds) -> Unit,
    onClickDifficulty: (Difficulty) -> Unit,
) {
    Card(
        shape = RoundedCornerShape(size = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(intrinsicSize = IntrinsicSize.Min)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.Black)
                .border(
                    width = 1.dp,
                    color = Color.Black,
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                )
        ) {
            Text(
                text = stringResource(id = R.string.multi_lobby_game_setting_sub_title),
                style = TextStyle(
                    color = Color.White,
                    fontSize = 18.textDp,
                    fontWeight = FontWeight.W500
                ),
                modifier = Modifier.padding(vertical = 10.dp, horizontal = 16.dp)
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(intrinsicSize = IntrinsicSize.Min)
        ) {
            Spacer(modifier = Modifier.width(width = 16.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(intrinsicSize = IntrinsicSize.Min)
                    .weight(weight = 1f),
            ) {
                Spacer(modifier = Modifier.height(height = 12.dp))
                GameSettingCounterType(
                    painter = painterResource(R.drawable.ic_clock),
                    text = stringResource(R.string.multi_lobby_game_setting_limit_time_title),
                    value = String.format(stringResource(R.string.multi_lobby_game_setting_limit_time_s), gameTimeLimit.seconds.toString()),
                    onClick = { value ->
                        val isAdd = value == 1
                        if (isAdd) {
                            onClickTimeLimit.invoke(gameTimeLimit.add())
                        } else {
                            onClickTimeLimit.invoke(gameTimeLimit.minus())
                        }
                    }
                )
                Spacer(modifier = Modifier.height(height = 12.dp))
                GameSettingCounterType(
                    painter = painterResource(R.drawable.ic_rounds_hash_tilted),
                    text = stringResource(R.string.multi_lobby_game_setting_game_round_title),
                    value = String.format(stringResource(R.string.multi_lobby_game_setting_game_round_s), gameRounds.count.toString()),
                    onClick = { value ->
                        val isAdd = value == 1
                        if (isAdd) {
                            onClickGameRound.invoke(gameRounds.add())
                        } else {
                            onClickGameRound.invoke(gameRounds.minus())
                        }
                    }
                )
                Spacer(modifier = Modifier.height(height = 12.dp))
                GameSettingSelectorType(
                    painter = painterResource(R.drawable.ic_difficulty),
                    text = stringResource(R.string.multi_lobby_game_setting_difficulty_title),
                    selectorList = Difficulty.entries.map { it },
                    selectedGameDifficulty = selectedGameDifficulty,
                    onClick = onClickDifficulty
                )
                Spacer(modifier = Modifier.height(height = 16.dp))
            }
            Spacer(modifier = Modifier.width(width = 16.dp))
        }
    }
}

@Composable
fun GameSettingCounterType(
    painter: Painter,
    text: String,
    value: String,
    onClick: (Int) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            painter = painter,
            contentDescription = null,
            tint = colorResource(R.color.color_DB2777),
            modifier = Modifier.size(size = 24.dp)
        )
        Spacer(modifier = Modifier.width(width = 8.dp))
        Text(
            text = text,
            style = TextStyle(
                color = colorResource(R.color.black),
                fontSize = 14.textDp,
                fontWeight = FontWeight.W500
            ),
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.weight(weight = 1f))
        CounterControl(painter = painterResource(R.drawable.ic_minus), onClick = { onClick.invoke(-1) })
        Spacer(modifier = Modifier.width(width = 4.dp))
        Box(modifier = Modifier.width(width = 44.dp)) {
            Text(
                text = value,
                style = TextStyle(
                    color = colorResource(R.color.black),
                    fontSize = 13.textDp,
                    fontWeight = FontWeight.W700,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.align(alignment = Alignment.Center)
            )
        }
        CounterControl(painter = painterResource(R.drawable.ic_plus), onClick = { onClick.invoke(1) })
    }
}

@Composable
fun CounterControl(painter: Painter, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(size = 24.dp)
            .clip(shape = CircleShape)
            .background(color = colorResource(R.color.color_FCE7F3))
            .noRippleClickable(onClick = onClick)
    ) {
        Icon(
            painter = painter,
            contentDescription = null,
            tint = colorResource(R.color.color_DB2777),
            modifier = Modifier
                .size(size = 18.dp)
                .align(alignment = Alignment.Center)
        )
    }
}

@Composable
fun GameSettingSelectorType(
    painter: Painter,
    text: String,
    selectorList: List<Difficulty>,
    selectedGameDifficulty: Difficulty,
    onClick: (Difficulty) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            painter = painter,
            contentDescription = null,
            tint = colorResource(R.color.color_DB2777),
            modifier = Modifier.size(size = 24.dp)
        )
        Spacer(modifier = Modifier.width(width = 4.dp))
        Text(
            text = text,
            style = TextStyle(
                color = colorResource(R.color.black),
                fontSize = 14.textDp,
                fontWeight = FontWeight.W500
            ),
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.weight(weight = 1f))
        selectorList.forEachIndexed { index, difficulty ->
            DifficultySelector(difficulty = difficulty, selectedGameDifficulty = selectedGameDifficulty, onClick = onClick)
            if (index == selectorList.lastIndex) return@forEachIndexed
            Spacer(modifier = Modifier.width(width = 8.dp))
        }
    }
}

@Composable
fun DifficultySelector(difficulty: Difficulty, selectedGameDifficulty: Difficulty, onClick: (Difficulty) -> Unit) {
    val backgroundColor = if (difficulty.text == selectedGameDifficulty.text) {
        colorResource(R.color.color_DB2777)
    } else {
        colorResource(R.color.color_B5EAEAE8)
    }

    val textColor = if (difficulty.text == selectedGameDifficulty.text) {
        colorResource(R.color.white)
    } else {
        colorResource(R.color.black)
    }
    Card(
        shape = RoundedCornerShape(size = 16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.noRippleClickable(onClick = { onClick.invoke(difficulty) })
    ) {
        Text(
            text = difficulty.text,
            style = TextStyle(
                color = textColor,
                fontSize = 13.textDp,
                fontWeight = FontWeight.W500
            ),
            modifier = Modifier.padding(vertical = 6.dp, horizontal = 10.dp)
        )
    }
}