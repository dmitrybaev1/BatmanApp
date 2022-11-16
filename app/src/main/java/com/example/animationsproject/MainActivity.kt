package com.example.animationsproject

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.app.ActivityOptions
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.BatteryManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.cardview.widget.CardView
import androidx.constraintlayout.helper.widget.Carousel
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

class MainActivity : AppCompatActivity(), TopicsActions {
    private lateinit var rootLayout: ConstraintLayout
    private lateinit var howToTextView: TextView
    private lateinit var mainLayout: LinearLayout
    private lateinit var carouselCardView: CardView
    private lateinit var carousel: Carousel
    private lateinit var newsCardView: CardView
    private lateinit var vpnCardView: CardView
    private lateinit var blurCardView: CardView
    private lateinit var newsTitleTextView: TextView
    private lateinit var newsDescriptionTextView: TextView
    private lateinit var newsButton: Button
    private lateinit var vpnButton: Button
    private lateinit var blurButton: Button
    private var randomNewsNumber = 0
    private var currentNewsNumber = 0

    companion object{
        const val CHANNEL_ID = "Main"
        const val NEGATIVE_NOTIFICATION_ID = 0
        const val CAROUSEL_INDEX_TAG = "carouselIndex"
        const val CURRENT_NEWS_TAG = "currentNews"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        rootLayout = findViewById(R.id.rootLayout)
        howToTextView = findViewById(R.id.howToTextView)
        mainLayout = findViewById(R.id.mainLayout)
        newsCardView = findViewById(R.id.newsCardView)
        vpnCardView = findViewById(R.id.vpnCardView)
        blurCardView = findViewById(R.id.blurCardView)
        blurButton = findViewById(R.id.blurButton)
        newsTitleTextView = findViewById(R.id.titleNewsTextView)
        newsDescriptionTextView = findViewById(R.id.descriptionNewsTextView)
        newsButton = findViewById(R.id.newsButton)
        vpnButton = findViewById(R.id.vpnButton)
        carousel = findViewById(R.id.carousel)
        carousel.setAdapter(TopicsCarouselAdapter(this))
        carouselCardView = findViewById(R.id.carouselCardView)
        findViewById<TextView>(R.id.button0).setOnClickListener {
            howToTextView.visibility = View.VISIBLE
        }
        carouselCardView.setOnClickListener {
            howToTextView.visibility = View.VISIBLE
            playHintAppearAnim()
        }
        howToTextView.setOnClickListener {
            playHintDisappearAnim()
        }
        newsButton.setOnClickListener {
            val intent = Intent(this,UrgentNewsActivity::class.java)
            intent.putExtra("newsNumber",randomNewsNumber)
            startActivity(intent)
        }
        vpnButton.setOnClickListener {
            val intent = Intent(this,VpnActivity::class.java)
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        }
        blurButton.setOnClickListener {
            val intent = Intent(this,BlurActivity::class.java)
            startActivity(intent)
        }
        PowerBroadcastReceiver()
        savedInstanceState?.let {
            if(it.containsKey(CAROUSEL_INDEX_TAG) && it.getInt(CAROUSEL_INDEX_TAG) == 0 && it.containsKey(CURRENT_NEWS_TAG)) {
                currentNewsNumber = it.getInt(CURRENT_NEWS_TAG)
                setNews(currentNewsNumber)
            }
            else
                makeRandomNews()
        } ?: run{makeRandomNews()}
    }

    private fun playHintAppearAnim(){
        (AnimatorInflater.loadAnimator(this, R.animator.hint_appear) as AnimatorSet).apply {
            setTarget(howToTextView)
            interpolator = LinearInterpolator()
            start()
        }
    }

    private fun playHintDisappearAnim(){
        (AnimatorInflater.loadAnimator(this, R.animator.hint_disappear) as AnimatorSet).apply {
            setTarget(howToTextView)
            interpolator = LinearInterpolator()
            start()
            addListener(object : AnimatorListenerAdapter(){
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    howToTextView.visibility = View.GONE
                }
            })
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(CAROUSEL_INDEX_TAG,carousel.currentIndex)
        Log.d("currentNewNumberSave",currentNewsNumber.toString())
        outState.putInt(CURRENT_NEWS_TAG,currentNewsNumber)
    }

