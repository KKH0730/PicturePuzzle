package com.seno.game.ui.game.humminjeongeum

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import com.seno.game.R
import com.seno.game.extensions.getString
import com.seno.game.extensions.textDp
import com.seno.game.extensions.toast
import com.seno.game.ui.game.GameViewModel
import com.seno.game.ui.game.component.GamePrepareView
import com.seno.game.util.KeyboardState
import com.seno.game.util.WordValidationCheckUtil
import com.seno.game.util.keyboardAsState
import kotlinx.coroutines.delay
import java.util.*

val ConsonantList = listOf("ㄱ", "ㄴ", "ㄷ", "ㄹ", "ㅁ", "ㅂ", "ㅅ", "ㅇ", "ㅈ", "ㅊ", "ㅋ", "ㅌ", "ㅍ", "ㅎ")

@SuppressLint("MutableCollectionMutableState")
@OptIn(ExperimentalLifecycleComposeApi::class, ExperimentalComposeUiApi::class)
@Composable
fun HunMinJeongEumScreen(
    viewModel: GameViewModel,
    consonantLength: Int = 2,
) {
    var prepareVisible by remember { mutableStateOf(true) }
    val consonantSetState by remember { mutableStateOf(HashSet<String>()) }
    val consonant: String = getSuggestionConsonant(
        consonantLength = consonantLength,
        consonantSet = consonantSetState,
        onCreatedConsonant = { consonantSetState.add(it) }
    )
    var suggestionConsonant by remember { mutableStateOf(consonant) }
    var input by remember { mutableStateOf("") }
    var response by remember { mutableStateOf("") }
    var humMinJeongEumAnswerCount by remember { mutableStateOf(0) }
    val context = LocalContext.current

    val isKeyboardOpen by keyboardAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    LaunchedEffect(key1 = isKeyboardOpen) {
        if (isKeyboardOpen == KeyboardState.Closed) {
            keyboardController?.show()
        }
    }

    LaunchedEffect(key1 = viewModel) {
        viewModel.searchResultMessage.collect { resultMessage ->
            if (resultMessage == R.string.hummin_correct) {
                response = ""
                input = ""
                humMinJeongEumAnswerCount += 1

                val nextSuggestionConsonant = getSuggestionConsonant(
                    consonantLength = consonantLength,
                    consonantSet = consonantSetState,
                    onCreatedConsonant = { consonantSetState.add(it) }
                )

                suggestionConsonant = nextSuggestionConsonant
            } else {
                response = getString(resultMessage)
            }
        }
    }

    LaunchedEffect(key1 = viewModel) {
        viewModel.message.collect {
            context.toast(messageResId = it)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            TopTimer(
                prepareVisible = prepareVisible,
                onFinishedGame = { TODO() },
                modifier = Modifier.align(alignment = Alignment.End)
            )
            Spacer(modifier = Modifier.height(100.dp))
            SuggestionContainer(
                suggestionConsonant = suggestionConsonant,
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(50.dp))
            ResponseContainer(
                response = response,
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.weight(1f))
            InputContainer(
                prepareVisible = prepareVisible,
                answer = input,
                onValueChanged = {
                    if (response == getString(R.string.hummin_correct) ||
                        response == getString(R.string.hummin_incorrect)
                    ) {
                        response = ""
                    }
                    input = it
                },
                onSubmit = {
                    if (WordValidationCheckUtil.checkValidation(input, suggestionConsonant)) {
                        viewModel.searchWord(input = input)
                    } else {
                        context.toast(messageResId = R.string.hummin_mismatch)
                    }
                }
            )
        }
        AnimatedVisibility(
            visible = prepareVisible,
            exit = ExitTransition.None
        ) {
            GamePrepareView { prepareVisible = false }
        }
    }
}

@Composable
private fun TopTimer(
    prepareVisible: Boolean,
    onFinishedGame: () -> Unit,
    modifier: Modifier,
) {
    var timber by remember { mutableStateOf(20) }

    if (!prepareVisible) {
        LaunchedEffect(key1 = timber) {
            delay(1000)
            if (timber - 1 == 0) {
                onFinishedGame.invoke()
                return@LaunchedEffect
            } else {
                timber -= 1
            }
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.padding(end = 15.dp, top = 15.dp)
    ) {
        Text(
            text = timber.toString(),
            style = TextStyle(
                fontSize = 24.textDp,
                color = Color.Black
            )
        )
    }
}

@Composable
private fun SuggestionContainer(
    suggestionConsonant: String,
    modifier: Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier
    ) {
        suggestionConsonant.forEach {
            SuggestionConsonantBox(it.toString())
        }
    }
}

@Composable
private fun SuggestionConsonantBox(consonant: String) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .border(
                BorderStroke(
                    width = 0.5.dp,
                    color = Color(0xFFD1CDCD)
                ),
                shape = RectangleShape
            )
            .padding(20.dp)
    ) {
        Text(
            text = consonant,
            style = TextStyle(
                fontSize = 50.textDp,
                color = Color.Black
            )
        )
    }
}

@Composable
private fun ResponseContainer(response: String, modifier: Modifier) {
    Text(
        text = response,
        style = TextStyle(
            fontSize = 24.textDp,
            color = Color.Black
        ),
        modifier = modifier
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun InputContainer(
    prepareVisible: Boolean,
    answer: String,
    onValueChanged: (String) -> Unit,
    onSubmit: () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }

    Row(
        modifier = Modifier
            .height(IntrinsicSize.Max)
            .background(color = Color.Yellow)

    ) {
        TextField(
            value = answer,
            onValueChange = {
                if (!prepareVisible) {
                    onValueChanged(it)
                }
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp)
                )
            },
            maxLines = 1,
            modifier = Modifier
                .fillMaxHeight()
                .focusRequester(focusRequester)
                .weight(0.7f)
        )
        TextButton(
            onClick = onSubmit,
            modifier = Modifier
                .fillMaxHeight()
                .weight(0.3f)
        ) {
            Text(
                text = stringResource(id = R.string.hummin_submit),
                style = TextStyle(
                    fontSize = 18.textDp,
                    color = Color.Black
                )
            )
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

private fun getSuggestionConsonant(
    consonantLength: Int,
    consonantSet: HashSet<String>,
    onCreatedConsonant: (String) -> Unit,
): String {
    val result = StringBuilder()
    val random = Random()

    while (true) {
        val consonant = random.nextInt(13)
        result.append(ConsonantList[consonant])

        if (result.length == consonantLength) {
            onCreatedConsonant.invoke(result.toString())
            return result.toString()
        } else {
            if (consonantSet.contains(result.toString())) {
                result.clear()
            }
        }
    }
}