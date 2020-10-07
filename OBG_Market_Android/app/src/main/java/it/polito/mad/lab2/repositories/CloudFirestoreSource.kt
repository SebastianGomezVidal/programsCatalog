package it.polito.mad.lab2.repositories

import android.net.Uri
import android.util.Log
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.storage.ktx.storage
import it.polito.mad.lab2.classes.Adds
import it.polito.mad.lab2.classes.UserAppRate
import it.polito.mad.lab2.classes.UserInfo
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream


//class that handle the communication with firestore
class CloudFirestoreSource {

    private val db = Firebase.firestore
    // Create a storage reference from our app
    private val storageRef = Firebase.storage.reference


    fun getToken(userId:String):Boolean {
        val data = db.collection("users").document(userId)
        if (data.equals(true)) return true
        else return false
    }

    /**
     * Given the userId it returns the live data associated to that user.
     * It receives also a callback to be called on success and a callback to be called on failure
     */
    fun getUser(userId:String): DocumentReference {
        Log.e("Lab3Debug","userId in firestore $userId")
        return db.collection("users").document(userId)
    }


    /**
     * Given a UserInfo class, it set/update the user with the same id
     * It return true if everything is ok, otherwise, it returns false
     */
    fun setUser(userId: String, userInfo: UserInfo) : Boolean{
        //TODO(for the moment I not pass to this function success and failure calllback because I want to understand if there
        // are better ways to return on the set operation the results)

        //TODO(for the moment I don't use transaction because I suppose that the user can be edited only on one device at a time)
        db
            .collection("users")
            .document(userId)
            .set(userInfo)
            .addOnSuccessListener {
                Log.e("Assignment3Debug","User saved")
                //TODO understand how to return data to the repository;
            }
            .addOnFailureListener {
                exception ->
                Log.e("Assignment3Debug","Error in setting user",exception)
                //TODO understand how to return error to the repository;
            }
        return true
    }


    /**
     * Given the itemId it returns the live data associated to that user.
     * It receives also a callback to be called on success and a callback to be called on failure
     */

    fun getItemGivenUserAndId(userId: String, itemId:String) : DocumentReference{

        return db
            .collection("users")
            .document(userId)
            .collection("itemsOnSaleList")
            .document(itemId)
    }

    /**
     * Given a Adds class, it set/update the user with the same id
     * It return true if everything is ok, otherwise, it returns false

    //Not really sure if we should also set a parameter of the function the id of the user
    //who is publishing or updating any add.
    fun setItemById(userId: String,add:Adds){
        db
            .collection("users")
            .document(userId)
            .collection("itemsOnSaleList")
            .document("itemid1")
            .set(add)
            .addOnSuccessListener {

            }
            .addOnFailureListener{

            }

    }*/

    fun getItemsList(userId: String):CollectionReference{
        return db
            .collection("users")
            .document(userId)
            .collection("itemsOnSaleList")
    }

    fun makeAdd(adds:Adds): Task<Void> {
        var newDocRef = db.collection("users").document(adds.ownerId)
            .collection("itemsOnSaleList").document()

        return db.runTransaction {
            val autogenItemId = it.get(newDocRef).id
            adds.addsId = autogenItemId
            it.set(newDocRef,adds)

            null
        }
    }

    fun editAdd(adds:Adds): Task<Void> {
        var docRef = db.collection("users").document(adds.ownerId)
            .collection("itemsOnSaleList").document(adds.addsId)

        val updates = hashMapOf<String, Any?>(
            "category" to adds.category,
            "description" to adds.description,
            "expireDate" to adds.expireDate,
            "imgPath" to adds.imgPath,
            "location" to adds.location,
            "price" to adds.price,
            "title" to adds.title,
            "status" to adds.status,
            "latitude" to adds.latitude,
            "longitude" to adds.longitude,
            "buyerId" to adds.buyerId
        )

        return docRef.update(updates)
    }

    fun writeInterestOnDb(user:String, adds: Adds): Task<Void> {

        var documentReference = db.collection("users").document(adds.ownerId)
            .collection("itemsOnSaleList").document(adds.addsId)

        return documentReference.update("interestedUsers", FieldValue.arrayUnion(user))

    }

    fun removeInterestFromDb(user:String, adds: Adds): Task<Void> {

        var documentReference = db.collection("users").document(adds.ownerId)
            .collection("itemsOnSaleList").document(adds.addsId)

        return documentReference.update("interestedUsers", FieldValue.arrayRemove(user))

    }

    fun getUsersList() : CollectionReference{
        return db
            .collection("users")
    }

    fun uploadImageOnFirebaseStorage(option:String,imageUri:String){
        if (option == "gallery") {
            Log.e("Lab3Debug","Gallery")
            var file = Uri.parse(imageUri)
            val imageRef = storageRef.child("images/${file.lastPathSegment}")
            /*Log.e("Lab3Debug","complete path : $file")
            Log.e("Lab3Debug","only filename : ${file.lastPathSegment}")
            val uploadTask = imageRef.putFile(file)*/

            val stream = FileInputStream(File(imageUri))
            val uploadTask = imageRef.putStream(stream)

            // Register observers to listen for when the download is done or if it fails
            uploadTask.addOnFailureListener {
                // Handle unsuccessful uploads
                Log.e("Lab3Debug", "Error during image upload", it)
                throw it
            }.addOnSuccessListener {
                // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
                // ...
                Log.e("Lab3Debug", "Successfully uploaded ${it.bytesTransferred} Byte ")
            }
        }
        else if (option == "camera"){
            Log.e("Lab3Debug","Gallery")
            var file = Uri.parse(imageUri)
            val imageRef = storageRef.child("images/${file.lastPathSegment}")
            Log.e("Lab3Debug","complete path : $file")
            Log.e("Lab3Debug","only filename : ${file.lastPathSegment}")
            val uploadTask = imageRef.putFile(file)

            //val stream = FileInputStream(File(imageUri))
            //val uploadTask = imageRef.putStream(stream)

            // Register observers to listen for when the download is done or if it fails
            uploadTask.addOnFailureListener {
                // Handle unsuccessful uploads
                Log.e("Lab3Debug", "Error during image upload", it)
                throw it
            }.addOnSuccessListener {
                // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
                // ...
                Log.e("Lab3Debug", "Successfully uploaded ${it.bytesTransferred} Byte ")
            }
        }

    }

    fun saveRate(ownerId:String, itemId:String, userRating:Float, comment:String): Task<Void> {
        var docRef = db.collection("users").document(ownerId)
            .collection("itemsOnSaleList").document(itemId)

        val updates = hashMapOf<String, Any?>(
            "rating" to userRating,
            "comment" to comment
        )

        return docRef.update(updates)
    }

    fun saveAppRate(userId: String, userAppRate: UserAppRate): Task<Void> {
        var docRef = db.collection("usersAppRate").document(userId)


        return docRef.set(userAppRate)
    }

}