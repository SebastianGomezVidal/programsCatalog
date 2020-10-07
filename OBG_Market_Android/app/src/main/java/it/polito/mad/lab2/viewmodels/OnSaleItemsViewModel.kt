package it.polito.mad.lab2.viewmodels

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import it.polito.mad.lab2.classes.Adds
import it.polito.mad.lab2.classes.ItemStatus
import it.polito.mad.lab2.classes.UserInfo
import it.polito.mad.lab2.repositories.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class OnSaleItemsViewModel: ViewModel() , Observable{
    private val repo = Repository
    private val usersCollectionReference = repo.getUsersList()


    //private val onSaleAddsListFiltered = MutableLiveData<List<Adds>>()
    lateinit var menuStored: Menu


    //done in this way to attach only once the addSnapshot listener and to allow retrieving multiple time
    //the list without reattaching the listeners
    private val onSaleAddsList by lazy{
        val ret = MutableLiveData<List<Adds>>()

        usersCollectionReference.addSnapshotListener{ listOfUsers,e1 ->

            if(e1 != null){
                Log.w("Lab3Debug3","ViewModel: Listen failed", e1)
                return@addSnapshotListener
            }

            if(listOfUsers != null){

                var allItemsList = mutableListOf<Adds>()

                Log.d("Lab3Debug4","Size of ListOfUser ---> ${listOfUsers.size()}")

                for(user in listOfUsers!!) {
                    //usersIds.add(user.id)
                    //Log.d("Lab3Debug4","User id ---> ${user.id}")

                    //I will retrieve only the items of other user!!
                    if (user.id != FirebaseAuth.getInstance().currentUser?.uid) {
                        //retrieve item of a certain user
                        repo.getAnyUserAddsList(user.id).addSnapshotListener { listOfItems, e ->
                            if (e != null) {
                                Log.w("Lab3Debug3", "ViewModel: Listen failed", e)
                                return@addSnapshotListener
                            }

                            if (listOfItems != null) {
                                Log.e(
                                    "Lab3Debug4",
                                    "List of items of user ${user.id} size : ${listOfItems.size()}"
                                )
                                Log.e(
                                    "Lab3Debug4",
                                    "AllItemsList size before adding cycle: ${allItemsList.size}"
                                )

                                //Beware: each time the items of a certain  user are updated, I should first remove all of them
                                //and then re-add the new element inside the list
                                allItemsList.removeIf { adds: Adds -> adds.ownerId == user.id }

                                for (item in listOfItems!!) {
                                    Log.e(
                                        "Lab3Debug4", "items of user ${user.id} title :  " +
                                                "${item.toObject(Adds::class.java).title}"
                                    )
                                    allItemsList.add(item.toObject(Adds::class.java))
                                }

                                Log.e(
                                    "Lab3Debug4",
                                    "AllItemsList size after adding cycle: ${allItemsList.size}"
                                )

                                /**
                                 * Beware I will not filter item there because I want to maintain all of them otherwise
                                 * I cannot filter and obtain my data correctly the second time(example scenario: I open the
                                 * app and then I retrieve all the list(Item A, B and C). I put filters here and one item on the db changed
                                 * in this way this listener is called. The list obtained from the server is then filtered here
                                 * and only the filtered item are maintained locally(i.e. item B and C). If at this time I change the filter
                                 * I cannot retrieve the element A since this listener is called only when element on db changes and element A
                                 * is not available locally since I have filtered it!)
                                 */
                                //allItemsList = this.categoryItemSearch(categorySearch.listOfItems!!,allItemsList)
                                //allItemsList = this.priceItemSearch(minPriceSearch.listOfItems!!,maxPriceSearch.listOfItems!!,allItemsList)
                                //We update the live data once we have filtered the list.
                                ret.value = allItemsList
                                //Log.d("Lab3Debug4","${onSaleAddsList.value}")
                            }
                        }
                    }
                }
            }
        }

        return@lazy ret

    }

    //Alejandro: This function returns a list of the adds of all the users
    fun getOnSaleItems(): LiveData<List<Adds>> {

        //var usersIds = mutableListOf<String>()

        /*usersCollectionReference
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->

            }
            .continueWith { inputTask ->
                val listOfUser = inputTask.getResult()
                for (user in listOfUser!!){

                }
            }*/

        return onSaleAddsList

    }

    // The next live data is used to search and clasify items
    @Bindable
    var titleSearch = MutableLiveData<String>()

    @Bindable
    var categorySearch = MutableLiveData<String>()

    @Bindable
    var minPriceSearch = MutableLiveData<String>()

    @Bindable
    var maxPriceSearch = MutableLiveData<String>()



    private val callbacks: PropertyChangeRegistry by lazy { PropertyChangeRegistry() }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        callbacks.add(callback)
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        callbacks.remove(callback)
    }

    @Synchronized fun filterList(itemStatus:ItemStatus, title:String, minPrice:String,
                                 maxPrice:String, category:String) : List<Adds> {



        var toBeFiltered = getOnSaleItems().value!!

        //filter that should not be useful but we need it for the first time we get open the app because in that
        //case, getOnSaleItems() doesn't filter the item of a user that is not logged yet
        toBeFiltered = toBeFiltered.filter { adds -> adds.ownerId != FirebaseAuth.getInstance().currentUser?.uid }

        //itemStatus filter
        toBeFiltered = toBeFiltered.filter { adds -> adds.status == itemStatus }

        //title filter
        toBeFiltered = toBeFiltered.filter { adds -> adds.title.toLowerCase().contains(title.toLowerCase()) }

        //category filter
        if(category != "Category") {    //category parameter is equal to "Category" in the default case and using
            //it means not filtering by category
            toBeFiltered = toBeFiltered.filter { adds -> adds.category.contains(category) }
        }

        //price filter
        var minPriceDouble:Double = 0.0
        var maxPriceDouble:Double = 0.0

        if(minPrice.isNotEmpty() && maxPrice.isNotEmpty()) { // if both are full I have to check that min <= max
            if(minPrice.toDouble() > maxPrice.toDouble()) //in case min>max I will not filter by price
                return toBeFiltered
        }

        if(minPrice.isNotEmpty()) {
            minPriceDouble = minPrice.toDouble()
            Log.e("Lab3Debug5","MinPriceDouble : $minPriceDouble")
            toBeFiltered = toBeFiltered
                .filter { adds ->
                    val addsPriceDouble = addsPriceToDouble(adds.price)

                    //the last instruction is considered as the returned value
                    //price higher than min
                    minPriceDouble < addsPriceDouble
                }
        }

        if(maxPrice.isNotEmpty()) {
            maxPriceDouble = maxPrice.toDouble()
            Log.e("Lab3Debug5","MaxPriceDouble : $maxPriceDouble")
            toBeFiltered = toBeFiltered
                .filter { adds ->
                    val addsPriceDouble = addsPriceToDouble(adds.price)

                    //the last instruction is considered as the returned value
                    //price smaller than max
                    addsPriceDouble < maxPriceDouble
                }
        }

        //expired item filter
        toBeFiltered = toBeFiltered.filter { adds ->
            var parsedDate = LocalDate.parse(adds.expireDate, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            Log.e("Lab4Debug7","Parsed date : $parsedDate")
            Log.e("Lab4Debug7","Now : ${LocalDate.now()}")

            parsedDate >= LocalDate.now()

        }

        return toBeFiltered

    }

    private fun addsPriceToDouble(price:String) : Double{

        val addsPriceNoDollar = price.removePrefix("$")
        //Log.e("Lab3Debug5","addsPriceNoDollar : $addsPriceNoDollar")

        val addsPriceDouble = addsPriceNoDollar.toDouble()
        //Log.e("Lab3Debug5","addsPriceDouble : $addsPriceDouble")

        return addsPriceDouble

    }

    //LAB4 Alejandro
    fun getBoughtItemList(): List<Adds>{
        var allItemsList = getOnSaleItems().value!!
        var boughtItemList:List<Adds> = listOf()

        boughtItemList = allItemsList.filter { adds ->
            adds.buyerId != null && adds.buyerId!!.contains(FirebaseAuth.getInstance().currentUser?.uid.toString())
        }

        return boughtItemList
    }

    fun getItemsOfInterestList():List<Adds>{
        val allItemsList = getOnSaleItems().value!!
        var itemsOfInterestList:List<Adds> = listOf()

        itemsOfInterestList = allItemsList.filter { adds ->
            adds.interestedUsers.contains(FirebaseAuth.getInstance().currentUser?.uid.toString())
        }

        return itemsOfInterestList
    }


}