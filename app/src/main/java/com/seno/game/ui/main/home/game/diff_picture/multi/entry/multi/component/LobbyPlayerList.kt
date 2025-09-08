package com.seno.game.ui.main.home.game.diff_picture.multi.entry.multi.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.seno.game.R
import com.seno.game.extensions.textDp
import com.seno.game.manager.AccountManager
import com.seno.game.model.Player
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun LobbyPlayerList(
    ownerUid: String,
    players: List<Player>,
    modifier: Modifier = Modifier
) {
    val lazyListState = rememberLazyListState()
    Card(
        shape = RoundedCornerShape(size = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
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
                    text = String.format(stringResource(R.string.multi_room_player_count_s), players.size.toString()),
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 18.textDp,
                        fontWeight = FontWeight.W400
                    ),
                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 16.dp)
                )
            }
            LazyColumn(
                state = lazyListState,
                contentPadding = PaddingValues(vertical = 16.dp, horizontal = 16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(
                    count = players.size,
                    key = { index -> players[index].uid }
                ) { index ->
                   Column {
                       Row(
                           verticalAlignment = Alignment.CenterVertically,
                           modifier = Modifier
                               .fillMaxWidth()
                               .height(height = 50.dp)
                       ) {
                           if (players[index].profileUri.isEmpty()) {
                               Box(
                                   modifier = Modifier
                                       .size(size = 40.dp)
                                       .border(
                                           border = BorderStroke(
                                               width = 2.dp,
                                               color = colorResource(R.color.color_D3CAC6C6)
                                           ),
                                           shape = CircleShape
                                       )
                               ) {
                                   Image(
                                       painter = painterResource(id = R.drawable.ic_rounded_person),
                                       contentDescription = null,
                                       contentScale = ContentScale.Crop,
                                       colorFilter = ColorFilter.tint(color = colorResource(R.color.color_D3CAC6C6)),
                                       modifier = Modifier
                                           .size(size = 36.dp)
                                           .align(alignment = Alignment.Center)
                                   )
                               }
                           } else {
                               GlideImage(
                                   imageModel = players[index].profileUri,
                                   contentScale = ContentScale.Crop,
                                   placeHolder = painterResource(id = R.drawable.ic_profile_not_login),
                                   modifier = Modifier
                                       .size(size = 40.dp)
                                       .clip(shape = CircleShape)
                                       .background(color = Color.Black)
                               )
                           }
                           Spacer(modifier = Modifier.width(width = 14.dp))
                           Text(
                               text = players[index].nickname,
                               style = TextStyle(
                                   color = Color.Black,
                                   fontSize = 14.textDp,
                                   fontWeight = FontWeight.W500
                               )
                           )
                           Spacer(modifier = Modifier.weight(weight = 1f))
                           if (players[index].uid == ownerUid) {
                               if (players[index].uid == AccountManager.firebaseUid) {
                                   Image(
                                       painter = painterResource(id = R.drawable.img_game_start),
                                       contentDescription = null,
                                       contentScale = ContentScale.Fit,
                                       modifier = Modifier
                                           .size(size = 50.dp)
                                           .rotate(degrees = 45f)
                                   )
                               } else {
                                   Image(
                                       painter = painterResource(id = R.drawable.img_letter_room_owner),
                                       contentDescription = null,
                                       contentScale = ContentScale.Fit,
                                       modifier = Modifier.size(size = 50.dp)
                                   )
                               }
                           }
                       }
                       if (index != players.lastIndex) {
                           Spacer(modifier = Modifier.height(height = 8.dp))
                           Spacer(
                               modifier = Modifier
                                   .fillMaxWidth()
                                   .height(height = 1.dp)
                                   .background(color = colorResource(R.color.color_B5EAEAE8))
                           )
                           Spacer(modifier = Modifier.height(height = 8.dp))
                       }
                   }
                }
            }
        }
    }
}