package it.polito.mad.lab2.viewmodels

import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.google.firebase.auth.FirebaseAuth
import it.polito.mad.lab2.classes.Adds
import it.polito.mad.lab2.classes.ItemStatus
import it.polito.mad.lab2.repositories.Repository
import org.json.JSONException
import org.json.JSONObject
import java.lang.Exception

class ItemsViewModel : ViewModel() {

    private val repo = Repository

    var currentUserAddsList : MutableLiveData<List<Adds>> = MutableLiveData()
    var itemDetailAdds : MutableLiveData<Adds> = MutableLiveData()
    var currentUserRating: MutableLiveData<Float> = MutableLiveData()

    var currentItem_itemId:String = ""
    var currentItem_ownerId:String = ""

    //send request for notification
    private val FCM_API = "https://fcm.googleapis.com/fcm/send"
    private val serverKey =
        "key=" + "AAAAn3zpLDc:APA91bG5fPFsEoqIpE7w_Wmt7ganmU_kQJckONMpVOchvkcKvoXu9VeqCPRpvd3Y15z4dI4G4S9BPBP4NcS7AUS0Zgrm3B6HFacc8O4uD-JYZYLnCEbsDS0a1iaG2zuvi78Y4eRpt-ck"
    private val contentType = "application/json"

    // get realtime updates from firebase regarding saved addresses
    fun getCurrentUserItemsList(): LiveData<List<Adds>> {
        repo.getCurrentUserAddsList().addSnapshotListener { value, e ->
            if (e != null) {
                Log.w("Lab3Debug", "Listen failed.", e)
                currentUserAddsList.value = null
                return@addSnapshotListener
            }

            var listOfAdds : MutableList<Adds> = mutableListOf()
            Log.e("Lab3DebugItems","List received")
            for (doc in value!!) {
                //Log.e("Lab3Debug","item received : $doc")
                var item = doc.toObject(Adds::class.java)
                listOfAdds.add(item)
            }
            currentUserAddsList.value = listOfAdds
        }

        return currentUserAddsList
    }

    // get realtime updates from firebase regarding saved addresses
    fun getItemGivenUserAndId(userId:String, itemId:String): LiveData<Adds> {
        repo.getItemGivenUserAndId(userId,itemId).addSnapshotListener { value, e ->
            if (e != null) {
                Log.w("Lab3Debug", "Listen failed.", e)
                itemDetailAdds.value = null
                return@addSnapshotListener
            }

            var receivedAdds  = value!!.toObject(Adds::class.java)

            itemDetailAdds.value = receivedAdds
        }

        return itemDetailAdds
    }

    fun makeAdd(imgPath: String = "", Title: String ="",
            Description: String ="", Price: String="",
            Category: String ="", Location: String="",
                latitude: Double = 1000.0, longitude: Double = 1000.0,
            ExpireDate: String="") {

        //When an item is created, it has always status purchasable
        val newAdd = Adds("",imgPath, Title, Description, Price, Category,
            Location, latitude, longitude, ExpireDate,FirebaseAuth.getInstance().currentUser?.uid!!, emptyList(),ItemStatus.PURCHASABLE,null)

        Repository
            .makeAdd(newAdd)
            .addOnSuccessListener {
                Log.d("Lab3Debug", "Transaction success!")
            }
            .addOnFailureListener { e ->
                Log.e("Lab3Debug", "Transaction failure.", e)
                throw Exception()
            }
    }

    fun editAdd(requestQueue: RequestQueue, addToBeStored:Adds,ownerNickname: String) {

        Repository
            .editAdd(addToBeStored)
            .addOnSuccessListener {
                Log.d("Lab3Debug", "Adds edited successfully")
            }
            .addOnFailureListener { e ->
                Log.e("Lab3Debug", "Adds editing failure", e)
                throw Exception()
            }

        if(addToBeStored.status == ItemStatus.SOLD || addToBeStored.status == ItemStatus.BLOCKED){
            //Log.e("Lab3Debug8", "ItemStatus = SOLD or BLOCKED")
            //Log.e("Lab3Debug8", "list of interestedUser size : ${addToBeStored.interestedUsers.size}")

            lateinit var notifyReason:NotificationReason
            if(addToBeStored.status == ItemStatus.SOLD) {

                notifyReason = NotificationReason.ITEM_BOUGHT
                sendSmartPhoneNotification(requestQueue,ownerNickname, addToBeStored.title, addToBeStored.buyerId!!, notifyReason)

                notifyReason = NotificationReason.ITEM_SOLD
            } else //case item blocked
                notifyReason = NotificationReason.ITEM_BLOCKED

            for(interestedUser in addToBeStored.interestedUsers){

                Log.e("Lab4Debug", "interestedUser : ${interestedUser}")

                if(addToBeStored.status == ItemStatus.SOLD && interestedUser == addToBeStored.buyerId) {
                    Log.e("Lab4Debug", "interestedUser : ${interestedUser} - continue")
                    continue
                }


                sendSmartPhoneNotification(requestQueue,ownerNickname, addToBeStored.title, interestedUser, notifyReason)
            }
        }


    }

