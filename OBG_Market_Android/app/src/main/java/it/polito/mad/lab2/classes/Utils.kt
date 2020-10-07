package it.polito.mad.lab2.classes

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

fun hideKeyboard(activity: Activity) {
    val inputMethodManager =
        activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    // Check if no view has focus
    val currentFocusedView = activity.currentFocus
    currentFocusedView?.let {
        inputMethodManager.hideSoftInputFromWindow(
            currentFocusedView.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }
}

enum class ArrivesToEditItem{
    FROM_ITEM_LIST,
    FROM_ITEM_DETAILS
}

object Utils{
    lateinit var arrivesToEditItem:ArrivesToEditItem
}

fun loadImageFromRemote(imgPath:String, ctx:Context, imgView:ImageView){

    Log.e("Lab3Debug","loadImageFromRemote")

    val storageReference = Firebase.storage.reference
    var file = Uri.parse(imgPath)
    val imageRef = storageReference.child("images/${file.lastPathSegment}")

    // Download directly from StorageReference using Glide
    // (See MyAppGlideModule for Loader registration)
    Glide.with(ctx)
        .load(imageRef)
        .into(imgView)
}