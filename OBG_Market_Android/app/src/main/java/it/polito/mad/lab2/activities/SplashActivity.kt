package it.polito.mad.lab2.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import it.polito.mad.lab2.R

class SplashActivity : AppCompatActivity() {

    //Variables
    private val SPLASH_SCREEN :Long = 500

    var topAnim: Animation? = null
    var botAnim: Animation? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        //Animations
        //topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation)
        //botAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation)

        topAnim = AnimationUtils.loadAnimation(this, android.R.anim.fade_in)
        botAnim = AnimationUtils.loadAnimation(this, android.R.anim.fade_in)

        //Hooks
        val logo: ImageView = findViewById(R.id.logoImage)
        val logoText: TextView = findViewById(R.id.logoText)

        logo.startAnimation(topAnim)
        logoText.startAnimation(botAnim)

        val mRunnable = Runnable {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        Handler().postDelayed(mRunnable, SPLASH_SCREEN)
    }
}
