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
import com.example.kingofsmash.models.GameOverPlayerCard
import com.example.kingofsmash.models.Player

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
                player.stock = 0
        }
    }

    private fun sortArrayInRankDescOrder(players: Array<Player>) {
        // sort by rank => ie: by death order
        players.sortBy { it.rank }

        // players still alive => sort by games
        val comparator = Comparator { p1: Player, p2: Player ->
            return@Comparator when {
                p1.rank != p2.rank -> 0
                else -> p1.game - p2.game
            }
        }
        players.sortWith(comparator)
    }

    private fun setGameOverPlayerCardValues(
        player: Player,
        gameOverPlayerCard: GameOverPlayerCard
    ) {
        gameOverPlayerCard.backgroundImage.setImageResource(player.character.gameOverBackground)
        gameOverPlayerCard.name.text = player.character.character
        gameOverPlayerCard.stock.text = player.stock.toString()
        if (player.stock == 0)
            gameOverPlayerCard.stock.setTextColor(Color.rgb(255, 10, 10))
        gameOverPlayerCard.smashMeter.text = player.smashMeter.toString()
        gameOverPlayerCard.game.text = player.game.toString()

        // set kills
        for (i in gameOverPlayerCard.kills.indices) {
            gameOverPlayerCard.kills[i].setImageResource(0)
        }

        for (i in player.kills.indices) {
            gameOverPlayerCard.kills[i].setImageResource(player.kills[i].icon)
        }
    }

    private fun getGameOverPlayerCards() = listOf(
        GameOverPlayerCard(
            id = 0,
            backgroundImage = binding.gameoverImageP1,
            name = binding.gameoverNameP1,
            stock = binding.gameoverTextLifeP1,
            smashMeter = binding.gameoverTextSmP1,
            game = binding.gameoverTextGameP1,
            kills = listOf(
                binding.gameoverP1Kill1,
                binding.gameoverP1Kill2,
                binding.gameoverP1Kill3
            )
        ),
        GameOverPlayerCard(
            id = 1,
            backgroundImage = binding.gameoverImageP2,
            name = binding.gameoverNameP2,
            stock = binding.gameoverTextLifeP2,
            smashMeter = binding.gameoverTextSmP2,
            game = binding.gameoverTextGameP2,
            kills = listOf(
                binding.gameoverP2Kill1,
                binding.gameoverP2Kill2,
                binding.gameoverP2Kill3
            )
        ),
        GameOverPlayerCard(
            id = 2,
            backgroundImage = binding.gameoverImageP3,
            name = binding.gameoverNameP3,
            stock = binding.gameoverTextLifeP3,
            smashMeter = binding.gameoverTextSmP3,
            game = binding.gameoverTextGameP3,
            kills = listOf(
                binding.gameoverP3Kill1,
                binding.gameoverP3Kill2,
                binding.gameoverP3Kill3
            )
        ),
        GameOverPlayerCard(
            id = 3,
            backgroundImage = binding.gameoverImageP4,
            name = binding.gameoverNameP4,
            stock = binding.gameoverTextLifeP4,
            smashMeter = binding.gameoverTextSmP4,
            game = binding.gameoverTextGameP4,
            kills = listOf(
                binding.gameoverP4Kill1,
                binding.gameoverP4Kill2,
                binding.gameoverP4Kill3
            )
        ),
    )
}