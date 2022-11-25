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
import com.example.kingofsmash.models.PlayerCard
import com.example.kingofsmash.viewmodels.KingOfSmashViewModel
import kotlinx.coroutines.launch

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding
    private lateinit var viewModel: KingOfSmashViewModel
    private val args: MainFragmentArgs by navArgs()
    private lateinit var playerCards: List<PlayerCard>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        viewModel = KingOfSmashViewModel(args.character)

        playerCards = getPlayerCards()
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
                playerCards.forEach { playerCard ->
                    val player = it.players[playerCard.id]
                    playerCard.game.text = player.game.toString()
                    playerCard.stock.text = player.stock.toString()
                    playerCard.smashMeter.text = player.smashMeter.toString()

                    // TODO: this is done every time,
                    playerCard.icon.setImageResource(player.character.icon)
                    playerCard.name.text = player.character.character
                }
            }
        }
        // Inflate the layout for this fragment
        return binding.root
    }

    private fun getPlayerCards() = listOf(
        PlayerCard(
            id = 0,
            icon = binding.fragmentMainImgPlayer1Icon,
            name = binding.fragmentMainTxtPlayer1Name,
            stock = binding.fragmentMainTxtPlayer1Stock,
            smashMeter = binding.fragmentMainTxtPlayer1SmashMeter,
            game = binding.fragmentMainTxtPlayer1Game
        ),
        PlayerCard(
            id = 1,
            icon = binding.fragmentMainImgPlayer2Icon,
            name = binding.fragmentMainTxtPlayer2Name,
            stock = binding.fragmentMainTxtPlayer2Stock,
            smashMeter = binding.fragmentMainTxtPlayer2SmashMeter,
            game = binding.fragmentMainTxtPlayer2Game
        ),
        PlayerCard(
            id = 2,
            icon = binding.fragmentMainImgPlayer3Icon,
            name = binding.fragmentMainTxtPlayer3Name,
            stock = binding.fragmentMainTxtPlayer3Stock,
            smashMeter = binding.fragmentMainTxtPlayer3SmashMeter,
            game = binding.fragmentMainTxtPlayer3Game
        ),
        PlayerCard(
            id = 3,
            icon = binding.fragmentMainImgPlayer4Icon,
            name = binding.fragmentMainTxtPlayer4Name,
            stock = binding.fragmentMainTxtPlayer4Stock,
            smashMeter = binding.fragmentMainTxtPlayer4SmashMeter,
            game = binding.fragmentMainTxtPlayer4Game
        ),
    )
}