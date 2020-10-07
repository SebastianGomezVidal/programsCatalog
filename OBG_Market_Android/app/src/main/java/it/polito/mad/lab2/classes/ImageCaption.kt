package it.polito.mad.lab2.classes

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.MutableLiveData
import it.polito.mad.lab2.repositories.Repository
import kotlinx.android.synthetic.main.fragment_2_edit_profile.*
import kotlinx.android.synthetic.main.fragment_item_edit.*
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

enum class Destination{
    ITEM_EDIT_FRAGMENT,
    EDIT_PROFILE_FRAGMENT
}

@SuppressLint("Registered")
class ImageCaption(): Activity() {

    /****************************************
     ************ Static Data ***************
     ****************************************/

    companion object {

        //Reference for Activity and Context
        private lateinit var imageContextExt: Context
        private lateinit var imageActivityExt: Activity
        private lateinit var destClass:Destination
        private lateinit var destImageUri:MutableLiveData<String>

        //Permission code gallery
        private const val PERMISSION_CODE_GALLERY = 1000

        //Permission code camera
        private const val PERMISSION_CODE_CAMERA = 1001

        //Image picture code
        private const val IMAGE_PICTURE_CODE = 1002

        //Image camera code
        private const val IMAGE_CAMERA_CODE = 1003

        //Image picture code
        private const val PERMISSION_CODE_STORAGE = 1005

        //Checking multiple permissions
        private var permission_deny: Int = 0


        //Picture path names and uri
        private var image_uri: Uri? = null
        private var imageFile: File? = null
        private var photoName: String? = null

        /********************************************
         ************ Static Function ***************
         ********************************************/

        fun imageManager(fcontext: Context, factivity: Activity, option: String, dest:Destination, fDestImgUri : MutableLiveData<String>) {

            this.imageContextExt = fcontext
            this.imageActivityExt = factivity
            this.destClass = dest
            this.destImageUri = fDestImgUri

            /********************************************
             *********** Checking permissions ***********
             ********************************************/

            when (option) {

                "gallery" -> {
                    if (ContextCompat.checkSelfPermission(fcontext, Manifest.permission.READ_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_DENIED) {
                        //Permission denied
                        val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                        //Show popup to request runtime permission
                        ActivityCompat.requestPermissions(factivity, permissions, PERMISSION_CODE_GALLERY)
                    } else pickImageFromGallery()
                }

                "camera" -> {
                    if (ContextCompat.checkSelfPermission(
                            fcontext,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ) ==
                        PackageManager.PERMISSION_DENIED && ContextCompat.checkSelfPermission(
                            fcontext,
                            Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_DENIED
                    ) {

                        // Setting the control flag to one
                        permission_deny == 1

                        //Permission denied - granted?
                        val permissions = arrayOf(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA
                        )

                        //Show popup to request runtime permission
                        ActivityCompat.requestPermissions(
                            factivity,
                            permissions,
                            PERMISSION_CODE_STORAGE
                        )

                    } else if (ContextCompat.checkSelfPermission(
                            factivity,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ) ==
                        PackageManager.PERMISSION_DENIED
                    ) {

                        //Permission denied-granted?
                        val permissions = arrayOf(Manifest.permission.CAMERA)

                        //Show popup to request runtime permission
                        ActivityCompat.requestPermissions(
                            factivity,
                            permissions,
                            PERMISSION_CODE_CAMERA
                        )

                    } else if (ContextCompat.checkSelfPermission(
                            factivity,
                            Manifest.permission.CAMERA
                        ) ==
                        PackageManager.PERMISSION_DENIED
                    ) {

                        //Permission denied - granted?
                        val permissions = arrayOf(Manifest.permission.CAMERA)

                        //Show popup to request runtime permission
                        ActivityCompat.requestPermissions(
                            factivity,
                            permissions,
                            PERMISSION_CODE_CAMERA
                        )

                    } else openCamera()
                }
            }
        }

        /************************************************************
         *************** CAMERA REQUESTS MANAGEMENT *****************
         ***********************************************************/
        //Handle requested permission result
        fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
        ) {
            when (requestCode) {
                PERMISSION_CODE_GALLERY -> {
                    if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        //permission from popup granted
                        pickImageFromGallery()
                    } else {
                        //permission from popup denied
                        Toast.makeText(imageContextExt, "Permission denied", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                PERMISSION_CODE_CAMERA -> {
                    if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                        //permission from popup was granted
                        if (permission_deny == 1) permission_deny == 0
                        else openCamera()

                    } else {
                        //permission from popup was denied
                        Toast.makeText(imageContextExt, "Permission denied", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                PERMISSION_CODE_STORAGE -> {
                    if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                        //permission from popup was granted
                        if (permission_deny == 1) permission_deny == 0
                        else openCamera()

                    } else {
                        //permission from popup was denied
                        Toast.makeText(imageContextExt, "Permission denied", Toast.LENGTH_SHORT)
                            .show()

                    }
                }
            }
        }

        /************************************************************
         ******************  CAMERA FUNCTIONS  **********************
         ************************************************************/

        //Getting an image from gallery
        private fun pickImageFromGallery() {
            //Intent to pick image
            val galleryIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            galleryIntent.type = "image/*"
            galleryIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            galleryIntent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
            imageActivityExt.startActivityForResult(galleryIntent, IMAGE_PICTURE_CODE)
        }

        //Getting an image from camera
        private fun openCamera() {

            Log.e("CHECK", "1")
            //Creating an app folder to store taken pictures
            val pictureDirectory: File? = imageContextExt.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            Log.e("CHECK", "1")
            photoName = getPictureName()
            imageFile = File(pictureDirectory, photoName)
            image_uri = FileProvider.getUriForFile(imageContextExt, "it.polito.mad.lab2", imageFile!!)

            //Making an intent to call camera app
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
            cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            cameraIntent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
            imageActivityExt.startActivityForResult(cameraIntent, IMAGE_CAMERA_CODE)
        }

        // Get Picture Name
        private fun getPictureName(): String {
            val today = LocalDateTime.now().withNano(0)
            val timeStamp = today.format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmms"))
            return "IMG" + timeStamp + ".jpg"
        }

        /*******************************************************
         **************** Camera Image Result  *****************
         *******************************************************/

        //handle result of picked image
        fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICTURE_CODE) { //case photo taken from gallery

                val f = createImageFile()
                val inputStream = imageActivityExt.contentResolver.openInputStream(data?.data!!)
                inputStream?.copyTo(f.outputStream())

                //imageActivityExt.editImageView.setImageURI(Uri.parse(f.absolutePath))
                if(destClass == Destination.EDIT_PROFILE_FRAGMENT) {
                    imageActivityExt.editImageView.setImageURI(Uri.parse(f.absolutePath))
                }
                else{
                    imageActivityExt.imageViewItem.setImageURI(Uri.parse(f.absolutePath))
                }
                destImageUri.value = f.absolutePath

                Repository.uploadImageOnFirebaseStorage("gallery",f.absolutePath)

            } else if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_CAMERA_CODE) {

                //editImageView.setImageBitmap(bitmap)
                //imageActivityExt.editImageView.setImageURI(image_uri)
                if(destClass == Destination.EDIT_PROFILE_FRAGMENT) {
                    imageActivityExt.editImageView.setImageURI(image_uri)
                }
                else{
                    imageActivityExt.imageViewItem.setImageURI(image_uri)
                }
                destImageUri.value = image_uri.toString()

                MediaStore.Images.Media.insertImage(
                    imageContextExt.contentResolver, imageFile.toString(),
                    imageFile?.name, "Image Description"
                );

                Repository.uploadImageOnFirebaseStorage("camera", image_uri.toString())
            }
        }

        private fun createImageFile(): File {

            val storageDir: File =
                imageContextExt.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
            return File(storageDir, getPictureName())
        }
    }
}


















