package com.shorbgy.tiktokclone

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Video(
    val title: String = "",
    val desc: String = "",
    val url: String = ""
): Parcelable
