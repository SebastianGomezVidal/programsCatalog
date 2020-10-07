package it.polito.mad.lab2.classes

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import it.polito.mad.lab2.R
import it.polito.mad.lab2.viewmodels.UserViewModel

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

    @JvmStatic
    fun closeApp(context: Context, userViewModel: UserViewModel) {

        //Logout of the app

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id)) //request a client token
            .requestEmail()
            .build()

        userViewModel.mGoogleSignInClient = GoogleSignIn.getClient(context, gso)
        userViewModel.mGoogleSignInClient.signOut()
        userViewModel.auth.signOut()
        Toast.makeText(context, "The App will close in 5 seconds", Toast.LENGTH_SHORT).show()
    }
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