    private fun setNews(newsNumber: Int){
        newsCardView.visibility = View.VISIBLE
        vpnCardView.visibility = View.INVISIBLE
        blurCardView.visibility = View.INVISIBLE
        when(newsNumber){
            0 -> newsDescriptionTextView.text = resources.getString(R.string.gotham_times_1)
            1 -> newsDescriptionTextView.text = resources.getString(R.string.gotham_times_2)
            2 -> newsDescriptionTextView.text = resources.getString(R.string.gotham_times_3)
            3 -> newsDescriptionTextView.text = resources.getString(R.string.gotham_times_4)
        }
    }

    override fun makeRandomNews() {
        randomNewsNumber = java.util.Random().nextInt(4)
        setNews(randomNewsNumber)
        currentNewsNumber = randomNewsNumber
        Log.d("currentNewsNumber",currentNewsNumber.toString())
    }

    override fun empty() {
        newsCardView.visibility = View.INVISIBLE
        vpnCardView.visibility = View.INVISIBLE
        blurCardView.visibility = View.INVISIBLE
    }

    override fun showVPN() {
        newsCardView.visibility = View.INVISIBLE
        vpnCardView.visibility = View.VISIBLE
        blurCardView.visibility = View.INVISIBLE
    }

    override fun blurJoker() {
        newsCardView.visibility = View.INVISIBLE
        vpnCardView.visibility = View.INVISIBLE
        blurCardView.visibility = View.VISIBLE
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

        @RequiresApi(33)
        private val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {}

        private val notificationManagerCompat = NotificationManagerCompat.from(this@MainActivity)

        init{
            lifecycle.addObserver(this)
        }

        private val filter = IntentFilter().apply {
            addAction(Intent.ACTION_POWER_DISCONNECTED)
            addAction(Intent.ACTION_POWER_CONNECTED)
        }
        private val flags = ContextCompat.RECEIVER_EXPORTED

        override fun onStart(owner: LifecycleOwner) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel()
            }
            detectUsbConnection()
            ContextCompat.registerReceiver(this@MainActivity, this, filter, flags)
        }

        override fun onStop(owner: LifecycleOwner) {
            this@MainActivity.unregisterReceiver(this)
        }

        override fun onReceive(context: Context?, intent: Intent?) {
            when(intent?.action){
                Intent.ACTION_POWER_DISCONNECTED -> {
                    if(getConnectionStatus() != false.toString()) {
                        writeConnectionStatus(false)
                        destroyNegativeNotification()
                        setProtectedBackground()
                    }
                }
                Intent.ACTION_POWER_CONNECTED -> {
                    if(getConnectionStatus() != true.toString()) {
                        writeConnectionStatus(true)
                        createNegativeNotification()
                        setUnprotectedBackground()
                    }
                }
            }
        }

        @RequiresApi(26)
        private fun createNotificationChannel() {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

        }

        private fun detectUsbConnection(){
            val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
                this@MainActivity.registerReceiver(null, ifilter)
            }
            val chargePlug = batteryStatus?.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1) ?: -1
            val usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB
            if(getConnectionStatus() != usbCharge.toString()) {
                writeConnectionStatus(usbCharge)
                if (usbCharge) {
                    createNegativeNotification()
                    setUnprotectedBackground()
                } else {
                    destroyNegativeNotification()
                    setProtectedBackground()
                }
            }
        }

        private fun getConnectionStatus(): String?{
            val sharedPref = getPreferences(Context.MODE_PRIVATE)
            return sharedPref.getString("connected",null)
        }
        private fun writeConnectionStatus(connected: Boolean){
            val sharedPref = getPreferences(Context.MODE_PRIVATE)
            with(sharedPref.edit()){
                putString("connected",connected.toString())
                apply()
            }
        }

        private fun createNegativeNotification(){
            if(Build.VERSION.SDK_INT >= 33){
                if(ContextCompat.checkSelfPermission(this@MainActivity, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED){
                    buildAndShowNegativeNotification()
                }
                else
                    requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
            else {
                buildAndShowNegativeNotification()
            }

        }
        private fun buildAndShowNegativeNotification(){
            val builder = NotificationCompat.Builder(this@MainActivity, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Внимание!")
                .setContentText("Когда вы подключены по проводу, злодеи могут проникнуть в ваш телефон. Мы рекомендуем вам отключиться.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(PendingIntent.getActivity(this@MainActivity, 0, Intent(), 0))
                .setAutoCancel(true)
            notificationManagerCompat.notify(NEGATIVE_NOTIFICATION_ID, builder.build())
        }

        private fun destroyNegativeNotification(){
            notificationManagerCompat.cancel(NEGATIVE_NOTIFICATION_ID)
        }
    }
}