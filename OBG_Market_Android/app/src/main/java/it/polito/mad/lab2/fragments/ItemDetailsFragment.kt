package it.polito.mad.lab2.fragments

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RatingBar
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.auth.FirebaseAuth
import it.polito.mad.lab2.R
import it.polito.mad.lab2.activities.MapsActivity
import it.polito.mad.lab2.classes.*
import it.polito.mad.lab2.viewmodels.ItemsViewModel
import it.polito.mad.lab2.viewmodels.NotificationReason
import it.polito.mad.lab2.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.fragment_item_details.*
import org.json.JSONException
import org.json.JSONObject

class ItemDetailsFragment : Fragment() {


    private val itemViewModel: ItemsViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()

    //function that check if the item is of another user or is of this user
    //return true if itemIsOfAnotherUser
    private fun itemIsOfAnotherUser() : Boolean{
        //itemViewModel.currentItemId contains the id of the owner of the item currently displayed in itemDetails
        val ret =  FirebaseAuth.getInstance().currentUser?.uid != itemViewModel.currentItem_ownerId
        //val ret =  true
        Log.e("Lab3Debug","Item is of the another user ? $ret")
        return ret
    }

    //return true if you have bought the item!
    private fun itemBoughtByYou(adds:Adds): Boolean{
        val ret = FirebaseAuth.getInstance().currentUser?.uid == adds.buyerId
        Log.e("Lab4Debug2","Have you bought the item ?  $ret")
        return ret
    }

    override fun onResume() {
        super.onResume()
        val button = view?.findViewById<ImageButton>(R.id.googleButtonShowItem)
        button?.setOnClickListener(){
            val intent = Intent(activity, MapsActivity::class.java)
            intent.putExtra("action", 2)
            intent.putExtra("latitude",  itemViewModel.itemDetailAdds.value!!.latitude)
            intent.putExtra("longitude", itemViewModel.itemDetailAdds.value!!.longitude)
            startActivity(intent)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_item_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            if(it.containsKey(AddsArray.ARG_ITEM_INDEX) && it.containsKey(AddsArray.ARG_ITEM_OWNER)) {
                itemViewModel.currentItem_itemId = it.getString(AddsArray.ARG_ITEM_INDEX, "")
                itemViewModel.currentItem_ownerId = it.getString(AddsArray.ARG_ITEM_OWNER, "")
                Log.e("Lab3Debug9", "Here in arguments $arguments")
            }
        }

        if(itemIsOfAnotherUser()){
            toolbarItemDetails.menu.findItem(R.id.edit_item).setVisible(false)
            fab_interested_empty.setOnClickListener {
                notifyInterest()
            }
            fab_interested_full.setOnClickListener {
                removeInterest()
            }
            //fab_interested.visibility = View.VISIBLE
            tvInterestedUsers.visibility = View.GONE

        }
        else {
            toolbarItemDetails.menu.findItem(R.id.edit_item).setVisible(true )
            toolbarItemDetails.setOnMenuItemClickListener { menuItem ->
                Log.d("Lab2Debug", "optionSelected")
                when (menuItem.itemId) {
                    R.id.edit_item -> {
                        editItem()
                        true
                    }
                    else -> false
                }
            }
            fab_interested_empty.visibility = View.GONE
            fab_interested_full.visibility = View.GONE
            tvInterestedUsers.visibility = View.VISIBLE

        }

        //code needed to change toolbar and to make the back button correctly working
        val navHostFragment = NavHostFragment.findNavController(this);
        //NavigationUI.setupWithNavController(toolbarItemDetails, navHostFragment)
        toolbarItemDetails.setNavigationOnClickListener {
            findNavController().navigateUp()
        }


        if(itemViewModel.currentItem_itemId != "" && itemViewModel.currentItem_ownerId != "") { //the other cases should never happens
            Log.e("Lab3Debug9","Here itemId and ownerId != empty string")
            itemViewModel.getItemGivenUserAndId(itemViewModel.currentItem_ownerId,itemViewModel.currentItem_itemId).observe(viewLifecycleOwner,
                Observer { currentItem: Adds ->
                    //itemDetailsImageView.setImageURI(Uri.parse(currentItem.imgPath))
                    loadImageFromRemote(currentItem.imgPath,requireContext(),itemDetailsImageView)
                    tvTitle.text = currentItem.title
                    tvDescription.text = currentItem.description
                    tvPrice.text = currentItem.price
                    tvCategory.text = currentItem.category
                    tvLocation.text = currentItem.location
                    tvExpiryDate.text = currentItem.expireDate
                    tvStatus.text = currentItem.status.toString()
                    itemOwner.text = getString(R.string.press_here_for_owner_info)

                    btnRateUser.setOnClickListener(null)
                    btnRateUser.visibility = View.GONE

                    if (itemIsOfAnotherUser()){ //update the fab according to if the user is already interested or not in the item
                        if(itemBoughtByYou(currentItem)){ //if I had bought the item, I should not show the fav button and add a new button for rating the user

                            btnRateUser.visibility = View.VISIBLE
                            //hide both the button of interest because in this case, the item has been already bought
                            fab_interested_full.visibility = View.GONE
                            fab_interested_empty.visibility = View.GONE

                            if(currentItem.rating != null){ //case in which I have already rated the item
                                btnRateUser.text = getString(R.string.seller_rated)
                                btnRateUser.setBackgroundColor(Color.GRAY)
                                btnRateUser.isClickable = false
                            }
                            else {
                                btnRateUser.text = getString(R.string.rate_the_seller)
                                btnRateUser.isClickable = true
                                btnRateUser.setOnClickListener {
                                    rateItem()
                                }
                            }
                        }
                        else{ //case in which the item is of another user and I have not bought it -> show fav button and hide rating button
                            if(currentItem.interestedUsers.contains(FirebaseAuth.getInstance().currentUser?.uid)){ //the users is already interested in the item
                                //fab_interested.setImageDrawable(getResources().getDrawable(R.drawable.ic_interested_full))
                                fab_interested_full.visibility = View.VISIBLE
                                fab_interested_empty.visibility = View.GONE
                            }
                            else{
                                //fab_interested.setImageDrawable(getResources().getDrawable(R.drawable.ic_interested_empty))
                                fab_interested_full.visibility = View.GONE
                                fab_interested_empty.visibility = View.VISIBLE
                            }

                        }
                    }
                    else{ //case in which it is your item -> show the list of interested user!
                        var interestedUsersString:String = "\nInterested Users : \n"
                        var cntInterested = 0
                        for(userId in currentItem.interestedUsers){

                            val userRetrieved = userViewModel.map_UserId_User.value?.get(userId)

                            if(userRetrieved != null && userRetrieved.nickName != "") { //if the userId is in the list of user(it should always be in this way!)
                                interestedUsersString += "\t\t\t\t" + Html.fromHtml("&#8226;")
                                    .toString() + "${userRetrieved.nickName}\n"

                                cntInterested++
                            }
                        }

                        if (cntInterested != 0)
                            tvInterestedUsers.text = interestedUsersString
                        else
                            tvInterestedUsers.text = ""
                    }
                }
            )
        }

        if(itemIsOfAnotherUser()){
            itemOwner.setOnClickListener {
                showProfileOfOtherUser()
                itemOwner.visibility = View.VISIBLE
            }
        }else{
            itemOwner.visibility = View.GONE
        }

    }