    fun writeInterestOnDb(user:String, adds: Adds) {
        repo.writeInterestOnDb(user,adds).addOnFailureListener {
            Log.e("Lab3Debug","Failed to save Address!", it)
        }
    }

    fun removeInterestFromDb(user:String, adds: Adds) {
        repo.removeInterestFromDb(user,adds).addOnFailureListener {
            Log.e("Lab3Debug","Failed to save Address!", it)
        }
    }

    fun sendSmartPhoneNotification(requestQueue: RequestQueue, userNickname: String,
                                   itemTitle:String, receiver_UserId:String, notifyReason: NotificationReason) {

        val notification = JSONObject()
        val notifcationBody = JSONObject()

        try {
            if(notifyReason == NotificationReason.ITEM_SOLD){ //case in which I have to notify that I have sold one item
                notifcationBody.put("title", "Item Sold")
                notifcationBody.put("body", "User ${userNickname} " +
                        "has sold his item '${itemTitle}'")   //Enter your notification message
            }
            else if(notifyReason == NotificationReason.INTERESTED_IN_ITEM){ //case in which I notify interest in one item
                notifcationBody.put("title", "Interesting Item ")
                notifcationBody.put("body", "User ${userNickname} " +
                            "is interested in your item '${itemTitle}'")   //Enter your notification message
            }
            else if (notifyReason == NotificationReason.ITEM_BLOCKED){ //case in which I notify that I have blocked one item
                notifcationBody.put("title", "Item Blocked")
                notifcationBody.put("body", "User ${userNickname} " +
                        "has blocked his item '${itemTitle}'")   //Enter your notification message
            }
            if(notifyReason == NotificationReason.ITEM_BOUGHT){ //case in which I have to notify that I have bought one item
                notifcationBody.put("title", "Item Bought")
                notifcationBody.put("body", "You have bougth item '${itemTitle}' " +
                        "from ${userNickname}")   //Enter your notification message
            }
            notification.put("to", "/topics/${receiver_UserId}")
            //notification.put("notification", notifcationBody)
            notification.put("data", notifcationBody)
            Log.e("Lab3Debug", "Notification : $notification")
        } catch (e: JSONException) {
            Log.e("Lab3Debug", "onCreate: " + e.message)
        }

        Log.e("Lab3Debug8", "sendNotification")
        //it send an http request to "FCM_API" that contains in the body a json object "notification"
        //and in the header contains the server key to connect to our firestore server
        //and also the content-type that tell that our body contains a JSON body.
        //It is used the volley library!
        val jsonObjectRequest = object : JsonObjectRequest(FCM_API, notification,
            Response.Listener<JSONObject> { response ->
                Log.i("Lab3Debug", "onResponse: $response")
                //msg.setText("")
            },
            Response.ErrorListener {
                //Toast.makeText(context, "Request error", Toast.LENGTH_LONG).show()
                Log.i("Lab3Debug", "onErrorResponse: Didn't work")
                throw Exception()
            }) {

            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Authorization"] = serverKey
                params["Content-Type"] = contentType
                return params
            }
        }
        requestQueue.add(jsonObjectRequest)
    }


    fun saveRateForCurrentItem(userRating:Float, comment:String){
        Repository
            .saveRate(currentItem_ownerId,currentItem_itemId,userRating,comment)
            .addOnSuccessListener {
                Log.d("Lab3Debug", "Rate added successfully")
            }
            .addOnFailureListener { e ->
                Log.e("Lab3Debug", "Rate adding failure", e)
                throw Exception()
            }
    }

    // get realtime updates from firebase regarding the user mean rating
    fun getUserMeanRating(userId:String): LiveData<Float> {
        repo.getAnyUserAddsList(userId).addSnapshotListener { value, e ->
            if (e != null) {
                Log.w("Lab4Debug", "Listen failed.", e)
                currentUserRating.value = null
                return@addSnapshotListener
            }

            var ratingSum:Float = 0.0F
            var ratingCount:Int = 0

            Log.e("Lab4Debug","List of item received")
            for (doc in value!!) {
                //Log.e("Lab3Debug","item received : $doc")
                var item = doc.toObject(Adds::class.java)
                if(item.rating != null){ //rating is null when the item is not sold!
                    Log.e("Lab4Debug3","Item ${item.title} -> Rating ${item.rating}")
                    ratingSum += item.rating!!
                    ratingCount++
                }
            }

            if(ratingCount == 0){ //case in which a user has only item that are not sold
                currentUserRating.value = 0F
            } else{ //standard case in which the user has already sold some item
                currentUserRating.value = ratingSum/ratingCount
            }

        }

        return currentUserRating
    }
}

enum class NotificationReason{
    ITEM_BOUGHT,
    ITEM_SOLD,
    ITEM_BLOCKED,
    INTERESTED_IN_ITEM
}