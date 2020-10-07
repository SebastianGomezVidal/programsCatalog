package it.polito.mad.lab2.fragments

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.*
import android.widget.LinearLayout.VERTICAL
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import it.polito.mad.lab2.R
import it.polito.mad.lab2.classes.*
import it.polito.mad.lab2.databinding.FragmentOnSaleListBinding
import it.polito.mad.lab2.viewmodels.OnSaleItemsViewModel
import kotlinx.android.synthetic.main.fragment_on_sale_list.*

/**
 * A simple [Fragment] subclass.
 */
class OnSaleListFragment : Fragment() {

    private val itemOnSaleViewModel: OnSaleItemsViewModel by activityViewModels()
    private lateinit var adapter:CustomAdapter

    //needed for accessing text of the SearchView

    private lateinit var binding: FragmentOnSaleListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_on_sale_list,
            container,
            false
        )

        // Set the viewmodel for databinding - this allows the bound layout access
        // to all the data in the VieWModel
        binding.onSale = itemOnSaleViewModel

        // Specify the fragment view as the lifecycle owner of the binding.
        // This is used so that the binding can observe LiveData updates
        binding.lifecycleOwner = viewLifecycleOwner

        /*//Observables of the search variables
        itemOnSaleViewModel.titleSearch.observe(viewLifecycleOwner, Observer {
            Toast.makeText(this.context,"Está pasando",Toast.LENGTH_LONG).show()
        })*/

        setHasOptionsMenu(true)

        // Inflate the layout for this fragment
        return binding.root
    }

    /***********************************
     ********** On Created View ********
     ***********************************/

    @SuppressLint("WrongConstant")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)
        //val fab: FloatingActionButton = view.findViewById(R.id.floating_action_button)

        //getting recyclerview from xml
        val recyclerView = getView()?.findViewById(R.id.onSale_recycler_view) as RecyclerView
        val emptyView: TextView? = getView()?.findViewById(R.id.onSale_empty_view)
        //creating adapter
        adapter = CustomAdapter(emptyList(), FragmentName.OTHERS_ON_SALE_LIST)

        //adding a layout manager
        recyclerView.layoutManager = LinearLayoutManager(context, VERTICAL, false)

        //now adding the adapter to recyclerview
        recyclerView.adapter = adapter

        FirebaseAuth.getInstance().addAuthStateListener {
            if(it.currentUser != null) { //the user is logged in
                itemOnSaleViewModel.getOnSaleItems().observe(viewLifecycleOwner,
                    Observer { addsLists:List<Adds> ->

                        val titleFilter = (itemOnSaleViewModel.menuStored.getItem(0) as MenuItem).actionView as SearchView
                        Log.e("Lab3Debug6","size of addsList : ${addsLists.size}")

                        val filteredList = itemOnSaleViewModel.filterList(
                            ItemStatus.PURCHASABLE,
                            titleFilter.query.toString(),
                            editTextMinPrice.text.toString(),
                            editTextMaxPrice.text.toString(),
                            filterCategory.selectedItem.toString()
                        )

                        Log.e("Lab3Debug6","size of filteredList : ${filteredList.size}")

                        //I have to use the filtered list because the received adds list contains all the item so
                        //also the item sold and blocked. They are locally filtered!
                        if (filteredList.isEmpty()){
                            recyclerView.visibility = View.GONE;
                            emptyView?.visibility = View.VISIBLE;
                        }
                        else {
                            recyclerView.visibility = View.VISIBLE;
                            emptyView?.visibility = View.GONE;

                            //Log.i("Lab3Debug4","Size of list of items updated : ${addsLists.size}")

                            adapter.updateList(
                                filteredList
                            )
                        }
                    })
            }
        }

        search_bar.visibility = View.GONE

        val adapterCategory = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.categories_with_empty_item,
            android.R.layout.simple_spinner_item
        ).also { adapterCategory ->
            // Specify the layout to use when the list of choices appears
            adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            filterCategory.adapter = adapterCategory
        }


        val spinnerItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                return
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val cat = parent?.getItemAtPosition(position) as String

                Log.e("Lab3Debug5","Selected Category : $cat")
                if(itemOnSaleViewModel.getOnSaleItems().value != null){ //since the spinner is preselected with value
                                                                        //category at the creation of the fragment,
                                                                        // it is possible that the list of items is null
                                                                        // so for avoiding exception, in that case
                                                                        //filtering is not applied!

                    val titleFilter = (itemOnSaleViewModel.menuStored.getItem(0) as MenuItem).actionView as SearchView
                    Log.e("Lab4Debug6","Query title text : ${titleFilter.query.toString()}")
                    val filtList = itemOnSaleViewModel.filterList(ItemStatus.PURCHASABLE, titleFilter.query.toString(), editTextMinPrice.text.toString(),
                        editTextMaxPrice.text.toString(), cat)

                    if (filtList.isEmpty()){
                        recyclerView.visibility = View.GONE;
                        emptyView?.visibility = View.VISIBLE;
                    }
                    else {
                        recyclerView.visibility = View.VISIBLE;
                        emptyView?.visibility = View.GONE;

                        //Log.i("Lab3Debug4","Size of list of items updated : ${addsLists.size}")

                        adapter.updateList(
                            filtList
                        )
                    }

                    /*adapter.updateList(
                        itemOnSaleViewModel.filterList(ItemStatus.PURCHASABLE, titleFilter.text.toString(), editTextMinPrice.text.toString(),
                            editTextMaxPrice.text.toString(), cat)
                    )*/
                }
            }
        }

        filterCategory.onItemSelectedListener = spinnerItemSelectedListener

        /*fab.setOnClickListener (View.OnClickListener (){
            //itemOnSaleViewModel.currentItem_itemId = ""
            //findNavController().navigate(R.id.action_nav_adds_to_itemEditFragment)
        })*/


    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater){
        menuInflater.inflate(R.menu.menu_search_item, menu)

        // Define the listener
        val expandListener = object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                // Do something when action item collapses
                search_bar.visibility = View.GONE

                //Recycler View params
                val rvParams: CoordinatorLayout.LayoutParams = (onSale_recycler_view.getLayoutParams() as CoordinatorLayout.LayoutParams)
                rvParams.topMargin = 0
                onSale_recycler_view.setLayoutParams(rvParams)

                hideKeyboard(requireActivity())

                return true // Return true to collapse action view
            }

            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                Log.e("Lab4Debug6","Search menu expand")
                search_bar.visibility = View.VISIBLE
                return true // Return true to expand action view
            }
        }

        // Get the MenuItem for the action item
        val actionMenuItem = menu?.findItem(R.id.search_title)

        // Assign the listener to that action item
        actionMenuItem?.setOnActionExpandListener(expandListener)

        // Associate searchable configuration with the SearchView
        val searchManager = requireActivity().getSystemService(Context.SEARCH_SERVICE) as SearchManager
        (menu.findItem(R.id.search_title).actionView as SearchView).apply {
            setSearchableInfo(searchManager.getSearchableInfo(requireActivity().componentName))



            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    //TODO("Not yet implemented")
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    filterBasedOnText(newText ?: "")
                    return true
                }

            })
        }

        itemOnSaleViewModel.menuStored = menu


        //initialization done here otherwise the app crashes
        val twEditTextFilter = object: TextWatcher {
            override fun afterTextChanged(editable: Editable?) {

                val titleFilter = (itemOnSaleViewModel.menuStored.getItem(0) as MenuItem).actionView as SearchView
                filterBasedOnText(titleFilter.query.toString())

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
        }

        //titleFilter.addTextChangedListener(twEditTextFilter)
        editTextMinPrice.addTextChangedListener(twEditTextFilter)
        editTextMaxPrice.addTextChangedListener(twEditTextFilter)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.search_title -> {
            //I directly expand the search view when I click on the search button
            (item.actionView as SearchView).onActionViewExpanded()
            (item.actionView as SearchView).queryHint = " What are you looking for? "

            //Recycler View params
            val rvParams: CoordinatorLayout.LayoutParams = (onSale_recycler_view.getLayoutParams() as CoordinatorLayout.LayoutParams)
            rvParams.topMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, resources.getDimension(R.dimen.item_search_bar_margin), resources.displayMetrics).toInt()
            onSale_recycler_view.setLayoutParams(rvParams)

            true
        }
        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    fun filterBasedOnText(title:String){
        val recyclerView = getView()?.findViewById(R.id.onSale_recycler_view) as RecyclerView
        val emptyView: TextView? = getView()?.findViewById(R.id.onSale_empty_view)

        if(editTextMinPrice.text.isEmpty() || editTextMaxPrice.text.isEmpty()){
            //if one field is empty
            // between min and max
            // no further check needed

            val filtList = itemOnSaleViewModel.filterList(ItemStatus.PURCHASABLE, title, editTextMinPrice.text.toString(),
                editTextMaxPrice.text.toString(), filterCategory.selectedItem.toString())

            if (filtList.isEmpty()){
                recyclerView.visibility = View.GONE;
                emptyView?.visibility = View.VISIBLE;
            }
            else {
                recyclerView.visibility = View.VISIBLE;
                emptyView?.visibility = View.GONE;

                //Log.i("Lab3Debug4","Size of list of items updated : ${addsLists.size}")

                adapter.updateList(
                    filtList
                )
            }
        }
        else{ //If min and max fields are full, I can update the list only when min<=max

            val minDouble = editTextMinPrice.text.toString().toDouble()
            val maxDouble = editTextMaxPrice.text.toString().toDouble()
            if(minDouble <= maxDouble){
                editTextMinPrice.setError(null)
                editTextMaxPrice.setError(null)

                val filtList = itemOnSaleViewModel.filterList(ItemStatus.PURCHASABLE, title, editTextMinPrice.text.toString(),
                    editTextMaxPrice.text.toString(), filterCategory.selectedItem.toString())

                if (filtList.isEmpty()){
                    recyclerView.visibility = View.GONE;
                    emptyView?.visibility = View.VISIBLE;
                }
                else {
                    recyclerView.visibility = View.VISIBLE;
                    emptyView?.visibility = View.GONE;

                    //Log.i("Lab3Debug4","Size of list of items updated : ${addsLists.size}")

                    adapter.updateList(
                        filtList
                    )
                }
            }
            else{ //if I am here, both fields min and max are full and min > max
                val toast =
                    Toast.makeText(context, "min value must be less than max", Toast.LENGTH_SHORT)
                toast.show()
                editTextMinPrice.setError("Min is greater than Max")
                editTextMaxPrice.setError("Min is greater than Max")
                //editTextMinPrice.requestFocus()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        hideKeyboard(requireActivity())
    }

}

