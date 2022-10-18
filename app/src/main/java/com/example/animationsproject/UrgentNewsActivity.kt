package com.example.animationsproject

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.res.Resources
import android.os.Bundle
import android.os.PersistableBundle
import android.view.animation.BounceInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class UrgentNewsActivity : AppCompatActivity() {
    private lateinit var newsImageView: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_urgent)
        //title = "Новостной выпуск"
        newsImageView = findViewById(R.id.newsImageView)
        when(intent.getIntExtra("newsNumber",5)){
            0 -> newsImageView.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.gotham_times_1))
            1 -> newsImageView.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.gotham_times_2))
            2 -> newsImageView.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.gotham_times_3))
            3 -> newsImageView.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.gotham_times_4))
        }
        (AnimatorInflater.loadAnimator(this, R.animator.urgent_news_animator) as AnimatorSet).apply {
            setTarget(newsImageView)
            interpolator = DecelerateInterpolator()
            start()
        }
    }
}