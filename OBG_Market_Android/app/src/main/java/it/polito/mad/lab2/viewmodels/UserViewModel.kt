package it.polito.mad.lab2.viewmodels

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.*
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import it.polito.mad.lab2.R.*
import it.polito.mad.lab2.classes.Destination
import it.polito.mad.lab2.classes.ImageCaption
import it.polito.mad.lab2.classes.UserAppRate
import it.polito.mad.lab2.classes.UserInfo
import it.polito.mad.lab2.repositories.Repository
import java.lang.Exception


class UserViewModel: ViewModel(), Observable{

    private val repo = Repository
    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var auth: FirebaseAuth
    lateinit var account: GoogleSignInAccount

    private val currentUser : MutableLiveData<UserInfo> by lazy {
        repo.getUser(auth.currentUser?.uid!!)
    }

    var user = MutableLiveData<UserInfo>()

    var map_UserId_User : MutableLiveData<Map<String, UserInfo>> = MutableLiveData()

    fun getToken (userId: String):Boolean{
        return repo.getToken(userId)
    }

    fun getCurrentUser() : LiveData<UserInfo>{
        return currentUser
    }

    fun getUser(userId:String): LiveData<UserInfo>{
        user = repo.getUser(userId)

        return user
    }

    fun setUser(userId:String, usr:UserInfo){
        repo.setUser(userId,usr)
    }

    /****************************
     ***  Managing live Data  ***
     ****************************/
    @Bindable
    var profileImg_value = MutableLiveData<String>(
        Uri.parse("android.resource://it.polito.mad.lab2/" + drawable.avatar1).toString()) //as LiveData<String>

    @Bindable
    var fullName_value = MutableLiveData<String>() //as LiveData<String>

    @Bindable
    var nickName_value = MutableLiveData<String>() //as LiveData<String>

    //User email
    @Bindable
    var email_value = MutableLiveData<String>() //as LiveData<String>

    //User Location
    @Bindable
    var location_value = MutableLiveData<String>() //as LiveData<String>


    // --> Disk icon actions after pressing
    /********************************************************
     *************     Managing the camera    ***************
     ********************************************************/

    fun gallery(ctx:Context,activity: Activity) {
        ImageCaption.imageManager(ctx,activity,
            "gallery",
            Destination.EDIT_PROFILE_FRAGMENT, profileImg_value
        )
    }

    fun camera(ctx:Context,activity: Activity) {
        ImageCaption.imageManager(ctx,activity,
            "camera",
            Destination.EDIT_PROFILE_FRAGMENT,
            profileImg_value
        )
    }


    // --> Disk icon actions after pressing
    /****************************************************
     *************     Saving the data    ***************
     ****************************************************/

    // Function that checks the information provided by the user
    // If all the information is correct, then saves the data
    fun save(latitude:Double, longitude:Double): String {

        if (listOf(
                profileImg_value.value,
                fullName_value.value,
                nickName_value.value,
                email_value.value,
                location_value.value
            ).all { !it.isNullOrBlank() } && latitude != null && longitude !=null
            && android.util.Patterns.EMAIL_ADDRESS.matcher(email_value.value).matches())
        {
            Log.e("DebugLab3", "HERE-1")

            setUser(auth.currentUser?.uid!!,
                UserInfo( profileImg_value.value!!,
                fullName_value.value!!,
                nickName_value.value!!,
                email_value.value!!,
                location_value.value!!,
                latitude,
                longitude))

            return "Ok"

        } else {
            Log.e("DebugLab3", "HERE-2")
            Log.e("DebugLab3", user.value?.fullName.toString())
            if (fullName_value.value!!.isBlank() ?: false) { return "Name"}
            else if (nickName_value.value!!.isBlank() ?: false) { return "Nick" }
            else if (email_value.value!!.isBlank() ?: false || !android.util.Patterns.EMAIL_ADDRESS.matcher(email_value.value).matches()) { return "Email"}
            else if (location_value.value!!.isBlank() ?: false) { return "Location"}
            else if (latitude == null){return "coord"}
            else return ""
        }
    }

    private val callbacks: PropertyChangeRegistry by lazy { PropertyChangeRegistry()}

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        callbacks.add(callback)
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        callbacks.remove(callback)
    }


    // get realtime updates from firebase regarding list of user! Needed for retrieving easily the nickname of a certain
    // user given the id stored in the field interested user of an item
    fun getUserList(): LiveData<Map<String,UserInfo>> {
        repo.getUsersList().addSnapshotListener { userList, e ->
            if (e != null) {
                Log.w("Lab3Debug", "Listen failed.", e)
                map_UserId_User.value = null
                return@addSnapshotListener
            }

            var map : MutableMap<String, UserInfo> = mutableMapOf()
            Log.e("Lab3Debug","List received")
            for (user in userList!!) {
                //Log.e("Lab3Debug","user received ${user.id} : $user")
                var usr= user.toObject(UserInfo::class.java)
                map.put(user.id,usr)
            }
            map_UserId_User.value = map
        }

        return map_UserId_User
    }

    fun saveRateForCurrentApp(userAppRate: UserAppRate){
        Repository
            .saveAppRate(auth.currentUser?.uid!!,userAppRate)
            .addOnSuccessListener {
                Log.d("Lab3Debug", "Rate added successfully")
            }
            .addOnFailureListener { e ->
                Log.e("Lab3Debug", "Rate adding failure", e)
                throw Exception()
            }
    }



}

