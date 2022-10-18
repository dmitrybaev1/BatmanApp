package com.example.animationsproject

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.helper.widget.Carousel
import androidx.core.content.ContextCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner

class MainActivity : AppCompatActivity(), TopicsActions {
    private lateinit var rootLayout: LinearLayout
    private lateinit var carousel: Carousel
    private lateinit var newsLayout: LinearLayout
    private lateinit var newsTitleTextView: TextView
    private lateinit var newsDescriptionTextView: TextView
    private lateinit var newsButton: Button
    private var randomNewsNumber = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        title = "Главная страница"
        rootLayout = findViewById(R.id.rootLayout)
        newsLayout = findViewById(R.id.newsLayout)
        newsTitleTextView = findViewById(R.id.titleNewsTextView)
        newsDescriptionTextView = findViewById(R.id.descriptionNewsTextView)
        newsButton = findViewById(R.id.newsButton)
        newsLayout.visibility = View.GONE
        carousel = findViewById(R.id.carousel)
        carousel.setAdapter(TopicsCarouselAdapter(this))
        newsButton.setOnClickListener {
            val intent = Intent(this,UrgentNewsActivity::class.java)
            intent.putExtra("newsNumber",randomNewsNumber)
            startActivity(intent)
        }
        PowerBroadcastReceiver()
        makeRandomNews()
    }

    override fun makeRandomNews() {
        newsLayout.visibility = View.VISIBLE
        randomNewsNumber = java.util.Random().nextInt(4)
        Log.d("random",randomNewsNumber.toString())
        when(randomNewsNumber){
            0 -> newsDescriptionTextView.text = resources.getString(R.string.gotham_times_1)
            1 -> newsDescriptionTextView.text = resources.getString(R.string.gotham_times_2)
            2 -> newsDescriptionTextView.text = resources.getString(R.string.gotham_times_3)
            3 -> newsDescriptionTextView.text = resources.getString(R.string.gotham_times_4)
        }

    }

    override fun empty() {
        newsLayout.visibility = View.GONE
    }

    private fun setUnprotectedBackground(){
        rootLayout.background = ContextCompat.getDrawable(this,R.drawable.unprotected_state)
        Toast.makeText(this,"Внимание: питание подключено, вы уязвимы",Toast.LENGTH_LONG).show()
    }

    private fun setProtectedBackground(){
        rootLayout.background = ContextCompat.getDrawable(this,R.color.background_white)
        Toast.makeText(this,"Питание отключено, вы защищены",Toast.LENGTH_LONG).show()
    }

    inner class PowerBroadcastReceiver : BroadcastReceiver(), DefaultLifecycleObserver{

        init{
            lifecycle.addObserver(this)
        }

        private val filter = IntentFilter().apply {
            addAction(Intent.ACTION_POWER_DISCONNECTED)
            addAction(Intent.ACTION_POWER_CONNECTED)
        }
        private val flags = ContextCompat.RECEIVER_EXPORTED

        override fun onStart(owner: LifecycleOwner) {
            detectUsbConnection()
            ContextCompat.registerReceiver(this@MainActivity, this, filter, flags)
            Log.d("START","onStart")
        }

        override fun onStop(owner: LifecycleOwner) {
            this@MainActivity.unregisterReceiver(this)
            Log.d("STOP","onStop")
        }

        override fun onReceive(context: Context?, intent: Intent?) {
            when(intent?.action){
                Intent.ACTION_POWER_DISCONNECTED -> setProtectedBackground()
                Intent.ACTION_POWER_CONNECTED -> setUnprotectedBackground()
            }
        }

        private fun detectUsbConnection(){
            val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
                this@MainActivity.registerReceiver(null, ifilter)
            }
            val chargePlug = batteryStatus?.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1) ?: -1
            val usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB
            if(usbCharge)
                setUnprotectedBackground()
            else
                setProtectedBackground()
        }

    }
}