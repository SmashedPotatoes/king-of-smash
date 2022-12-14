package com.example.kingofsmash.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.kingofsmash.R
import com.example.kingofsmash.databinding.FragmentDiceBinding
import com.example.kingofsmash.enums.Dice
import com.example.kingofsmash.utils.initDieButton
import com.example.kingofsmash.viewmodels.DicesViewModel
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 * Use the [DiceFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DiceFragment(val onSubmit: (dices: List<Dice>) -> Unit) : Fragment() {

    private lateinit var binding: FragmentDiceBinding
    private lateinit var fragmentDiceBtns: List<Button>
    private lateinit var fragmentDiceBtnLocks: List<View>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentDiceBinding.inflate(inflater, container, false)
        binding.root.setBackgroundResource(R.color.black_transparent)
        fragmentDiceBtns = listOf(
            binding.fragmentDiceBtnDice1,
            binding.fragmentDiceBtnDice2,
            binding.fragmentDiceBtnDice3,
            binding.fragmentDiceBtnDice4,
            binding.fragmentDiceBtnDice5,
            binding.fragmentDiceBtnDice6
        )
        fragmentDiceBtnLocks = listOf(
            binding.fragmentDiceBtnImgLock1,
            binding.fragmentDiceBtnImgLock2,
            binding.fragmentDiceBtnImgLock3,
            binding.fragmentDiceBtnImgLock4,
            binding.fragmentDiceBtnImgLock5,
            binding.fragmentDiceBtnImgLock6
        )
        setDicesVisibility(diceVisible = false)
        binding.fragmentDiceImgThrowDice.setOnClickListener {
            setDicesVisibility(diceVisible = true)
        }

        val viewModel: DicesViewModel by viewModels()
        lifecycleScope.launch {
            viewModel.stateFlow.collect {
                // UPDATE DISPLAY DICES
                println(it.dices)
                for (i in 0..5) {
                    initDieButton(fragmentDiceBtns[i], it.dices[i].dice)
                    if (it.dices[i].shouldKeep) {
                        fragmentDiceBtnLocks[i].visibility = View.VISIBLE
                        val material = fragmentDiceBtns[i] as MaterialButton
                        material.strokeWidth = 10
                    } else {
                        fragmentDiceBtnLocks[i].visibility = View.INVISIBLE
                        val material = fragmentDiceBtns[i] as MaterialButton
                        material.strokeWidth = 0
                    }
                }
                if (it.roll == 0) {
                    binding.fragmentDiceBtnReroll.setBackgroundColor(resources.getColor(R.color.grey_transparent))
                }
                binding.fragmentDiceBtnReroll.isEnabled = it.roll > 0
                binding.fragmentDiceBtnReroll.text = "Reroll (${it.roll})"
            }
        }
        binding.fragmentDiceBtnReroll.setOnClickListener {
            viewModel.rollDice()
        }
        binding.fragmentDiceBtnSubmit.setOnClickListener {
            Log.d("DiceFragment", "Submitting dices")
            Log.d("DiceFragment", viewModel.stateFlow.value.dices.toString())
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

    private fun setDicesVisibility(diceVisible: Boolean) {
        val diceVisibility = if (diceVisible) View.VISIBLE else View.INVISIBLE
        binding.fragmentDiceImgThrowDice.visibility =
            if (diceVisible) View.INVISIBLE else View.VISIBLE
        fragmentDiceBtns.forEach {
            it.visibility = diceVisibility
            val material = it as MaterialButton
            material.setStrokeColorResource(R.color.green_transparent)
        }
        binding.fragmentDiceBtnReroll.visibility = diceVisibility
        binding.fragmentDiceBtnSubmit.visibility = diceVisibility
        binding.fragmentDiceTxtTitle.visibility = diceVisibility
    }
}