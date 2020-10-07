package it.polito.mad.lab2.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import it.polito.mad.lab2.R
import it.polito.mad.lab2.fragments.ItemEditFragment
import kotlinx.android.synthetic.main.fragment_item_edit.*
class TestToolBarActivity : AppCompatActivity() {

    // TODO to be removed and also the layout associated; used only for testing the toolbar with image,
    //  without the navigation stuff

    private var ief = ItemEditFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_tool_bar)

        if (savedInstanceState != null) {
            //Restore the fragment's instance
            ief = supportFragmentManager.getFragment(savedInstanceState, "myFragmentName") as ItemEditFragment;
        }
        else{
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container_view_test,ief,"Frag1Tag")
                .addToBackStack(null)
                .commit()
        }



        setSupportActionBar(toolbarItemEdit)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        //Save the fragment's instance
        supportFragmentManager.putFragment(outState, "myFragmentName", ief!!);
    }

}
