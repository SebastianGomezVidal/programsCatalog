package it.polito.mad.lab2.fragments



import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import it.polito.mad.lab2.R
import it.polito.mad.lab2.activities.MapsActivity
import it.polito.mad.lab2.classes.hideKeyboard
import it.polito.mad.lab2.databinding.Fragment2EditProfileBinding
import it.polito.mad.lab2.viewmodels.ItemsViewModel
import it.polito.mad.lab2.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.fragment_2_edit_profile.*


class EditProfileFragment : Fragment() {

    private lateinit var binding: Fragment2EditProfileBinding

    private val userViewModel: UserViewModel by activityViewModels()
    private val itemViewModel: ItemsViewModel by activityViewModels()

    private val rCode: Int = 500
    private var latitude:Double? = null
    private var longitude:Double? = null

    /************************************
     ********* On Create View ************
     *************************************/

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //Enable options menu in fragment
        setHasOptionsMenu(true)

        // Inflate view and obtain an instance of the binding class
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_2_edit_profile,
            container,
            false
        )

        // Set the viewmodel for databinding - this allows the bound layout access
        // to all the data in the VieWModel
        binding.userd = userViewModel

        // Specify the fragment view as the lifecycle owner of the binding.
        // This is used so that the binding can observe LiveData updates
        binding.lifecycleOwner = viewLifecycleOwner


        userViewModel.getCurrentUser().observe(viewLifecycleOwner, Observer{

            Log.e("USER", it.fullName)

            if(it.profileImg == "") binding.editImageView.setImageResource(R.drawable.avatar1)
            else binding.editImageView.setImageURI(Uri.parse(it.profileImg))
            binding.editFullName.setText(it.fullName)
            binding.editNickName.setText(it.nickName)
            binding.editEmail.setText(it.email)
            binding.editLocation.setText(it.location)
            Log.d("Lab3Debug","EditProfileFragment: $it")

            itemViewModel.getUserMeanRating(FirebaseAuth.getInstance().currentUser!!.uid).observe(viewLifecycleOwner, Observer {
                binding.tvRating.text = it.toString()
            })
        })

        //Binding the camera icon with the context menu
        registerForContextMenu(binding.root.findViewById(R.id.srcPic))


        if (userViewModel.getCurrentUser().value?.latitude != 0.0)
        {
            Log.e("HERE","HERERERERE")
            latitude = userViewModel.getCurrentUser().value?.latitude
            longitude = userViewModel.getCurrentUser().value?.longitude
        }

        binding.googleButton?.setOnClickListener(){
            val intent = Intent(activity, MapsActivity::class.java)
            intent.putExtra("latitude",  latitude)
            intent.putExtra("longitude", longitude)
            intent.putExtra("action", 1)
            startActivityForResult(intent, rCode)
        }

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.srcPic.setOnClickListener {
            //Toast.makeText(this.context, "Before openContextMenu", Toast.LENGTH_SHORT).show()
            requireActivity().openContextMenu(it)
            //Toast.makeText(this.context, "After openContextMenu", Toast.LENGTH_SHORT).show()
        }

    }

    // --> Disk icon option
    /****************************************************
     ********* Overriding Activity's Toolbar ************
     ****************************************************/

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_save, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    /****************************************************
     ************ Toolbar Options Selected   ************
     ****************************************************/

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.menu_save -> {
                //Saving the information of the user
                //in case there is missing or wrong data
                //stays on the editProfile otherwise
                //jumps to ShowProfile

                // explanation : if I am here, the user was on showProfile, then clicked the edit button
                // and goes in editProfile  The first popBackStack remove editProfile from the backStack,
                // the second one, remove also showProfile this is needed because the instruction navigate
                // after the popBackStacks add another time showProfile to the backStack

                lateinit var result:String

                //code needed because otherwise the app will crash if I click on save and latitude and longitude are null!
                if(latitude != null && longitude != null) {
                    result = userViewModel.save(latitude!!, longitude!!)
                }
                else
                    result = "coord"

                Log.d("Lab3Debug","EDITPROFILE: ${editEmail.text}")


                if (result.equals("Ok")) {
                    //The operation of saving the user in firebase is done in the userViewModel
                    hideKeyboard(requireActivity())
                    findNavController().popBackStack()
                    findNavController().popBackStack()
                    findNavController().navigate(R.id.nav_profile)
                }else {

                    if (result.equals("Name")) {
                        val toast = Toast.makeText(
                            activity,
                            "Complete full name field!",
                            Toast.LENGTH_SHORT
                        )
                        toast.show()
                        binding.editFullName.requestFocus()
                    } else if (result.equals("Nick")) {
                        val toast = Toast.makeText(
                            activity,
                            "Complete nickname field!",
                            Toast.LENGTH_SHORT
                        )
                        toast.show()
                        binding.editNickName.requestFocus()
                    } else if (result.equals("Email")) {
                        val toast = Toast.makeText(
                            activity,
                            "Complete email field with a correct email!",
                            Toast.LENGTH_SHORT
                        )
                        toast.show()
                        binding.editEmail.requestFocus()
                    } else if (result.equals("Location")) {
                        val toast =
                            Toast.makeText(
                                activity,
                                "Complete Location field!",
                                Toast.LENGTH_SHORT
                            )
                        toast.show()
                        binding.editLocation.requestFocus()
                    } else if (result.equals("Wemail")) {
                        val toast =
                            Toast.makeText(
                                activity,
                                "Wrong email field!",
                                Toast.LENGTH_SHORT
                            )
                        toast.show()
                        binding.editEmail.setError("Inserted email is wrong")
                        binding.editLocation.requestFocus()
                    }
                    else if (result.equals("coord")) {
                        val toast =
                            Toast.makeText(
                                activity,
                                "Pick Coordinates!",
                                Toast.LENGTH_SHORT
                            )
                        toast.show()
                    }
                    else {

                    }
                }

                true
            }
            else -> super.onOptionsItemSelected(menuItem)
        }
    }

    /****************************************************
     ************ Overriding Context Menu ***************
     ****************************************************/

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val inflater: MenuInflater? = activity?.menuInflater
        //inflate method is the operation used to perform the inflation of menu object
        inflater?.inflate(R.menu.floating_menu, menu)
    }

    /*********************************************************
     ************ Context Menu Options Selected   ************
     *********************************************************/

    override fun onContextItemSelected(item: MenuItem): Boolean {
        // Handle item selection

        return when (item.itemId) {
            R.id.gallery -> {
                userViewModel.gallery(requireContext(),requireActivity())
                true
            }
            R.id.camera -> {
                userViewModel.camera(requireContext(),requireActivity())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == rCode) {
            if (resultCode == Activity.RESULT_OK) {
                latitude = data?.getDoubleExtra("latitude", 0.0)!!
                longitude = data.getDoubleExtra("longitude", 0.0)
            }
        }
    }
}






