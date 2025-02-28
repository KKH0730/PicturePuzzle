package com.seno.game.ui.main.component


import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.seno.game.R
import com.seno.game.navigation.NavigationRoute
import com.seno.game.theme.NoRippleTheme

val BottomMenuList = listOf(
    NavigationRoute.HOME,
    NavigationRoute.HOME2,
    NavigationRoute.HOME3,
    NavigationRoute.PROFILE
)

@Composable
fun BottomNavigationBar(navController: NavController, modifier: Modifier = Modifier){
    CompositionLocalProvider(LocalRippleTheme provides NoRippleTheme){
        BottomNavigation(
            backgroundColor = Color.White,
            contentColor = Color.Green,
            modifier = modifier.height(50.dp)
        ) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination

            BottomMenuList.forEach { route ->
                BottomNavigationItem(
                    icon = { Icon(
                        painter = painterResource(id = route.icon ?: R.drawable.ic_launcher_foreground),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )},

                    label = { Text(text = route.routeName, style = TextStyle(fontSize = 10.sp))},
                    selected = currentDestination?.hierarchy?.any { it.route == route.routeName } == true,
                    unselectedContentColor = Color.Yellow,
                    onClick = {
                        navController.navigate(route.routeName) {
                            // Pop up to the start destination of the graph to
                            // avoid building up a large stack of destinations
                            // on the back stack as users select items
                            // 스택이 쌓이는 것을 방지
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            // 스택에 같은 화면이 쌓이는 것을 막음
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
                            // 재선택 시 이전 화면 유지
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}