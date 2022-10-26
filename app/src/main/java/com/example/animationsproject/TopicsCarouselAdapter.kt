package com.example.animationsproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.helper.widget.Carousel
import kotlin.random.Random

class TopicsCarouselAdapter(private val topicsActions: TopicsActions) : Carousel.Adapter {
    private val list = arrayListOf<String>("Срочные новости", "VPN", "Пусто", "Пусто", "Пусто")
    override fun count(): Int = list.size

    override fun populate(view: View?, index: Int) {
        val textView = (view as TextView)
        textView.text = list[index]
    }

    override fun onNewItem(index: Int) {
        when(index){
            0 -> topicsActions.makeRandomNews()
            1 -> topicsActions.showVPN()
            else -> topicsActions.empty()
        }
    }
}