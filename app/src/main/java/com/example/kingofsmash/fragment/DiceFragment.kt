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
import com.example.kingofsmash.viewmodels.DicesViewModel
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
                    displayButtonDice(fragmentDiceBtns[i], it.dices[i].dice)
                    if (it.dices[i].shouldKeep) {
                        fragmentDiceBtnLocks[i].visibility = View.VISIBLE
                    } else {
                        fragmentDiceBtnLocks[i].visibility = View.INVISIBLE
                    }
                }
                binding.fragmentDiceBtnReroll.isEnabled = it.roll > 0
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
        binding.fragmentDiceImgThrowDice.visibility = if (diceVisible) View.INVISIBLE else View.VISIBLE
        fragmentDiceBtns.forEach { it.visibility = diceVisibility }
        binding.fragmentDiceBtnReroll.visibility = diceVisibility
        binding.fragmentDiceBtnSubmit.visibility = diceVisibility
    }

    private fun displayButtonDice(button: Button, dice: Dice) {
        button.text = ""
        button.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)

        when (dice) {
            Dice.ONE -> button.text = "1"
            Dice.TWO -> button.text = "2"
            Dice.THREE -> button.text = "3"
            Dice.SMASH_METER -> button.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.icon_smash_meter, 0, 0)
            Dice.SMASH -> button.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.icon_smash, 0, 0)
            Dice.STOCK -> button.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.icon_stock, 0, 0)
        }
    }
}