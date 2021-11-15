package com.android04.godfisherman.ui.stopwatch

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.android04.godfisherman.R
import com.android04.godfisherman.databinding.FragmentStopwatchInfoBinding
import com.android04.godfisherman.ui.base.BaseFragment
import com.android04.godfisherman.ui.main.MainActivity

class StopwatchInfoFragment : BaseFragment<FragmentStopwatchInfoBinding, StopwatchInfoViewModel>(R.layout.fragment_stopwatch_info) {
    override val viewModel: StopwatchInfoViewModel by viewModels()
    lateinit var intent: Intent

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        intent = Intent(requireContext(), StopwatchActivity::class.java)
        skipInfo()
//        binding.btnStart.setOnClickListener { startActivity(intent) }
        binding.btnStart.setOnClickListener {
            val activity = requireActivity() as MainActivity
            activity.viewModel.stopwatchOnFlag.value = true
            activity.viewModel.isFromInfoFragment = true
        }
    }

    private fun skipInfo(){
        if (StopwatchActivity.isStopwatchServiceRunning) {
            startActivity(intent)
        }
    }
}
