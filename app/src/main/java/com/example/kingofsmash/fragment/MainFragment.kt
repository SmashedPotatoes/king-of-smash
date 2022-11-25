package com.example.kingofsmash.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.example.kingofsmash.R
import com.example.kingofsmash.databinding.FragmentMainBinding
import com.example.kingofsmash.enums.Action
import com.example.kingofsmash.enums.Dice
import com.example.kingofsmash.enums.PlayerType
import com.example.kingofsmash.viewmodels.KingOfSmashViewModel
import kotlinx.coroutines.launch

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding
    private lateinit var viewModel: KingOfSmashViewModel
    private val args: MainFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        viewModel = KingOfSmashViewModel(args.character)

        lifecycleScope.launch {
            viewModel.stateFlow.collect {
                when (it.currentAction) {
                    Action.THROW_DICES -> {
                        println("THROW DICESU")
                        val currentPlayer = viewModel.getCurrentPlayer()
                        // TODO: Refacto into currentPlayer.throwDices(context)
                        if (currentPlayer.type == PlayerType.PLAYER) {
                            val fragment = DiceFragment(onSubmit = { dices ->
                                viewModel.throwDices(dices)
                            });
                            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
                            val fragmentTransaction = fragmentManager.beginTransaction()
                            fragmentTransaction.add(R.id.fragmentContainerView, fragment)
                            fragmentTransaction.commit()
                        } else {
                            val dices = listOf(Dice.ONE, Dice.TWO, Dice.THREE, Dice.ONE, Dice.THREE, Dice.TWO)
                            viewModel.throwDices(dices)
                        }
                    }

                    Action.EXECUTE_DICES -> {
                        println("EXECUTE DICESU")
                        // DISPLAY DICES AND EXECUTE
                        viewModel.executeDices()
                    }

                    Action.EXECUTE_CARDS -> {
                        println("EXECUTE CARDSU")
                        viewModel.waitEndTurn()
                    }

                    Action.WAIT_END_TURN -> {
                        println("WAIT END TURNU")
                        val currentPlayer = viewModel.getCurrentPlayer()
                        // TODO: Refacto into currentPlayer.endTurn(context)
                        if (currentPlayer.type == PlayerType.PLAYER) {
                            binding.fragmentMainBtnEndOfTurn.visibility = View.VISIBLE
                            binding.fragmentMainBtnEndOfTurn.setOnClickListener {
                                viewModel.endTurn()
                                binding.fragmentMainBtnEndOfTurn.visibility = View.INVISIBLE
                            }
                        } else {
                            viewModel.endTurn()
                        }
                    }
                }

                // TODO: SET PLAYER CARD INFO (STOCK, SMASH, SMASH_METER, CARD COLOR, IS_KING)
                println("CurrentPlayerIdx: ${it.currentPlayerIdx}")
            }
        }
        // Inflate the layout for this fragment
        return binding.root
    }
}