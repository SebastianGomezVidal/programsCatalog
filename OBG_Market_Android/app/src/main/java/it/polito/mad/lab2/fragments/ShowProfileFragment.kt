package it.polito.mad.lab2.fragments


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import it.polito.mad.lab2.R
import it.polito.mad.lab2.databinding.Fragment1ShowProfileBinding
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.google.firebase.auth.FirebaseAuth
import it.polito.mad.lab2.activities.MapsActivity
import it.polito.mad.lab2.classes.loadImageFromRemote
import it.polito.mad.lab2.viewmodels.ItemsViewModel
import it.polito.mad.lab2.viewmodels.UserViewModel


/**
 * A simple [Fragment] subclass.
 */
class ShowProfileFragment : Fragment() {

    private lateinit var binding: Fragment1ShowProfileBinding

    private val userViewModel: UserViewModel by activityViewModels()
    private val itemViewModel: ItemsViewModel by activityViewModels()

    private var latitude:Double? = null
    private var longitude:Double? = null



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        //Toolbar options
        setHasOptionsMenu(true)

        val otherUser = arguments?.getBoolean("otherUser")
        val currentItem_ownerId = arguments?.getString("currentItem_ownerId")

        // Inflate view and obtain an instance of the binding class
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_1_show_profile,
            container,
            false
        )

        //viewModel = ViewModelProvider(activity as MainActivity).get(UserViewModel::class.java)
        // Set the viewmodel for databinding - this allows the bound layout access
        // to all the data in the VieWModel
        binding.user = userViewModel

        // Specify the fragment view as the lifecycle owner of the binding.
        // This is used so that the binding can observe LiveData updates
        binding.lifecycleOwner = viewLifecycleOwner

        //Observe variables
        /*
        I do not user this variable but directly the user returned from the database because it is easy to copy the value
        from the user to the variable but it is difficult to say to this variable that they should consider also the user as
        source of the data because user could not contains any value at a certain time and be filled later from the db

        viewModel.profileImg_value.observe(viewLifecycleOwner, Observer<String> { img ->
            binding.imageView8.setImageURI(Uri.parse(img))
        })

        viewModel.fullName_value.observe(viewLifecycleOwner, Observer<String> { name ->
            binding.fullName.setText(viewModel.fullName_value.value)
        })

        viewModel.nickName_value.observe(viewLifecycleOwner, Observer<String> { nick ->
            binding.nickName.setText(nick)
        })

        viewModel.email_value.observe(viewLifecycleOwner, Observer<String> { mail ->
            binding.email.setText(mail)
        })

        viewModel.location_value.observe(viewLifecycleOwner, Observer<String> { location ->
            binding.location.setText(location)
        })*/


        //update drawer headerLayout
        //val navView: NavigationView = requireActivity().findViewById(R.id.nav_view)
        //val headerView = navView.getHeaderView(0)

        //val navView: NavigationView = requireActivity().findViewById(R.id.nav_view)
        //val headerView = navView.getHeaderView(0)

        if(otherUser != null && otherUser){ //case user owner of a certain item
            userViewModel.getUser(currentItem_ownerId!!).observe(viewLifecycleOwner, Observer{

                //binding.imageView8.setImageURI(Uri.parse(it.profileImg))
                loadImageFromRemote(it.profileImg,requireContext(),binding.imageView8)  //in the case of remote user, the
                                                                                        //profile image should be downloaded
                                                                                        //from the server
                //binding.imageView8.setImageURI(Uri.parse(it.profileImg))
                binding.fullName.setText(it.nickName)
                binding.fullName.textAlignment = View.TEXT_ALIGNMENT_CENTER
                binding.nickName.setText(it.nickName)
                binding.email.setText(it.email)
                binding.location.setText(it.location)

                itemViewModel.getUserMeanRating(currentItem_ownerId!!).observe(viewLifecycleOwner, Observer {
                    binding.tvRating.text = it.toString()
                })

            })

        }else{ //case using showProfile for showing the current user

            Log.e("111", "I am here")

            userViewModel.getCurrentUser().observe(viewLifecycleOwner, Observer{

                if(it.profileImg == "") binding.imageView8.setImageResource(R.drawable.avatar1)
                else binding.imageView8.setImageURI(Uri.parse(it.profileImg))
                binding.fullName.setText(it.fullName)
                binding.nickName.setText(it.nickName)
                binding.email.setText(it.email)
                binding.location.setText(it.location)

                latitude = it.latitude
                longitude = it.longitude

                itemViewModel.getUserMeanRating(FirebaseAuth.getInstance().currentUser!!.uid).observe(viewLifecycleOwner, Observer {
                    binding.tvRating.text = it.toString()
                })
            })

        }

        /*viewModel.getCurrentUser().observe(viewLifecycleOwner, Observer{
            binding.imageView8.setImageURI(Uri.parse(it.profileImg))
            binding.fullName.setText(it.fullName)
            binding.nickName.setText(it.nickName)
            binding.email.setText(it.email)
            binding.location.setText(it.location)
        })*/

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Binding the camera icon with the context menu
        //registerForContextMenu(binding.root.findViewById(R.id.srcPic))

        binding.googleButtonShowPro?.setOnClickListener(){
            val intent = Intent(activity, MapsActivity::class.java)
            intent.putExtra("latitude",  latitude)
            intent.putExtra("longitude", longitude)
            intent.putExtra("action", 2)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        val otherUser = arguments?.getBoolean("otherUser")
        //val currentItem_ownerId = arguments?.getString("currentItem_ownerId")

        if(otherUser != null && otherUser){
            inflater.inflate(R.menu.main, menu);
        }else{
            inflater.inflate(R.menu.menu_edit,menu)
        }

        super.onCreateOptionsMenu(menu,inflater);
    }

    override fun onOptionsItemSelected(menuItem: MenuItem ):Boolean{
        return when (menuItem.itemId) {
            R.id.menu_edit -> {
                findNavController().navigate(R.id.nav_editProfile)
                true
            }
            else -> false
        }
    }

}
