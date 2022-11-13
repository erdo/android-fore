package co.early.fore.kt.core.ui

import android.view.View


fun View.showOrInvisible(show: Boolean) {
    this.visibility = if (show) View.VISIBLE else View.INVISIBLE
}

fun View.showOrGone(show: Boolean) {
    this.visibility = if (show) View.VISIBLE else View.GONE
}
