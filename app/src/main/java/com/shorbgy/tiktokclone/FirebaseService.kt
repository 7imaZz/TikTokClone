package com.shorbgy.tiktokclone

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

fun getAllVideos(): Query {
    val db = FirebaseFirestore.getInstance()
    return db.collection("videos")
}