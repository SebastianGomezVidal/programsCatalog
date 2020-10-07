package it.polito.mad.lab2.repositories

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import it.polito.mad.lab2.classes.Adds
import it.polito.mad.lab2.classes.UserAppRate
import it.polito.mad.lab2.classes.UserInfo

//I think it should be unique for both user and item
//made singleton using object!
object Repository {

    //reference to the cloudFirestoreSource
    private val cloudFirestoreSource = CloudFirestoreSource()

    //private lateinit var uid:String //userId of the user actually logged in

    fun getToken(userId:String):Boolean{
        return cloudFirestoreSource.getToken(userId)
    }

    /**
     * Given the userId it returns the live data associated to that user
     * Beware, the user can be only one at a time and the getUser retrieve only the userInfo of the active
     * User
     */
    fun getUser(userId:String) : MutableLiveData<UserInfo>{

        Log.e("Lab3Debug","getUser in repository")
        var userData = MutableLiveData<UserInfo>()

        val docRef = cloudFirestoreSource.getUser(userId)

        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("Lab3Debug", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                Log.d("Lab3Debug", "Current data: ${snapshot.data}")
                val returnedUser = snapshot.toObject(UserInfo::class.java)
                userData.value = returnedUser
                //Log.d("Lab3Debug","Repository: ${userData.value}\"")
            } else {
                Log.d("Lab3Debug", "Current data: null")
            }
        }

        return userData
    }

    /**
     * Given a UserInfo class, it set/update the user with the same id
     * It return true if everything is ok, otherwise, it returns false
     */
    fun setUser(userId:String, userInfo: UserInfo) : Boolean{
        //TODO("Understand if returning true or false is the best way to make the viewModel aware of the result")

        cloudFirestoreSource.setUser(userId,userInfo)

        return true
    }

    /**
     * Given the userId and an itemId it returns the live data associated to that item
     */
    fun getItemGivenUserAndId(userId:String, itemId:String) : DocumentReference{
        return cloudFirestoreSource.getItemGivenUserAndId(userId,itemId)
    }


    /**
     * Given an Adds class, it set/update the add of an user with the same id
     * It return true if everything is ok, otherwise, it returns false
     *

    fun saveItem(userId:String, add: Adds){
        cloudFirestoreSource.setItemById(userId,add)

        //NOT FINISHED 
    }*/

    fun getCurrentUserAddsList() : CollectionReference{
        Log.e("Lab3Debug", "uid in getCurrentUserAddsList : ${FirebaseAuth.getInstance().currentUser?.uid}")
        return cloudFirestoreSource.getItemsList(FirebaseAuth.getInstance().currentUser?.uid!!)
    }

    fun makeAdd(adds:Adds): Task<Void> {
        return cloudFirestoreSource.makeAdd(adds)
    }

    fun editAdd(adds:Adds): Task<Void> {
        return cloudFirestoreSource.editAdd(adds)
    }

    fun writeInterestOnDb(user:String, adds: Adds): Task<Void> {
        return cloudFirestoreSource.writeInterestOnDb(user,adds)
    }

    fun removeInterestFromDb(user:String, adds: Adds): Task<Void> {
        return cloudFirestoreSource.removeInterestFromDb(user,adds)
    }

    fun getUsersList() : CollectionReference{
        return cloudFirestoreSource.getUsersList()
    }

    //ALEJANDRO: New method to get the list of items of any user
    fun getAnyUserAddsList(userId: String) : CollectionReference{
        return cloudFirestoreSource.getItemsList(userId)
    }

    fun uploadImageOnFirebaseStorage(option:String,imageUri:String){
        return cloudFirestoreSource.uploadImageOnFirebaseStorage(option,imageUri)
    }

    fun saveRate(ownerId:String, itemId:String, userRating:Float, comment:String): Task<Void> {
        return cloudFirestoreSource.saveRate(ownerId, itemId, userRating, comment)
    }

    fun saveAppRate(userId:String, userAppRate: UserAppRate): Task<Void> {
        return cloudFirestoreSource.saveAppRate(userId, userAppRate)
    }
}