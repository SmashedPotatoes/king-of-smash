package com.example.kingofsmash.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.kingofsmash.R
import com.example.kingofsmash.enums.Action
import com.example.kingofsmash.viewmodels.DicesViewModel
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 * Use the [DiceFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DiceFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val viewModel: DicesViewModel by viewModels()
        lifecycleScope.launch {
            viewModel.stateFlow.collect {
                // UPDATE DISPLAY DICES
            }
        }

        return inflater.inflate(R.layout.fragment_dice, container, false)
    }
}