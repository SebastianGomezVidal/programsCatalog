package it.polito.mad.lab2.classes

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import it.polito.mad.lab2.activities.MainActivity

object AddsArray {

    const val ARG_ITEM_INDEX = "itemIndex"
    const val ARG_ITEM_OWNER = "itemOwner"

    //var adds = mutableListOf<Adds>()


    //it returns the index of the added element!
    /*fun makeAdd(imgPath: String = "", Title: String ="",
                Description: String ="", Price: String="",
                Category: String ="", Location: String="",
                ExpireDate: String="", context: Context) : Int {

        val newAdd = Adds( imgPath, Title, Description, Price, Category, Location, ExpireDate)

        //Storing the captured variables back from EditProfileActivity
        val sharedPref =
            context.getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE) ?: return -1

        // it is put after getSharedPreferneces because otherwise,
        // the method could return -1 but the element is added!
        adds.add(newAdd)

        persistAddsArray(sharedPref)

        return (adds.size - 1)
    }

    fun editAdd(itemIndex: Int, adds: Adds, context: Context) {
        AddsArray.adds[itemIndex].imgPath = adds.imgPath
        AddsArray.adds[itemIndex].title = adds.title
        AddsArray.adds[itemIndex].description = adds.description
        AddsArray.adds[itemIndex].price = adds.price
        AddsArray.adds[itemIndex].category = adds.category
        AddsArray.adds[itemIndex].location = adds.location
        AddsArray.adds[itemIndex].expireDate = adds.expireDate

        //Storing the captured variables back from EditProfileActivity
        val sharedPref =
            context.getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE) ?: return

        persistAddsArray(sharedPref)

    }*/

    /*fun persistAddsArray(sharedPref: SharedPreferences){
        //Parsing the data-object as Json
        val gSon = Gson()
        val jsonObject: String = gSon.toJson(adds)

        with(sharedPref.edit()) {
            //Persisting the data
            putBoolean("notEmptyItems", true)
            //Control key-value pair to know if there is prior persistance
            putString("addsArray",jsonObject)
            //Storing the Json object
            commit()
        }
    }*/
}