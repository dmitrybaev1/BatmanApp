package com.example.animationsproject

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class VpnModalBottomSheet: BottomSheetDialogFragment() {
    private lateinit var coordinatorLayout: CoordinatorLayout
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.vpn_bottom_sheet,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val behavior = BottomSheetBehavior.from(dialog!!.findViewById(com.google.android.material.R.id.design_bottom_sheet))
        behavior.apply {
            //isFitToContents = false
            skipCollapsed = true
            halfExpandedRatio = 0.8f
            state = BottomSheetBehavior.STATE_HALF_EXPANDED
        }
        coordinatorLayout = view.findViewById(R.id.coordinator)
        coordinatorLayout.minimumHeight = Resources.getSystem().displayMetrics.heightPixels
        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback(){
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when(newState){
                    BottomSheetBehavior.STATE_EXPANDED -> Log.e("tag","EXPANDED")
                    BottomSheetBehavior.STATE_HALF_EXPANDED -> Log.e("tag","HALF_EXPANDED")
                    BottomSheetBehavior.STATE_SETTLING -> Log.e("tag","SETTLING")
                    BottomSheetBehavior.STATE_DRAGGING -> Log.e("tag","DRAGGING")
                    BottomSheetBehavior.STATE_HIDDEN -> Log.e("tag","HIDDEN")
                    BottomSheetBehavior.STATE_COLLAPSED -> behavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

        })
    }
}