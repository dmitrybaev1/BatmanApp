package com.example.animationsproject

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.example.animationsproject.databinding.VpnBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class VpnModalBottomSheet: BottomSheetDialogFragment() {
    private lateinit var binding: VpnBottomSheetBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = VpnBottomSheetBinding.inflate(inflater,container,false)
        return binding.root
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
        binding.coordinator.minimumHeight = Resources.getSystem().displayMetrics.heightPixels
        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback(){
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if(newState == BottomSheetBehavior.STATE_COLLAPSED)
                     behavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

        })
    }
}