package com.example.kingofsmash.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.kingofsmash.R
import com.example.kingofsmash.databinding.FragmentGameOverBinding
import com.example.kingofsmash.enums.Character
import com.example.kingofsmash.models.GameOverPlayerCard
import com.example.kingofsmash.models.Player
import com.example.kingofsmash.models.PlayerCard
import com.example.kingofsmash.viewmodels.KingOfSmashViewModel

/**
 * A simple [Fragment] subclass.
 * Use the [GameOverFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GameOverFragment : Fragment() {
    private lateinit var binding: FragmentGameOverBinding
    private lateinit var gameOverPlayerCards: List<GameOverPlayerCard>
    private lateinit var players: Array<Player>
    private val args: GameOverFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGameOverBinding.inflate(inflater, container, false)
        binding.gameoverAllScreen.setOnClickListener {
            findNavController().navigate(R.id.action_gameOverFragment_to_characterSelectionFragment)
        }

        gameOverPlayerCards = getGameOverPlayerCards()
        players = args.players

        // correct health points -ie: if dead => stock = 0
        correctPlayerStocks(players)

        // sort players so that players[0] = winner and players[3] = last player
        sortArrayInRankDescOrder(players)

        // iterate on players array and set the values of gameOverPlayerCards
        for (i in players.indices) {
            setGameOverPlayerCardValues(players[i], gameOverPlayerCards[i])
        }

        return binding.root
    }

    private fun correctPlayerStocks(players: Array<Player>) {
        for (player in players) {
           if (!player.isAlive)
               player.stock = 0;
        }
    }

    private fun sortArrayInRankDescOrder(players: Array<Player>) {
        // sort by rank => ie: by death order
        for (i in players.indices) {
            for (j in i + 1 until players.size) {
                if (players[i].rank > players[j].rank) {
                    val temp = players[i]
                    players[i] = players[j]
                    players[j] = temp
                }
            }
        }

        // players still alive => sort by games
        for (i in players.indices) {
            for (j in i + 1 until players.size) {
                if (players[i].rank == 0 && players[j].rank == 0)
                    if (players[i].game < players[j].game) {
                        val temp = players[i]
                        players[i] = players[j]
                        players[j] = temp
                    }
            }
        }
    }

    private fun setGameOverPlayerCardValues(player: Player, gameOverPlayerCard: GameOverPlayerCard) {
        gameOverPlayerCard.backgroundImage.setImageResource(player.character.gameOverBackground)
        gameOverPlayerCard.name.text = player.character.character
        gameOverPlayerCard.stock.text = player.stock.toString()
        if (player.stock == 0)
            gameOverPlayerCard.stock.setTextColor(Color.rgb(255, 10, 10))
        gameOverPlayerCard.smashMeter.text = player.smashMeter.toString()
        gameOverPlayerCard.game.text = player.game.toString()
    }

    private fun getGameOverPlayerCards() = listOf(
        GameOverPlayerCard(
            id = 0,
            backgroundImage = binding.gameoverImageP1,
            name = binding.gameoverNameP1,
            stock = binding.gameoverTextLifeP1,
            smashMeter = binding.gameoverTextSmP1,
            game = binding.gameoverTextGameP1
        ),
        GameOverPlayerCard(
            id = 1,
            backgroundImage = binding.gameoverImageP2,
            name = binding.gameoverNameP2,
            stock = binding.gameoverTextLifeP2,
            smashMeter = binding.gameoverTextSmP2,
            game = binding.gameoverTextGameP2
        ),
        GameOverPlayerCard(
            id = 2,
            backgroundImage = binding.gameoverImageP3,
            name = binding.gameoverNameP3,
            stock = binding.gameoverTextLifeP3,
            smashMeter = binding.gameoverTextSmP3,
            game = binding.gameoverTextGameP3
        ),
        GameOverPlayerCard(
            id = 3,
            backgroundImage = binding.gameoverImageP4,
            name = binding.gameoverNameP4,
            stock = binding.gameoverTextLifeP4,
            smashMeter = binding.gameoverTextSmP4,
            game = binding.gameoverTextGameP4
        ),
    )
}