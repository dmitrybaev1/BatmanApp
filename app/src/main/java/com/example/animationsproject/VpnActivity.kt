package com.example.animationsproject

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.os.Bundle
import android.text.Html
import androidx.transition.Scene
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.example.animationsproject.databinding.ActivityVpnBinding
import com.example.animationsproject.databinding.VpnSceneMainBinding
import com.example.animationsproject.databinding.VpnScenePreparationBinding
import kotlin.math.roundToInt

class VpnActivity : AppCompatActivity(){


    private lateinit var rootLayout: ConstraintLayout
    private lateinit var phoneImageView: ImageView
    private lateinit var contentLayout: LinearLayout
    private lateinit var thingLeft: ImageView
    private lateinit var thingUp: ImageView
    private lateinit var thingRight: ImageView
    private lateinit var holder: ImageView
    private lateinit var stepIndicatorImageView: ImageView
    private lateinit var titleTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var nextStepButton: Button
    private lateinit var cancelButton: Button
    private lateinit var activityVpnBinding: ActivityVpnBinding
    private lateinit var vpnSceneMainBinding: VpnSceneMainBinding
    private lateinit var vpnScenePreparationBinding: VpnScenePreparationBinding
    private val vpnAnimator = VpnAnimator()
    private var stepCount = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityVpnBinding = ActivityVpnBinding.inflate(layoutInflater)
        vpnScenePreparationBinding = activityVpnBinding.scene
        setContentView(activityVpnBinding.root)
        val sceneRoot: FrameLayout = activityVpnBinding.vpnSceneRoot
        val endingScene = Scene.getSceneForLayout(sceneRoot,R.layout.vpn_scene_main,this)
        val yesButton: Button = vpnScenePreparationBinding.yesButton
        yesButton.setOnClickListener {
            TransitionManager.go(endingScene,AutoTransition())
            vpnSceneMainBinding = VpnSceneMainBinding.bind(endingScene.sceneRoot.getChildAt(0))
            initMain()
            vpnAnimator.playToStep1Anim()
        }
    }

    private fun initMain(){
        rootLayout = vpnSceneMainBinding.mainLayout
        phoneImageView = vpnSceneMainBinding.phoneImageView
        contentLayout = vpnSceneMainBinding.contentLayout
        thingLeft = vpnSceneMainBinding.thingLeft
        thingUp = vpnSceneMainBinding.thingUp
        thingRight = vpnSceneMainBinding.thingRight
        holder = vpnSceneMainBinding.holder
        stepIndicatorImageView = vpnSceneMainBinding.stepIndicatorImageView
        titleTextView = vpnSceneMainBinding.titleTextView
        descriptionTextView = vpnSceneMainBinding.descriptionTextView
        cancelButton = vpnSceneMainBinding.cancelButton
        cancelButton.setOnClickListener {
            finishAfterTransition()
        }
        nextStepButton = findViewById(R.id.nextStepButton)
        nextStepButton.setOnClickListener {
            when(stepCount){
                1 -> {++stepCount;vpnAnimator.playToStep2Anim()}
                2 -> {++stepCount;cancelButton.visibility = View.VISIBLE;vpnAnimator.playToStep3Anim()}
                3 -> VpnModalBottomSheet().show(supportFragmentManager,"")
            }
        }
    }



    inner class VpnAnimator{
        fun playToStep1Anim(){

            val contentUpAnim = (AnimatorInflater.loadAnimator(this@VpnActivity, R.animator.vpn_content_up) as AnimatorSet).apply {
                setTarget(contentLayout)
                interpolator = AccelerateInterpolator()
            }
            val buttonUpAnim = (AnimatorInflater.loadAnimator(this@VpnActivity, R.animator.vpn_content_up) as AnimatorSet).apply {
                setTarget(nextStepButton)
                interpolator = AccelerateInterpolator()
            }
            val holderAnimUp = (AnimatorInflater.loadAnimator(this@VpnActivity, R.animator.vpn_holder_transition_up) as AnimatorSet).apply {
                setTarget(holder)
                interpolator = DecelerateInterpolator()
            }
            val phoneAppearAnim = (AnimatorInflater.loadAnimator(this@VpnActivity, R.animator.vpn_phone_picture_appear) as AnimatorSet).apply {
                setTarget(phoneImageView)
                interpolator = LinearInterpolator()
            }
            val thingLeftToRightAnim = (AnimatorInflater.loadAnimator(this@VpnActivity, R.animator.vpn_thing_left_to_right) as AnimatorSet).apply {
                setTarget(thingLeft)
                interpolator = AccelerateInterpolator()
            }
            val thingUpToDownAnim = (AnimatorInflater.loadAnimator(this@VpnActivity, R.animator.vpn_thing_up_to_down) as AnimatorSet).apply {
                setTarget(thingUp)
                interpolator = AccelerateInterpolator()
            }
            val thingRightToLeftAnim = (AnimatorInflater.loadAnimator(this@VpnActivity, R.animator.vpn_thing_right_to_left) as AnimatorSet).apply {
                setTarget(thingRight)
                interpolator = AccelerateInterpolator()
            }
            AnimatorSet().apply {
                //play(contentDownAnim).with(phoneDownAnim).with(buttonDownAnim).before(holderAnimUp)
                play(holderAnimUp).with(phoneAppearAnim).with(thingLeftToRightAnim)
                    .with(thingRightToLeftAnim).with(thingUpToDownAnim).before(contentUpAnim)
                play(contentUpAnim).with(buttonUpAnim)
                start()
            }
        }
        fun playToStep2Anim(){
            val phoneDownAnim = (AnimatorInflater.loadAnimator(this@VpnActivity, R.animator.vpn_content_down) as AnimatorSet).apply {
                setTarget(phoneImageView)
                interpolator = AccelerateInterpolator()
                //сменить изображение на шаг 2
                addListener(object : AnimatorListenerAdapter(){
                    override fun onAnimationEnd(animation: Animator) {
                        phoneImageView.setImageResource(R.drawable.phone_vpn_2)
                    }
                })
            }
            val contentDownAnim = (AnimatorInflater.loadAnimator(this@VpnActivity, R.animator.vpn_content_down) as AnimatorSet).apply {
                setTarget(contentLayout)
                interpolator = AccelerateInterpolator()
                //сменить текст на шаг 2
                addListener(object : AnimatorListenerAdapter(){
                    override fun onAnimationEnd(animation: Animator) {
                        titleTextView.text = resources.getString(R.string.vpn_title_step_2)
                        descriptionTextView.text = resources.getString(R.string.vpn_description_step_2)
                        stepIndicatorImageView.setImageResource(R.drawable.step_indicator_2)
                    }
                })
            }
            val buttonDownAnim = (AnimatorInflater.loadAnimator(this@VpnActivity, R.animator.vpn_content_down) as AnimatorSet).apply {
                setTarget(nextStepButton)
                interpolator = AccelerateInterpolator()
            }
            val contentUpAnim = (AnimatorInflater.loadAnimator(this@VpnActivity, R.animator.vpn_content_up) as AnimatorSet).apply {
                setTarget(contentLayout)
                interpolator = AccelerateInterpolator()
            }
            val buttonUpAnim = (AnimatorInflater.loadAnimator(this@VpnActivity, R.animator.vpn_content_up) as AnimatorSet).apply {
                setTarget(nextStepButton)
                interpolator = AccelerateInterpolator()
            }
            val holderAnim1 = (AnimatorInflater.loadAnimator(this@VpnActivity, R.animator.vpn_holder_transition_1) as AnimatorSet).apply {
                setTarget(holder)
                interpolator = DecelerateInterpolator()
            }
            val phoneAppearAnim = (AnimatorInflater.loadAnimator(this@VpnActivity, R.animator.vpn_phone_picture_appear) as AnimatorSet).apply {
                setTarget(phoneImageView)
                interpolator = LinearInterpolator()
            }
            AnimatorSet().apply {
                play(contentDownAnim).with(phoneDownAnim).with(buttonDownAnim).before(holderAnim1)
                play(holderAnim1).with(phoneAppearAnim).with(contentUpAnim).with(buttonUpAnim)
                start()
            }
        }
        fun playToStep3Anim(){
            val phoneDownAnim = (AnimatorInflater.loadAnimator(this@VpnActivity, R.animator.vpn_content_down) as AnimatorSet).apply {
                setTarget(phoneImageView)
                interpolator = AccelerateInterpolator()
                //сменить изображение на шаг 3
                addListener(object : AnimatorListenerAdapter(){
                    override fun onAnimationEnd(animation: Animator) {
                        phoneImageView.setImageResource(R.drawable.phone_vpn_3)
                    }
                })
            }
            val contentDownAnim = (AnimatorInflater.loadAnimator(this@VpnActivity, R.animator.vpn_content_down) as AnimatorSet).apply {
                setTarget(contentLayout)
                interpolator = AccelerateInterpolator()
                //сменить текст на шаг 3
                addListener(object : AnimatorListenerAdapter(){
                    override fun onAnimationEnd(animation: Animator) {
                        titleTextView.text = resources.getString(R.string.vpn_title_step_3)
                        descriptionTextView.setText(Html.fromHtml(resources.getString(R.string.vpn_description_step_3),Html.FROM_HTML_MODE_LEGACY),TextView.BufferType.SPANNABLE)
                        descriptionTextView.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
                        descriptionTextView.setPadding(dpToPx(35f,this@VpnActivity).roundToInt(),0,0,0)
                        descriptionTextView.textSize = 18f
                        stepIndicatorImageView.setImageResource(R.drawable.step_indicator_3)
                        nextStepButton.layoutParams.height = dpToPx(60f,this@VpnActivity).roundToInt()
                        nextStepButton.setText(Html.fromHtml(resources.getString(R.string.try_it),Html.FROM_HTML_MODE_LEGACY),TextView.BufferType.SPANNABLE)
                    }
                })
            }
            val buttonDownAnim = (AnimatorInflater.loadAnimator(this@VpnActivity, R.animator.vpn_content_down) as AnimatorSet).apply {
                setTarget(nextStepButton)
                interpolator = AccelerateInterpolator()
            }
            val contentUpAnim = (AnimatorInflater.loadAnimator(this@VpnActivity, R.animator.vpn_content_up) as AnimatorSet).apply {
                setTarget(contentLayout)
                interpolator = AccelerateInterpolator()
            }
            val buttonUpAnim = (AnimatorInflater.loadAnimator(this@VpnActivity, R.animator.vpn_content_up) as AnimatorSet).apply {
                setTarget(nextStepButton)
                interpolator = AccelerateInterpolator()
            }
            val cancelButtonUpAnim = (AnimatorInflater.loadAnimator(this@VpnActivity, R.animator.vpn_content_up) as AnimatorSet).apply {
                setTarget(cancelButton)
                interpolator = AccelerateInterpolator()
            }
            val holderAnim2 = (AnimatorInflater.loadAnimator(this@VpnActivity, R.animator.vpn_holder_transition_2) as AnimatorSet).apply {
                setTarget(holder)
                interpolator = DecelerateInterpolator()
            }
            val phoneAppearAnim = (AnimatorInflater.loadAnimator(this@VpnActivity, R.animator.vpn_phone_picture_appear) as AnimatorSet).apply {
                setTarget(phoneImageView)
                interpolator = LinearInterpolator()
            }
            AnimatorSet().apply {
                play(contentDownAnim).with(phoneDownAnim).with(buttonDownAnim).before(holderAnim2)
                play(holderAnim2).with(phoneAppearAnim).with(contentUpAnim).with(buttonUpAnim).with(cancelButtonUpAnim)
                start()
            }
        }
    }
}
