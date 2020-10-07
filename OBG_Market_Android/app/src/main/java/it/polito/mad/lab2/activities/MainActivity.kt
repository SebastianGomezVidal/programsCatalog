package it.polito.mad.lab2.activities

import android.Manifest
import android.app.Dialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.messaging.FirebaseMessaging
import it.polito.mad.lab2.R
import it.polito.mad.lab2.classes.*
import it.polito.mad.lab2.viewmodels.ItemsViewModel
import it.polito.mad.lab2.viewmodels.OnSaleItemsViewModel
import it.polito.mad.lab2.viewmodels.UserViewModel
import java.lang.Exception



class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    companion object {
        public val SHARED_PREFS: String = "sharedPrefs"
        private val RC_SIGN_IN:Int = 1000
    }

    private val userViewModel: UserViewModel by viewModels()
    private val itemsViewModel: ItemsViewModel by viewModels()
    private val onSaleItemsViewModel:OnSaleItemsViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_adds,
                R.id.nav_profile,
                R.id.nav_editProfile,
                R.id.nav_itemOnSale,
                R.id.nav_itemOfInterestListFragment,
                R.id.nav_boughtItemsListFragment
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        createNotificationChannel()

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) //request a client token
            .requestEmail()
            .build()

        // Build a GoogleSignInClient with the options specified by gso.
        userViewModel.mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //Get a shared instance of Firebase Auth
        userViewModel.auth = FirebaseAuth.getInstance()

        //handle the feedback navigation button
        navView.setNavigationItemSelectedListener(object : NavigationView.OnNavigationItemSelectedListener {
                override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
                        val id: Int = menuItem.getItemId()
                        //it's possible to do more actions on several items, if there is a large amount of items I prefer switch(){case} instead of if()
                        if (id == R.id.nav_feedback) {
                                //Toast.makeText(applicationContext, "FeedBack", Toast.LENGTH_SHORT).show()
                                rateApp()
                            }
                        else{
                                //This is for maintaining the behavior of the Navigation view
                                NavigationUI.onNavDestinationSelected(menuItem, navController)
                            }
                        //This is for closing the drawer after acting on it
                        drawerLayout.closeDrawer(GravityCompat.START)
                        return true
                   }
            })

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            } else {
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            }
        }

    }

    //if the received account is null, the user is not logged and so it is show the dialog for signing in via the intent
    //otherwise the data are retrieved from the repository!
    private fun updateUI(account: FirebaseUser?) {
        if(account == null){ //the user is not logged in
            val signInIntent: Intent = userViewModel.mGoogleSignInClient.getSignInIntent()
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
        else{
            //TODO to be implemented! ->  update the UI and set the user fields
            Toast.makeText(this, "Logged in successfully",Toast.LENGTH_SHORT)
            userViewModel.getCurrentUser()
            userViewModel.getUserList()
            //handling of notification
            Log.e("Lab3Debug8","uid : ${FirebaseAuth.getInstance().currentUser!!.uid}")
            FirebaseMessaging.getInstance().subscribeToTopic(FirebaseAuth.getInstance().currentUser!!.uid)
                .addOnCompleteListener { task ->
                    //var msg = "successful subscription"
                    if (!task.isSuccessful) {
                        //msg = getString(R.string.msg_subscribe_failed)
                        throw Exception()
                    }
                    Log.e("Lab3Debug8", "Successful subscription")
                    //Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                }

            val drawerLayout:DrawerLayout = findViewById(R.id.drawer_layout)
            drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener{
                override fun onDrawerStateChanged(newState: Int) {}

                override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                    val navView: NavigationView = drawerView.findViewById(R.id.nav_view)
                    val headerView = navView.getHeaderView(0)

                    if(userViewModel.getCurrentUser().value != null) {  // userVIewModel.getCurrentUser() is null
                        // when creating a new user and the various fields like
                        //fullname, email.. are also null causing the app crashing
                        val user = userViewModel.getCurrentUser().value!!

                        loadImageFromRemote(user.profileImg, applicationContext, headerView.findViewById(R.id.imgView_nav_header))
                        //headerView.findViewById<ImageView>(R.id.imgView_nav_header).setImageURI(Uri.parse(it.profileImg))
                        headerView.findViewById<TextView>(R.id.tv_nav_header_title).text = user.fullName
                        headerView.findViewById<TextView>(R.id.tv_nav_header_subtitle).text = user.email
                    }
                }

                override fun onDrawerClosed(drawerView: View) {}

                override fun onDrawerOpened(drawerView: View) {}

            })

        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d("Lab3Debug", "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("Lab3Debug", "Google sign in failed", e)
                Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_SHORT).show()
                updateUI(null)
            }
        }
        else
            ImageCaption.onActivityResult(requestCode, resultCode, data)
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        userViewModel.auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("Lab3Debug", "signInWithCredential:success -> uid : ${userViewModel.auth.currentUser?.uid}")
                    val user = userViewModel.auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("Lab3Debug", "signInWithCredential:failure", task.exception)
                    Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }

            }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] ==
                PackageManager.PERMISSION_GRANTED
            ) {
                if ((ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) ==
                            PackageManager.PERMISSION_GRANTED)
                ) {
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "The app will not work correctly", Toast.LENGTH_SHORT).show()
            }
            return
        }

        ImageCaption.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    //at the on start retrieve the current user and update the ui if the user is present!
    override fun onStart() {
        super.onStart()
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        val currentUser = userViewModel.auth.currentUser
        updateUI(currentUser)
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Firebase Notification channel"
            val descriptionText = "Notification received in this channel are all" +
                    " related to adds you are interested in or to adds you have bought"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel =
                NotificationChannel(
                    getString(R.string.default_notification_channel_id),
                    name,
                    importance
                ).apply {
                    description = descriptionText
                }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun rateApp(){
        // Get the layout inflater
        val inflater = layoutInflater;
        val rateView = inflater.inflate(R.layout.rate_user_dialog, null)
        val rateBarTitle: MaterialTextView = rateView.findViewById(R.id.tv_rate_user_title)
        rateBarTitle.text = "Rate the App"

        MaterialAlertDialogBuilder(this)
            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            .setView(rateView)
            .setPositiveButton("Save"){
                    dialog: DialogInterface, which: Int ->

                    val appRating = (dialog as Dialog).findViewById<RatingBar>(R.id.ratingBar_user).rating
                    val comment = (dialog as Dialog).findViewById<TextInputEditText>(R.id.tIET_Comment)?.text.toString()

                    Log.e("Lab4Debug1","Rating : $appRating")
                    Log.e("Lab4Debug1","Comment : $comment")


                    userViewModel.saveRateForCurrentApp(UserAppRate(appRating,comment))
                    Toast.makeText(applicationContext,"App Rating is being saved",Toast.LENGTH_SHORT).show()
                    dialog.dismiss()

                    //super.onBackPressed()
                }
            .setNegativeButton("Cancel"){
                        dialog: DialogInterface, which: Int ->
                    //Toast.makeText(context,"CANCELED",Toast.LENGTH_SHORT)
                    Toast.makeText(applicationContext,"App Rating not saved",Toast.LENGTH_SHORT).show()
                    dialog.cancel()
                }
            .create().show()



    }

}
