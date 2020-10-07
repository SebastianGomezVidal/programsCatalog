package it.polito.mad.lab2.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout.VERTICAL
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.auth.User
import com.google.firebase.ktx.Firebase
import it.polito.mad.lab2.R
import it.polito.mad.lab2.classes.Adds
//import it.polito.mad.lab2.classes.AddsArray.adds
import it.polito.mad.lab2.classes.CustomAdapter
import it.polito.mad.lab2.classes.FragmentName
import it.polito.mad.lab2.viewmodels.ItemsViewModel

/**
 * A simple [Fragment] subclass.
 */
class ItemListFragment : Fragment() {

    private val itemViewModel: ItemsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_item_list, container, false)
    }

    /***********************************
     ********** On Created View ********
     ***********************************/

    @SuppressLint("WrongConstant")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)
        val fab: FloatingActionButton = view.findViewById(R.id.floating_action_button)

        //getting recyclerview from xml
        val recyclerView = getView()?.findViewById(R.id.recycler_view) as RecyclerView
        val emptyView: TextView? = getView()?.findViewById(R.id.empty_view)
        //creating adapter
        val adapter = CustomAdapter(emptyList(), FragmentName.MY_ITEM_LIST)

        //adding a layout manager
        recyclerView.layoutManager = LinearLayoutManager(context, VERTICAL, false)

        //now adding the adapter to recyclerview
        recyclerView.adapter = adapter

        FirebaseAuth.getInstance().addAuthStateListener {
            if(it.currentUser != null) { //the user is logged in
                itemViewModel.getCurrentUserItemsList().observe(viewLifecycleOwner,
                    Observer { addsLists:List<Adds> ->
                        if (addsLists.isEmpty()){
                            recyclerView.visibility = View.GONE;
                            emptyView?.visibility = View.VISIBLE;
                        }
                        else {
                            recyclerView.visibility = View.VISIBLE;
                            emptyView?.visibility = View.GONE;

                            adapter.updateList(addsLists)
                        }
                    })
            }
        }

        fab.setOnClickListener (View.OnClickListener (){
            itemViewModel.currentItem_itemId = ""
            findNavController().navigate(R.id.action_nav_adds_to_itemEditFragment)
        })
    }

}