    private fun notifyInterest() {
        //I have to notify to the owner of the object that I am interested in buying this item!

        Log.e("Lab3Debug","Notify Interest clicked!")
        itemViewModel.writeInterestOnDb(FirebaseAuth.getInstance().currentUser!!.uid,itemViewModel.itemDetailAdds.value!!)

        val requestQueue: RequestQueue = SingletonRequestQueue.getInstance(requireContext()).requestQueue

        if(userViewModel.getCurrentUser().value != null) {  //case in which the user contains all the fields because
                                                            //when I try to set the different field of a user,
                                                            //all the fields must be set before saving in editProfile!
            itemViewModel.sendSmartPhoneNotification(requestQueue, userViewModel.getCurrentUser().value!!.nickName,
                itemViewModel.itemDetailAdds.value!!.title, itemViewModel.itemDetailAdds.value!!.ownerId,NotificationReason.INTERESTED_IN_ITEM)
        }
        else{ //case in which a new user has not set its fields like nickname, email...
            itemViewModel.sendSmartPhoneNotification(
                requestQueue,
                "Anonymous",
                itemViewModel.itemDetailAdds.value!!.title,
                itemViewModel.itemDetailAdds.value!!.ownerId,
                NotificationReason.INTERESTED_IN_ITEM
            )
        }

        fab_interested_empty.visibility = View.GONE
        fab_interested_full.visibility = View.VISIBLE
    }

    private fun removeInterest() {
        //I have to notify to the owner of the object that I am interested in buying this item!

        Log.e("Lab3Debug","Remove Interest clicked!")
        itemViewModel.removeInterestFromDb(FirebaseAuth.getInstance().currentUser!!.uid,itemViewModel.itemDetailAdds.value!!)

        fab_interested_empty.visibility = View.VISIBLE
        fab_interested_full.visibility = View.GONE
    }

    private fun editItem() {

        //val b = bundleOf(AddsArray.ARG_ITEM_INDEX to itemId)

        Utils.arrivesToEditItem = ArrivesToEditItem.FROM_ITEM_DETAILS

        //findNavController().navigate(R.id.itemEditFragment,b)
        findNavController().navigate(R.id.itemEditFragment)
        //Toast.makeText(context, "Edit Item Clicked", Toast.LENGTH_SHORT).show()*/
    }

    //New function to be called when the current user wants to see the profile of another user
    //This function should be executed when the name of the user is pressed in the add
    private fun showProfileOfOtherUser(){
        var otherUser :Boolean= itemIsOfAnotherUser()
        val currentItem_ownerId = bundleOf("currentItem_ownerId" to itemViewModel.currentItem_ownerId,
            "otherUser" to otherUser)

        this.findNavController().navigate(R.id.action_itemDetailsFragment_to_nav_profile,currentItem_ownerId)
    }

    private fun rateItem(){
        // Get the layout inflater
        val inflater = requireActivity().layoutInflater;

        MaterialAlertDialogBuilder(context)
            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            .setView(inflater.inflate(R.layout.rate_user_dialog, null))
            .setPositiveButton("Save"){
                    dialog: DialogInterface, which: Int ->

                val userRating = (dialog as Dialog).findViewById<RatingBar>(R.id.ratingBar_user).rating
                val comment = (dialog as Dialog).findViewById<TextInputEditText>(R.id.tIET_Comment)?.text.toString()

                Log.e("Lab4Debug1","Rating : $userRating")
                Log.e("Lab4Debug1","Comment : $comment")

                itemViewModel.saveRateForCurrentItem(userRating,comment)

                dialog.dismiss()

                //super.onBackPressed()
            }
            .setNegativeButton("Cancel"){
                    dialog: DialogInterface, which: Int ->
                //Toast.makeText(context,"CANCELED",Toast.LENGTH_SHORT)
                dialog.cancel()
            }
            .create().show()

    }


}
