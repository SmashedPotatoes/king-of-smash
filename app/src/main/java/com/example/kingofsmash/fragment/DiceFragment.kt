package com.example.kingofsmash.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.kingofsmash.R
import com.example.kingofsmash.databinding.FragmentDiceBinding
import com.example.kingofsmash.enums.Character
import com.example.kingofsmash.enums.Dice
import com.example.kingofsmash.viewmodels.DicesViewModel
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 * Use the [DiceFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DiceFragment(val onSubmit: (dices: List<Dice>) -> Unit) : Fragment() {

    private lateinit var binding: FragmentDiceBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDiceBinding.inflate(inflater, container, false)
        binding.root.setBackgroundColor(R.color.black_transparent)
        val viewModel: DicesViewModel by viewModels()
        lifecycleScope.launch {
            viewModel.stateFlow.collect {
                // UPDATE DISPLAY DICES
                println(it.dices)
                binding.fragmentDiceBtnDice1.text = it.dices[0].dice.name
                binding.fragmentDiceBtnDice2.text = it.dices[1].dice.name
                binding.fragmentDiceBtnDice3.text = it.dices[2].dice.name
                binding.fragmentDiceBtnDice4.text = it.dices[3].dice.name
                binding.fragmentDiceBtnDice5.text = it.dices[4].dice.name
                binding.fragmentDiceBtnDice6.text = it.dices[5].dice.name
            }
        }
        binding.fragmentDiceBtnReroll.setOnClickListener {
            viewModel.rollDice()
        }
        binding.fragmentDiceBtnSubmit.setOnClickListener {
            println("SUBMIT DICES")
            println(viewModel.stateFlow.value.dices)
            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.remove(this)
            fragmentTransaction.commit()
            onSubmit(viewModel.stateFlow.value.dices.map { it.dice })
        }
        binding.fragmentDiceBtnDice1.setOnClickListener {
            viewModel.toggleKeepDice(0)
        }
        binding.fragmentDiceBtnDice2.setOnClickListener {
            viewModel.toggleKeepDice(1)
        }
        binding.fragmentDiceBtnDice3.setOnClickListener {
            viewModel.toggleKeepDice(2)
        }
        binding.fragmentDiceBtnDice4.setOnClickListener {
            viewModel.toggleKeepDice(3)
        }
        binding.fragmentDiceBtnDice5.setOnClickListener {
            viewModel.toggleKeepDice(4)
        }
        binding.fragmentDiceBtnDice6.setOnClickListener {
            viewModel.toggleKeepDice(5)
        }
        return binding.root
    }
}