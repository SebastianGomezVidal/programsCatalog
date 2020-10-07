package it.polito.mad.lab2.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import it.polito.mad.lab2.R
import it.polito.mad.lab2.classes.Adds
import it.polito.mad.lab2.classes.CustomAdapter
import it.polito.mad.lab2.classes.FragmentName
import it.polito.mad.lab2.viewmodels.ItemsViewModel
import it.polito.mad.lab2.viewmodels.OnSaleItemsViewModel


class BoughtItemsListFragment : Fragment() {

    private val onSaleItemsViewModel: OnSaleItemsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bought_items_list, container, false)
    }

    /***********************************
     ********** On Created View ********
     ***********************************/

    @SuppressLint("WrongConstant")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //getting recyclerview from xml
        val recyclerView = getView()?.findViewById(R.id.recycler_view) as RecyclerView
        val emptyView: TextView? = getView()?.findViewById(R.id.empty_view)
        //creating adapter
        val adapter = CustomAdapter(emptyList(), FragmentName.BOUGHT_ITEMS_LIST)

        //adding a layout manager
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayout.VERTICAL, false)

        //now adding the adapter to recyclerview
        recyclerView.adapter = adapter

        FirebaseAuth.getInstance().addAuthStateListener {
            if (it.currentUser != null) { //the user is logged in
                val boughtItemsList: List<Adds> = onSaleItemsViewModel.getBoughtItemList()
                if(boughtItemsList.isEmpty()){
                    recyclerView.visibility = View.GONE;
                    emptyView?.visibility = View.VISIBLE;

                }else{
                    recyclerView.visibility = View.VISIBLE;
                    emptyView?.visibility = View.GONE;

                    adapter.updateList(boughtItemsList)

                }
            }
        }
    }
}