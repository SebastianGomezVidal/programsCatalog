package it.polito.mad.lab2.fragments

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.*
import android.util.Log
import android.view.*
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.android.volley.RequestQueue
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import it.polito.mad.lab2.R
import it.polito.mad.lab2.activities.MapsActivity
import it.polito.mad.lab2.classes.*
import it.polito.mad.lab2.viewmodels.ItemsViewModel
import it.polito.mad.lab2.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.fragment_item_edit.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class ItemEditFragment : Fragment(), AdapterView.OnItemSelectedListener {

    /****************************************
     *********** Global Variables ***********
     ****************************************/

    //private var itemIndex: Int = -1
    private lateinit var myContext: Context

    private val itemEditImg_key = "itemImage"
    private val itemEditTitle_key = "title"
    private val itemEditDescription_key = "description"
    private val itemEditPrice_key = "price"
    private val itemEditCategory_key = "category"
    private val itemEditLocation_key = "location"
    private val itemEditExpiryDate_key = "expiryDate"

    private val rCode = 600
    private var lat: Double? = null
    private var long: Double? = null

    private var itemEditImg_value = MutableLiveData<String>()
    //Image Location

    private val itemViewModel: ItemsViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()


    //private val requestQueue: RequestQueue = SingletonRequestQueue.getInstance(requireContext()).requestQueue

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_item_edit, container, false)
    }

    override fun onResume() {
        super.onResume()

        val button = view?.findViewById<ImageButton>(R.id.googleButtonItem)
        button?.setOnClickListener() {

            if (itemViewModel.currentItem_itemId != "") {
                val intent = Intent(activity, MapsActivity::class.java)
                intent.putExtra("action", 1)
                intent.putExtra("latitude", itemViewModel.itemDetailAdds.value!!.latitude)
                intent.putExtra("longitude", itemViewModel.itemDetailAdds.value!!.longitude)
                startActivityForResult(intent, rCode)

            } else {
                val intent = Intent(activity, MapsActivity::class.java)
                intent.putExtra("action", 1)
                intent.putExtra("latitude", 1000.0)
                intent.putExtra("longitude", 1000.0)
                startActivityForResult(intent, rCode)
            }
        }
    }

    override fun onAttach(pContext: Context) {
        super.onAttach(pContext)
        myContext = pContext
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(
            true // default to enabled
        ) {
            override fun handleOnBackPressed() {
                MaterialAlertDialogBuilder(context)
                    .setTitle("Warning")
                    .setMessage("You will loose all the unsaved changes.\nDo you want to continue?")
                    .setPositiveButton("OK"){
                            dialog: DialogInterface, which: Int ->
                        //Toast.makeText(context,"DONE",Toast.LENGTH_SHORT)
                        dialog.dismiss()
                        findNavController().navigateUp()
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
        requireActivity().onBackPressedDispatcher.addCallback(
            this,  // LifecycleOwner
            callback
        )
    }

    //called after onActivity created
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (savedInstanceState != null) {

            itemEditImg_value.value = savedInstanceState.getString(itemEditImg_key,Uri.parse(
                "android.resource://" + R::class.java.getPackage()!!.name + "/" + R.drawable.item).toString())
            imageViewItem.setImageURI(Uri.parse(itemEditImg_value.value))

            //Restore the fragment's state here
            tIET_Title.setText(savedInstanceState.getString(itemEditTitle_key))
            tIET_Description.setText(savedInstanceState.getString(itemEditDescription_key))
            tIET_Price.setText(savedInstanceState.getString(itemEditPrice_key))
            tIET_Title.setText(savedInstanceState.getString(itemEditTitle_key))

            tfCategory.setSelection(savedInstanceState.getLong(itemEditCategory_key).toInt())

            tIET_Location.setText(savedInstanceState.getString(itemEditLocation_key))
            tIET_expiryDate.setText(savedInstanceState.getString(itemEditExpiryDate_key))
        }
    }

    override fun onStart() {
        super.onStart()
        itemEditImg_value.observe(this, androidx.lifecycle.Observer { imageViewItem.setImageURI(Uri.parse(it)) })
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        //Save the fragment's state here
        outState.putString(itemEditImg_key, itemEditImg_value.value)
        outState.putString(itemEditTitle_key, tIET_Title.text.toString())
        outState.putString(itemEditDescription_key, tIET_Description.text.toString())
        outState.putString(itemEditPrice_key, tIET_Price.text.toString())
        outState.putLong(itemEditCategory_key, tfCategory.selectedItemId)
        outState.putString(itemEditLocation_key, tIET_Location.text.toString())
        outState.putString(itemEditExpiryDate_key, tIET_expiryDate.text.toString())

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbarItemEdit.setOnMenuItemClickListener { menuItem ->
            Log.d("Lab2Debug", "optionSelected")
            when (menuItem.itemId) {
                R.id.save_item -> {
                    saveItem()
                    true
                }
                else -> false
            }
        }

        //code needed to change toolbar and to make the back button correctly working
        val navHostFragment = NavHostFragment.findNavController(this);
        //NavigationUI.setupWithNavController(toolbarItemEdit, navHostFragment)
        toolbarItemEdit.setNavigationOnClickListener {
            hideKeyboard(requireActivity())
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }


        registerForContextMenu(changeItemSrcPic)
        //code needed to open the context menu at the single click of the image!
        changeItemSrcPic.setOnClickListener {
            //Toast.makeText(myContext, "Change item image clicked", Toast.LENGTH_SHORT).show()
            requireActivity().openContextMenu(it)
        }

        //tIET stands for TextInputEditText
        //this code allows not to show the keyboards when clicking on expiryDate field
        tIET_expiryDate.keyListener = null

        val handleDatePicker = {

            hideKeyboard(requireActivity())

            // Use the current date as the default date in the picker
            val currentTimeCalendar = Calendar.getInstance()
            val currentYear = currentTimeCalendar.get(Calendar.YEAR)
            val currentMonth = currentTimeCalendar.get(Calendar.MONTH)
            val currentDay = currentTimeCalendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(
                requireContext(),
                DatePickerDialog.OnDateSetListener { DialogView, selectedYear,
                                                     selectedMonth, selectedDayOfMonth ->

                    //create a new calendar object with the date set to the actual date
                    val selectedTimeCalendar = Calendar.getInstance()
                    //variable used for checks
                    val today = Calendar.getInstance()

                    //set to the date created, the year, month and day selected by the user
                    selectedTimeCalendar[Calendar.YEAR] = selectedYear
                    selectedTimeCalendar[Calendar.MONTH] = selectedMonth
                    selectedTimeCalendar[Calendar.DAY_OF_MONTH] = selectedDayOfMonth


                    if(today.before(selectedTimeCalendar)){
                        //format the calendar object with the date chosen by the user to a string dd/mm/yyyy
                        val currentDateString: String =
                            SimpleDateFormat("dd/MM/yyyy").format(selectedTimeCalendar.time)

                        //set the string formatted to the textInputEditText
                        tIET_expiryDate.text = SpannableStringBuilder(currentDateString)

                    }
                    else {
                        Snackbar.make(tfExpiryDate, "Date inserted is not valid. Expiration must be after today", Snackbar.LENGTH_SHORT)
                            .show()
                    }

                },
                currentYear,
                currentMonth,
                currentDay
            ).show()
        }

        //handle the endIconClick
        tfExpiryDate.setEndIconOnClickListener {
            handleDatePicker()
        }

        //handle the first click on tIET_expiryDate
        tIET_expiryDate.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus)
                handleDatePicker()
        }

        //handle the others clicks on tIET_expiryDate
        tIET_expiryDate.setOnClickListener {
            handleDatePicker()
        }


        // Create an ArrayAdapter using the string array and a default spinner layout
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.categories,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            tfCategory.adapter = adapter
        }

        val tw = object: TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                Log.e("Lab2Debug","editable : ${editable.toString()}")
                val moneySymbol="$"
                if (!editable.toString().startsWith(moneySymbol)) {
                    tIET_Price.setText(moneySymbol + editable);
                    Selection.setSelection(tIET_Price.getText(), tIET_Price
                        .getText()?.length ?: 0);
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
        }

        tIET_Price.addTextChangedListener(tw)

        tfCategory.onItemSelectedListener = this

        arguments?.let {//if a new item is created, arguments is null and this block of code not executed
                        //otherwise, if we come here from itemDetails or by clicking on the edit button
                        //of an item in itemlist, arguments is not null
            Log.e("Lab3Debug","Coming from itemList!")
            itemViewModel.currentItem_itemId = it.getString(AddsArray.ARG_ITEM_INDEX,"")
            itemViewModel.currentItem_ownerId = it.getString(AddsArray.ARG_ITEM_OWNER,"")
        }

        //tfStatus adapter setup
        val statusNamesList = mutableListOf<String>()
        enumValues<ItemStatus>().forEach { statusNamesList.add(it.name) }
        val statusAdapter = ArrayAdapter(requireContext(), R.layout.list_item, statusNamesList)
        (tfStatus.editText as? AutoCompleteTextView)?.setAdapter(statusAdapter)

        //Actv = AutoComplete TextView
        actv_status.setInputType(InputType.TYPE_NULL);


        //tfBuyer adapter setup
        userViewModel.getUserList().observe(viewLifecycleOwner, androidx.lifecycle.Observer {

            val lstNickname = mutableListOf<String>()
            lstNickname.add(getString(R.string.buyer))
            for ((userId,usr) in it){
                if (userId != FirebaseAuth.getInstance().currentUser?.uid)
                    lstNickname.add(usr.nickName)
            }

            val buyerAdapter = ArrayAdapter(requireContext(), R.layout.list_item, lstNickname)
            (tfBuyer.editText as? AutoCompleteTextView)?.setAdapter(buyerAdapter)

        })

        actv_buyer.setInputType(InputType.TYPE_NULL)

        if(itemViewModel.currentItem_itemId != ""){ //case in which the item is already existing

            tfStatus.visibility = View.VISIBLE  // the tfStatus is shown for allowing to choose the status
            tfBuyer.visibility = View.VISIBLE // the tfBuyer is shown for allowing to choose the buyer

            if(Utils.arrivesToEditItem == ArrivesToEditItem.FROM_ITEM_LIST) { //case coming directly from the itemList
                itemViewModel.getItemGivenUserAndId(
                    itemViewModel.currentItem_ownerId,
                    itemViewModel.currentItem_itemId
                ).observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                    val currentItem = it

                    itemEditImg_value.value = currentItem.imgPath
                    //imageViewItem.setImageURI(Uri.parse(itemEditImg_value.value))
                    loadImageFromRemote(itemEditImg_value.value!!,requireContext(),imageViewItem)
                    tIET_Title.text = SpannableStringBuilder(currentItem.title)
                    tIET_Description.text = SpannableStringBuilder(currentItem.description)
                    tIET_Price.text = SpannableStringBuilder(currentItem.price)

                    val categoryIndex = adapter.getPosition(currentItem.category)
                    tfCategory.setSelection(categoryIndex)

                    tIET_Location.text = SpannableStringBuilder(currentItem.location)
                    tIET_expiryDate.text = SpannableStringBuilder(currentItem.expireDate)

                    actv_status.text = SpannableStringBuilder(currentItem.status.name)
                    //this strange line below allows to get all the name inside the dropdown even
                    // if the auto complete already contain some text
                    ((tfStatus.editText as? AutoCompleteTextView)?.adapter as ArrayAdapter<String>).filter
                        .filter(null)

                    if(currentItem.buyerId != null) { //case in which the owner has a buyer
                        Log.e("Lab4Debug","Item Buyer != null")
                        userViewModel.getUser(currentItem.buyerId!!)
                            .observe(viewLifecycleOwner, androidx.lifecycle.Observer { user ->

                                Log.e("Lab4Debug","buyer retrieved")

                                actv_buyer.text = SpannableStringBuilder(user.nickName)
                                //this strange line below allows to get all the name inside the dropdown even
                                // if the auto complete already contain some text
                                ((tfBuyer.editText as? AutoCompleteTextView)?.adapter as ArrayAdapter<String>).filter
                                    .filter(null)
                            })
                    }
                })
            }
            else{ //case coming from item details
                val currentItem = itemViewModel.itemDetailAdds.value!!

                itemEditImg_value.value = currentItem.imgPath
                //imageViewItem.setImageURI(Uri.parse(itemEditImg_value.value))
                loadImageFromRemote(itemEditImg_value.value!!,requireContext(),imageViewItem)

                tIET_Title.text = SpannableStringBuilder(currentItem.title)
                tIET_Description.text = SpannableStringBuilder(currentItem.description)
                tIET_Price.text = SpannableStringBuilder(currentItem.price)

                val categoryIndex = adapter.getPosition(currentItem.category)
                tfCategory.setSelection(categoryIndex)

                tIET_Location.text = SpannableStringBuilder(currentItem.location)
                tIET_expiryDate.text = SpannableStringBuilder(currentItem.expireDate)

                actv_status.text = SpannableStringBuilder(currentItem.status.name)
                //this strange line below allows to get all the name inside the dropdown even
                // if the auto complete already contain some text
                ((tfStatus.editText as? AutoCompleteTextView)?.adapter as ArrayAdapter<String>).filter
                    .filter(null)

                if(currentItem.buyerId != null) { //case in which the owner has a buyer
                    Log.e("Lab4Debug","Item Buyer != null")
                    userViewModel.getUser(currentItem.buyerId!!)
                        .observe(viewLifecycleOwner, androidx.lifecycle.Observer { user ->

                            Log.e("Lab4Debug","buyer retrieved")

                            actv_buyer.text = SpannableStringBuilder(user.nickName)
                            //this strange line below allows to get all the name inside the dropdown even
                            // if the auto complete already contain some text
                            ((tfBuyer.editText as? AutoCompleteTextView)?.adapter as ArrayAdapter<String>).filter
                                .filter(null)
                        })
                }
            }
        }
        else{ //case creation of a new item
            tfStatus.visibility = View.GONE // if I am here a new item is going to be created and then its
                                            // status is always purchasable
            tfBuyer.visibility = View.GONE //it is not possible to create an item and assign it a buyer during the creation

        }

    }

    private fun saveItem() {
        Log.e("Lab3Debug","inside save item!")

        if (listOf(tIET_Title.text?.length, tIET_Description.text?.length, tIET_Price.text?.length,
                tIET_Location.text?.length, tIET_expiryDate.text?.length).all{it != 0} &&
            tIET_Price.text?.length != 1 && lat != null && long != null &&//in the case of price I add also the check length != 1 because it contains also "$"
            resources.getDrawable(R.drawable.item).toBitmap() != imageViewItem.drawable.toBitmap())
        {
            hideKeyboard(requireActivity())

            Log.e("Lab3Debug","inside save item!")

            if(itemViewModel.currentItem_itemId == "") { //if i am creating a new item
                Log.e("Lab3Debug","New item created")
                //Saving the data on the singleton list for advertisements
                Log.e("Lab3Debug",itemEditImg_value.value.toString())

                // in this case, I create a new add and then I will not pass tfStatus value because is always set to
                // purchasable for the creation(in itemViewModel)
                itemViewModel.makeAdd(
                    imgPath= itemEditImg_value.value.toString(),
                    Title = tIET_Title.text.toString(),
                    Description = tIET_Description.text.toString(),
                    Price = tIET_Price.text.toString(),
                    Category = tfCategory.selectedItem.toString(),
                    Location = tIET_Location.text.toString(),
                    latitude = lat!!,
                    longitude = long!!,
                    ExpireDate = tIET_expiryDate.text.toString()
                )
                    /*
                    Beware, toast are not show because tied to the lifecycle of itemeditfragment which is paused when
                    immediately after click on save button

                    .observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                    Log.e("Lab3Debug","item boolean : $it")
                    if(it == true){
                        Toast.makeText(activity,"Item successfully created", Toast.LENGTH_SHORT)
                    }
                    else{
                        Toast.makeText(activity,"Error during item creation", Toast.LENGTH_SHORT)
                    }
                })*/

                //explanation : if I am here, the user was on item list, then clicked the fab button and goes in itemEdit.
                //the first popBackStack remove itemEdit from the backStack, the second one, remove also itemList from the
                //backstack, this is needed because the instruction navigate after the popBackStacks add another time
                //itemList to the backStack
                findNavController().popBackStack()
                findNavController().popBackStack()
                findNavController().navigate(R.id.nav_adds)
            }
            else{ //case in which I am editing a certain item

                val newStatus = when(actv_status.text.toString()){
                    ItemStatus.PURCHASABLE.name -> ItemStatus.PURCHASABLE
                    ItemStatus.SOLD.name -> ItemStatus.SOLD
                    ItemStatus.BLOCKED.name -> ItemStatus.BLOCKED
                    else -> throw Exception()
                }

                //if a have sold the item, it is needed to add also a buyer!!
                if (newStatus == ItemStatus.SOLD &&
                    (actv_buyer.text!!.isEmpty() || actv_buyer.text!!.toString() == getString(R.string.buyer))) {
                    val toast = Toast.makeText(context, "Complete Buyer field!", Toast.LENGTH_SHORT)
                    toast.show()
                    actv_buyer.requestFocus()
                    return
                }

                //if the status is not sold, the buyer must be empty or "Buyer"
                if (newStatus != ItemStatus.SOLD){

                    //case in which the field is not empty and contains a word different from "Buyer"
                    //it is the only one in which I have to block the edit
                    if(actv_buyer.text!!.isNotEmpty() && actv_buyer.text!!.toString() != getString(R.string.buyer)) {
                        val toast = Toast.makeText(context, "Buyer field must be empty or set to 'Buyer'!", Toast.LENGTH_SHORT)
                        toast.show()
                        actv_buyer.requestFocus()
                        return
                    }
                }

                //create a new adds with the same id and owner and list of interested user unchanged
                val editedAdds = itemViewModel.itemDetailAdds.value!!.copy(
                    imgPath = itemEditImg_value.value.toString(),
                    title = tIET_Title.text.toString(),
                    description = tIET_Description.text.toString(),
                    price = tIET_Price.text.toString(),
                    category = tfCategory.selectedItem.toString(),
                    location = tIET_Location.text.toString(),
                    expireDate = tIET_expiryDate.text.toString(),
                    latitude = lat!!,
                    longitude = long!!,
                    status = newStatus,
                    buyerId = getBuyerId(newStatus)
                )
                val requestQueue: RequestQueue = SingletonRequestQueue.getInstance(requireContext()).requestQueue

                itemViewModel.editAdd(requestQueue,editedAdds,userViewModel.getCurrentUser().value!!.nickName)

                // explanation : if I am here, the user was on item list, then clicked the edit button and goes in itemEdit or
                // the user was in itemDetails and click the edit button. The first popBackStack remove itemEdit from the
                // backStack, the second one, remove also itemList or itemDetails from the backstack, this is needed because
                // the instruction navigate after the popBackStacks add another time itemList or itemDetails to the backStack
                findNavController().popBackStack()
                findNavController().popBackStack()

                //Toast.makeText(myContext, "Save Item Clicked", Toast.LENGTH_SHORT).show()
                //val b = bundleOf(AddsArray.ARG_ITEM_INDEX to itemIndex)

                //check if the user was on the list or on item details, before arriving there.
                //It is needed because it allows to return on the right page for handling better popBackStack
                if(Utils.arrivesToEditItem == ArrivesToEditItem.FROM_ITEM_DETAILS) {
                    //findNavController().navigate(R.id.itemDetailsFragment, b)
                    findNavController().navigate(R.id.itemDetailsFragment)
                }
                else
                    findNavController().navigate(R.id.nav_adds)
            }

        } else {
            if(resources.getDrawable(R.drawable.item).toBitmap() == imageViewItem.drawable.toBitmap()){
                val toast = Toast.makeText(context, "Attach an image!", Toast.LENGTH_SHORT)
                toast.show()
            }
            else if (tIET_Title.text!!.isBlank()){
                val toast = Toast.makeText(context, "Complete title field!", Toast.LENGTH_SHORT)
                toast.show()
                tIET_Title.requestFocus()
            }
            else if (tIET_Description.text!!.isBlank()){
                val toast = Toast.makeText(context, "Complete description field!", Toast.LENGTH_SHORT)
                toast.show()
                tIET_Description.requestFocus()
            }
            else if (tIET_Price.text!!.isBlank() || tIET_Price.text!!.length == 1){
                val toast = Toast.makeText(context, "Enter a price!", Toast.LENGTH_SHORT)
                toast.show()
                tIET_Price.requestFocus()
            }
            else if (tIET_Location.text!!.isBlank()) {
                val toast = Toast.makeText(context, "Complete Location field!", Toast.LENGTH_SHORT)
                toast.show()
                tIET_Location.requestFocus()
            }
            else if (tIET_expiryDate.text!!.isEmpty()) {
                val toast = Toast.makeText(context, "Complete Expiry Date field!", Toast.LENGTH_SHORT)
                toast.show()
                tIET_expiryDate.requestFocus()
            }
            else if (lat == null || long == null) {
                val toast = Toast.makeText(context, "Pick a Location!", Toast.LENGTH_SHORT)
                toast.show()
            }

        }

    }

    //not called at the first time because the spinner already contain the first element
    override fun onNothingSelected(parent: AdapterView<*>?) {
        return
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        return
    }


    /****************************************
     ************ Floating Bar **************
     ****************************************/

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val inflater: MenuInflater = requireActivity().menuInflater
        //inflate method is the operation used to perform the inflation of menu object
        inflater.inflate(R.menu.floating_menu, menu)
    }


    override fun onContextItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.gallery -> {
                ImageCaption.imageManager(requireContext(),requireActivity(),"gallery",
                    Destination.ITEM_EDIT_FRAGMENT,itemEditImg_value)
                true
            }
            R.id.camera -> {
                ImageCaption.imageManager(requireContext(),requireActivity(),"camera",
                    Destination.ITEM_EDIT_FRAGMENT, itemEditImg_value)
                true
            }
            else -> {
                    super.onOptionsItemSelected(item)
                    false
            }
        }
    }

    fun getBuyerId(itemStatus: ItemStatus) : String?{
        if(itemStatus != ItemStatus.SOLD)
            return null

        return if (actv_buyer.text.toString() != getString(R.string.buyer)){
            //userViewModel.getUserList().value
            val userList = userViewModel.getUserList().value!!
            var idToBeReturned:String? = null
            for ((userId,usr) in userList){
                if (usr.nickName == actv_buyer.text.toString()) {
                    idToBeReturned = userId
                }
            }
            idToBeReturned
        } else
            null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == rCode) {
            if (resultCode == Activity.RESULT_OK) {
                lat = data?.getDoubleExtra("latitude", 1000.0)
                long = data?.getDoubleExtra("longitude", 1000.0)
                Log.e("XXX", lat.toString())
                Log.e("XXX", long.toString())
            }
        }
        /*fun getBuyerId(itemStatus: ItemStatus): String? {
            if (itemStatus != ItemStatus.SOLD)
                return null

            if (actv_buyer.text.toString() != getString(R.string.buyer)) {
                //userViewModel.getUserList().value
                val userList = userViewModel.getUserList().value!!
                var idToBeReturned: String? = null
                for ((userId, usr) in userList) {
                    if (usr.nickName == actv_buyer.text.toString()) {
                        idToBeReturned = userId
                    }
                }
                return idToBeReturned
            } else
                return null
        }*/
    }
}
