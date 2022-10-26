package com.example.animationsproject

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.media.MediaPlayer
import android.os.Bundle
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class UrgentNewsActivity : AppCompatActivity() {

    private lateinit var newsImageView: ImageView
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_urgent)
        mediaPlayer = MediaPlayer.create(this,R.raw.urgent_sound)
        //title = "Новостной выпуск"
        newsImageView = findViewById(R.id.newsImageView)
        when(intent.getIntExtra("newsNumber",5)){
            0 -> newsImageView.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.gotham_times_1))
            1 -> newsImageView.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.gotham_times_2))
            2 -> newsImageView.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.gotham_times_3))
            3 -> newsImageView.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.gotham_times_4))
        }
        (AnimatorInflater.loadAnimator(this, R.animator.urgent_news_rotation) as AnimatorSet).apply {
            setTarget(newsImageView)
            interpolator = DecelerateInterpolator()
            start()
        }
    }

    override fun onResume() {
        super.onResume()
        mediaPlayer.start()
    }

    override fun onStop() {
        super.onStop()
        mediaPlayer.stop()
    }
}