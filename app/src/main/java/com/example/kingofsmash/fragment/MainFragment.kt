package com.example.kingofsmash.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.example.kingofsmash.R
import com.example.kingofsmash.enums.Action
import com.example.kingofsmash.viewmodels.KingOfSmashViewModel
import kotlinx.coroutines.launch

class MainFragment : Fragment() {

    private val args: MainFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewModel: KingOfSmashViewModel by lazy {
            KingOfSmashViewModel(args.character)
        }
        lifecycleScope.launch {
            viewModel.stateFlow.collect {
                when (it.currentAction) {
                    Action.THROW_DICES -> {
                        println("THROWDICESU")
                    }
                    Action.EXECUTE_DICES -> {

                    }

                    Action.EXECUTE_CARDS -> {

                    }
                }
            }
        }
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false)
    }
}