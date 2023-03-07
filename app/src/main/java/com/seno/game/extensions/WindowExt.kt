import android.annotation.SuppressLint
import android.os.Build
import android.view.*
import android.view.WindowInsets.Type.systemBars
import androidx.core.view.WindowInsetsControllerCompat

@SuppressLint("WrongConstant")
fun Window.hideNavigationBar() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        setDecorFitsSystemWindows(false)

        val insetsControllerCompat = WindowInsetsControllerCompat(this, this.decorView)
        insetsControllerCompat.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        insetsControllerCompat.hide(systemBars())
    } else {
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
    }
}