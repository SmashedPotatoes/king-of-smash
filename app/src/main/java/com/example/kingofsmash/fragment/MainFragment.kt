package com.example.kingofsmash.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.kingofsmash.R
import com.example.kingofsmash.databinding.FragmentMainBinding
import com.example.kingofsmash.enums.Action
import com.example.kingofsmash.enums.Dice
import com.example.kingofsmash.enums.PlayerType
import com.example.kingofsmash.models.Player
import com.example.kingofsmash.models.PlayerCard
import com.example.kingofsmash.viewmodels.KingOfSmashViewModel
import kotlinx.coroutines.launch

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding
    private lateinit var viewModel: KingOfSmashViewModel
    private val args: MainFragmentArgs by navArgs()
    private lateinit var playerCards: List<PlayerCard>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        viewModel = KingOfSmashViewModel(args.character)

        playerCards = getPlayerCards()
        lifecycleScope.launch {
            viewModel.stateFlow.collect {
                val currentPlayer = viewModel.getCurrentPlayer()
                when (it.currentAction) {
                    Action.THROW_DICES -> throwDices(currentPlayer)
                    Action.EXECUTE_DICES -> executeDices(currentPlayer)
                    Action.DF_ATTACKED -> dfAttacked(currentPlayer)
                    Action.CHECK_DF -> checkDF(currentPlayer)
                    Action.CHECK_GAME_OVER -> checkWinner()
                    Action.EXECUTE_CARDS -> executeCards()
                    Action.WAIT_END_TURN -> waitEndTurn(currentPlayer)
                }

                playerCards.forEach { playerCard ->
                    val player = it.players[playerCard.id]
                    playerCard.game.text = player.game.toString()
                    playerCard.stock.text = player.stock.toString()
                    playerCard.smashMeter.text = player.smashMeter.toString()

                    // TODO: this is done every time,
                    playerCard.icon.setImageResource(player.character.icon)
                    playerCard.name.text = player.character.character
                }

                if (it.playerInDF == null) {
                    binding.fragmentMainImgPlayerDf.visibility = View.INVISIBLE
                } else {
                    binding.fragmentMainImgPlayerDf.visibility = View.VISIBLE
                    binding.fragmentMainImgPlayerDf.setImageResource(it.playerInDF!!.character.df)
                }
            }
        }
        // Inflate the layout for this fragment
        return binding.root
    }

    private fun throwDices(currentPlayer: Player) {
        Log.d("MainFragment", "THROW DICESU")
        // TODO: Refacto into currentPlayer.throwDices(context)
        if (currentPlayer.type == PlayerType.PLAYER) {
            val fragment = DiceFragment(onSubmit = { dices ->
                viewModel.throwDices(dices)
            })
            openFragment(fragment)
        } else {
            val dices = listOf(Dice.ONE, Dice.ONE, Dice.THREE, Dice.SMASH, Dice.SMASH, Dice.SMASH)
            viewModel.throwDices(dices)
        }
    }

    private fun executeDices(currentPlayer: Player) {
        Log.d("MainFragment", "EXECUTE DICESU")
        // TODO: DISPLAY DICES AND EXECUTE
        val isPlayerAttackedAndInDF = viewModel.executeDices()
        if (isPlayerAttackedAndInDF) {
            viewModel.setDFAttacked()
        } else {
            viewModel.setCheckDF()
        }
    }

    private fun dfAttacked(currentPlayer: Player) {
        Log.d("MainFragment", "Player is in DF and is attacked")
        val fragment = DFAttackedFragment(onStay = { viewModel.setCheckDF() }, onLeave = {
            viewModel.leaveDFAndCheck()
        })
        openFragment(fragment)
    }

    private fun checkDF(currentPlayer: Player) {
        val isPlayerInDF = viewModel.checkDF()
        if (isPlayerInDF && currentPlayer.type == PlayerType.PLAYER) {
            // TODO: DISPLAY YOU ARE NOW IN DF
            Log.d("MainFragment", "You are now in DF")
        }
        viewModel.setCheckGameOver()
    }

    private fun checkWinner() {
        Log.d("MainFragment", "CHECK WINNERU")
        viewModel.getWinner()?.let {
            Log.d("MainFragment", "Winner is ${it.character.character}")
            findNavController().navigate(R.id.action_fragment_main_to_gameOverFragment)
        } ?: viewModel.waitEndTurn()
    }

    private fun executeCards() {
        Log.d("MainFragment", "EXECUTE CARDSU")
        viewModel.waitEndTurn()
    }

    private fun waitEndTurn(currentPlayer: Player) {
        Log.d("MainFragment", "WAIT END TURNU")
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

    private fun openFragment(fragment: Fragment) {
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.fragmentContainerView, fragment)
        fragmentTransaction.commit()
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