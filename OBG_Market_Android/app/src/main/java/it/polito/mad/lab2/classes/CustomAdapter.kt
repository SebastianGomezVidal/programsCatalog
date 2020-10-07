package it.polito.mad.lab2.classes

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import it.polito.mad.lab2.R
import it.polito.mad.lab2.activities.MainActivity
import it.polito.mad.lab2.activities.MapsActivity

//import it.polito.mad.lab2.classes.AddsArray.adds


//fragment is the fragment to which the adapter is associated.
//It can be ItemListFragment or OnSaleListFragment

enum class FragmentName{
        MY_ITEM_LIST,
        OTHERS_ON_SALE_LIST,
        ITEMS_OF_INTEREST_LIST,
        BOUGHT_ITEMS_LIST
    }

class CustomAdapter(var data:List<Adds>, private val fragment:FragmentName) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.card_views, parent, false)
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //holder.bindItems(adds[position]!!, position)
        holder.bindItems(data[position]!!, position)
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        //return adds.size
        return data.size
    }

    override fun onViewRecycled(holder: ViewHolder) {
        holder.unbind()
    }

    fun updateList(newData: List<Adds>) {
        val diffs = DiffUtil.calculateDiff(
            ItemDiffCallback(data,newData)
        )
        data = newData
        diffs.dispatchUpdatesTo(this)
        //notifyDataSetChanged()
    }

    //the class is holding the list view
    //inner is needed for accessing attribute of CustomAdapter inside ViewHolder
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val viewImg = itemView.findViewById(R.id.cardImage) as ImageView
        val viewCard = itemView.findViewById(R.id.cardView) as CardView
        val textViewTitle = itemView.findViewById(R.id.cardTitle) as TextView
        val textViewCategory = itemView.findViewById(R.id.cardCategory) as TextView
        val textViewLocation = itemView.findViewById(R.id.cardLocation) as TextView
        val textViewExpiration = itemView.findViewById(R.id.cardDate) as TextView
        val textViewPrice = itemView.findViewById(R.id.cardPrice) as TextView
        val btnCardEdit = itemView.findViewById(R.id.cardEdit) as ImageButton
        val btnCardthrash = itemView.findViewById(R.id.cardThrash) as ImageButton
        val btnCardPath = itemView.findViewById(R.id.cardPath) as ImageButton

        fun bindItems(appData: Adds, indexToSend: Int) {

            //viewImg.setImageURI(Uri.parse(appData.imgPath))

            // Reference to an image file in Cloud Storage
            /*val storageReference = Firebase.storage.reference
            var file = Uri.parse(appData.imgPath)
            val imageRef = storageReference.child("images/${file.lastPathSegment}")

            // Download directly from StorageReference using Glide
            // (See MyAppGlideModule for Loader registration)
            Glide.with(itemView.context)
                .load(imageRef)
                .into(viewImg)*/

            loadImageFromRemote(appData.imgPath,itemView.context,viewImg)

            textViewTitle.text = appData.title
            textViewCategory.text = appData.category
            textViewLocation.text = appData.location
            textViewExpiration.text = appData.expireDate
            textViewPrice.text = appData.price

            if(fragment == FragmentName.MY_ITEM_LIST) { //case my adds I leave trash and edit button in the adds

                btnCardPath.visibility = View.GONE
                btnCardEdit.visibility = View.VISIBLE
                //btnCardthrash.visibility = View.VISIBLE

                btnCardEdit.setOnClickListener {

                    Utils.arrivesToEditItem = ArrivesToEditItem.FROM_ITEM_LIST

                    val b = bundleOf(
                        AddsArray.ARG_ITEM_INDEX to appData.addsId,
                        AddsArray.ARG_ITEM_OWNER to appData.ownerId
                    )
                    it.findNavController().navigate(R.id.action_nav_adds_to_itemEditFragment, b)
                }

                /*
                 TODO rehandle the card thrash when all the rest works
                 btnCardthrash.setOnClickListener {

                     Snackbar.make(it, "You are about to delete this add", Snackbar.LENGTH_LONG)
                         .setAction("CONFIRM") {
                             adds.removeAt(adapterPosition)
                             val shared = it.context.getSharedPreferences(
                                 MainActivity.SHARED_PREFS,
                                 Context.MODE_PRIVATE
                             ) ?: return@setAction
                             AddsArray.persistAddsArray(shared)
                             it.findNavController().navigate(R.id.nav_adds)
                         }.show()
                 }*/
            }
            else{ //case fragment onSaleList; hide edit and trash button
                btnCardPath.visibility = View.VISIBLE
                btnCardEdit.visibility = View.GONE
                btnCardthrash.visibility = View.GONE

                btnCardPath.setOnClickListener {
                    val intent = Intent(itemView.context, MapsActivity::class.java)
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.putExtra("latitude",  appData.latitude)
                    intent.putExtra("longitude", appData.longitude)
                    intent.putExtra("action", 3)
                    itemView.context.applicationContext.startActivity(intent)
                }
            }


            //It doesn't work on itemView because it is under the other component
            viewCard.setOnClickListener {

                val b = bundleOf(
                    AddsArray.ARG_ITEM_INDEX to appData.addsId,
                    AddsArray.ARG_ITEM_OWNER to appData.ownerId
                )


                when(fragment){
                    FragmentName.MY_ITEM_LIST ->
                        it.findNavController().navigate(R.id.action_nav_adds_to_itemDetailsFragment, b)
                    FragmentName.OTHERS_ON_SALE_LIST ->
                        it.findNavController().navigate(R.id.action_onSaleListFragment_to_itemDetailsFragment, b)
                    FragmentName.ITEMS_OF_INTEREST_LIST ->
                        it.findNavController().navigate(R.id.action_nav_itemOfInterestListFragment_to_itemDetailsFragment,b)
                    FragmentName.BOUGHT_ITEMS_LIST ->
                        it.findNavController().navigate(R.id.action_boughtItemsListFragment_to_itemDetailsFragment,b)
                }
            }

        }

            fun unbind() {
                btnCardEdit.setOnClickListener(null)
                viewCard.setOnClickListener(null)
                btnCardthrash.setOnClickListener(null)
            }
    }
}
