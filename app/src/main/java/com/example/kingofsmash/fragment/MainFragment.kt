package com.example.kingofsmash.fragment

import android.graphics.Paint
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.kingofsmash.R
import com.example.kingofsmash.databinding.FragmentMainBinding
import com.example.kingofsmash.enums.*
import com.example.kingofsmash.models.Card
import com.example.kingofsmash.models.EffectAnimations
import com.example.kingofsmash.models.Player
import com.example.kingofsmash.models.PlayerCard
import com.example.kingofsmash.utils.initAllCards
import com.example.kingofsmash.utils.initDieButton
import com.example.kingofsmash.viewmodels.KingOfSmashViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.random.Random

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding
    private lateinit var viewModel: KingOfSmashViewModel
    private val args: MainFragmentArgs by navArgs()
    private lateinit var playerCards: List<PlayerCard>
    private lateinit var dice: List<Button>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        viewModel = KingOfSmashViewModel(args.character)

        playerCards = getPlayerCards()
        initPlayerCards()
        dice = getDice()

        initCards()
        setOnClickPlayerCard()

        lifecycleScope.launch {
            viewModel.stateFlow.collect {
                val currentPlayer = viewModel.getCurrentPlayer()

                when (it.currentAction) {
                    Action.THROW_DICES -> throwDices(currentPlayer)
                    Action.EXECUTE_DICES -> executeDices(currentPlayer)
                    Action.DF_ATTACKED -> dfAttacked(currentPlayer)
                    Action.CHECK_DF -> checkDF(currentPlayer)
                    Action.CHECK_GAME_OVER -> checkWinner(it.players, true)
                    Action.CARD_SELECTION -> cardSelection()
                    Action.EXECUTE_CARDS -> executeCards()
                    Action.CHECK_GAME_OVER_AFTER_CARDS -> checkWinner(it.players, false)
                    Action.WAIT_END_TURN -> waitEndTurn(currentPlayer)
                }

                playerCards.forEach { playerCard ->
                    val player = it.players[playerCard.id]
                    playerCard.game.text = player.game.toString()
                    playerCard.game.setTextColor(resources.getColor(R.color.white))
                    playerCard.stock.text = player.stock.toString()
                    playerCard.stock.setTextColor(resources.getColor(R.color.white))
                    if (player.stock == 0) {
                        playerCard.background.setColor(resources.getColor(R.color.red_transparent))
                        playerCard.name.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                    }
                    playerCard.smashMeter.text = player.smashMeter.toString()
                    playerCard.smashMeter.setTextColor(resources.getColor(R.color.white))

                    // TODO: this is done every time,
                    playerCard.icon.setImageResource(player.character.icon)
                    playerCard.name.text = player.character.character
                }

                if (it.playerInDF == null) {
                    binding.fragmentMainImgPlayerDf.visibility = View.INVISIBLE
                } else {
                    binding.fragmentMainImgPlayerDf.visibility = View.VISIBLE
                    binding.fragmentMainImgPlayerDf.setImageResource(it.playerInDF!!.character.df)
                    setCrown(it.playerInDF!!)
                }
            }
        }
        // Inflate the layout for this fragment
        return binding.root
    }

    private fun throwDices(currentPlayer: Player) {
        if (!currentPlayer.isAlive) {
            Log.d("MainFragment", "${currentPlayer.character} is dead, end turn")
            return viewModel.endTurn()
        }
        setPlayerCardUp(currentPlayer)
        Log.d("MainFragment", "${currentPlayer.character} THROW DICESU")
        if (currentPlayer.type == PlayerType.PLAYER) {
            val fragment = DiceFragment(onSubmit = { dices ->
                viewModel.throwDices(dices)
            })
            openFragment(fragment)
        } else {
            val random: Random = Random(Date().time)
            val dices = (1..6).map { Dice.getRandom(random) }
            viewModel.throwDices(dices)
        }
    }

    private suspend fun animateCardChange(
        player: Player,
        variations: List<Int>,
        variation: Int,
        type: PlayerCardAnimType
    ) {
        val playerCard = getPlayerCard(player)
        val color = if (variation > 0) R.color.animation_positive else R.color.animation_negative
        val symbol = if (variation > 0) '+' else ' '
        val textView: TextView
        var icon = 0

        when (type) {
            PlayerCardAnimType.STOCK -> {
                textView = playerCard.stock
                icon = R.drawable.ant_design_heart_filled
            }

            PlayerCardAnimType.SMASHMETER -> {
                textView = playerCard.smashMeter
                icon = R.drawable.ant_design_thunderbolt_filled
            }

            PlayerCardAnimType.GAME -> {
                textView = playerCard.game
                icon = R.drawable.smash
            }

            PlayerCardAnimType.SMASH -> {
                textView = playerCard.stock
                icon = R.drawable.ant_design_heart_filled
            }
        }
        playerCard.actionText.setTextColor(resources.getColor(color))
        playerCard.actionText.text = symbol + variation.toString()
        playerCard.actionText.visibility = View.VISIBLE
        playerCard.actionIcon.setImageResource(icon)
        playerCard.actionIcon.visibility = View.VISIBLE

        textView.setTextColor(resources.getColor(color))
        for (value in variations) {
            textView.text = value.toString()
            delay(500)
        }

        playerCard.actionIcon.visibility = View.INVISIBLE
        playerCard.actionText.visibility = View.INVISIBLE
    }

    private fun showDice(playedDice: List<Dice>) {
        // init dice
        for (i in dice.indices) {
            initDieButton(dice[i], playedDice[i])
        }

        // show dice
        for (die in dice) {
            die.visibility = View.VISIBLE
        }
    }

    private fun hideDice() {
        for (die in dice) {
            die.visibility = View.INVISIBLE
        }
    }

    private suspend fun animateEffects(effects: EffectAnimations) {
        showDice(effects.dice)
        delay(1500)

        // animate stock
        effects.stockAnim.forEach() { (player, variations, variation) ->
            animateCardChange(player, variations, variation, PlayerCardAnimType.STOCK)
        }

        // animate smash meter
        effects.smashMeterAnim.forEach() { (player, variations, variation) ->
            animateCardChange(player, variations, variation, PlayerCardAnimType.SMASHMETER)
        }

        // animate game
        effects.gameAnim.forEach() { (player, variations, variation) ->
            animateCardChange(player, variations, variation, PlayerCardAnimType.GAME)
        }

        // animate smash
        effects.smashAnim.forEach() { (player, variations, variation) ->
            animateCardChange(player, variations, variation, PlayerCardAnimType.SMASH)
        }

        hideDice()
    }

    private suspend fun animateCardsEffects(effects: EffectAnimations) {
        //show card
        showSelectedCard()
        delay(1500)

        // animate stock
        effects.stockAnim.forEach() { (player, variations, variation) ->
            animateCardChange(player, variations, variation, PlayerCardAnimType.STOCK)
        }

        // animate smash meter
        effects.smashMeterAnim.forEach() { (player, variations, variation) ->
            animateCardChange(player, variations, variation, PlayerCardAnimType.SMASHMETER)
        }

        // animate game
        effects.gameAnim.forEach() { (player, variations, variation) ->
            animateCardChange(player, variations, variation, PlayerCardAnimType.GAME)
        }

        // animate smash
        effects.smashAnim.forEach() { (player, variations, variation) ->
            animateCardChange(player, variations, variation, PlayerCardAnimType.SMASH)
        }
        //hide card
        hideSelectedCard()
    }

    private suspend fun executeDices(currentPlayer: Player) {
        Log.d("MainFragment", "${currentPlayer.character} EXECUTE DICESU")
        val diceAnimations = viewModel.computeDiceAnimations()
        if (currentPlayer.type != PlayerType.PLAYER)
            delay(1000)
        animateEffects(diceAnimations)
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
            Log.d("MainFragment", "You are now in DF")
            val fragment = PlayerInDFFragment()
            openFragment(fragment)
        }
        viewModel.setCheckGameOver()
    }

    private fun checkWinner(players: List<Player>, beforeCards: Boolean) {
        Log.d("MainFragment", "CHECK WINNERU")
        viewModel.getWinner()?.let {
            Log.d("MainFragment", "Winner is ${it.character.character}")
            val fragment = GameFragment(onClick = {
                findNavController().navigate(
                    MainFragmentDirections.actionFragmentMainToGameOverFragment(
                        players.toTypedArray()
                    )
                )
            })
            openFragment(fragment)
        } ?: run {
            if (!beforeCards) {
                val isPlayerInDF = viewModel.checkDF()
                if (isPlayerInDF && viewModel.getCurrentPlayer().type == PlayerType.PLAYER) {
                    Log.d("MainFragment", "You are now in DF")
                    val fragment = PlayerInDFFragment()
                    openFragment(fragment)
                }
                viewModel.waitEndTurn()
            } else {
                viewModel.setCardSelection()
            }
        }
    }

    private fun getNewCardsInDeck(): List<Card> {
        val allCards = viewModel.getCards()
        val card1 = allCards.random()
        allCards.remove(card1)
        val card2 = allCards.random()
        allCards.remove(card2)
        val card3 = allCards.random()
        allCards.remove(card3)
        //if deck is empty then take 3 from cards and add to deck
        if (viewModel.getCardsInDeck().isNotEmpty())
            viewModel.appendCards(viewModel.getCardsInDeck())
        viewModel.setCardsInDeck(mutableListOf(card1, card2, card3))
        //if deck is not empty then take 3 from cards and add to deck and add deck card to cards
        return viewModel.getCardsInDeck()
    }

    private fun getCardsInDeck(): List<Card> {
        if (viewModel.getCardsInDeck().isEmpty()) {
            return getNewCardsInDeck()
        }
        return viewModel.getCardsInDeck()
    }

    private fun onConfirmButtonCardSelection(card: Card?): Boolean {
        //add card to current player + play the animation / effect
        if (card == null || viewModel.getCurrentPlayer().smashMeter < card.cost) {
            viewModel.waitEndTurn()
            return false
        }
        viewModel.useCard(card)
        viewModel.setExecuteCard()
        return true
    }

    private fun beforeReroll(cost: Int): Boolean {
        return cost <= viewModel.getCurrentPlayer().smashMeter
    }

    private fun onRerollButtonCardSelection(): List<Card> {
        viewModel.cardReroll()
        return getNewCardsInDeck()
    }

    private fun cardSelection() {
        Log.d("MainFragment", "CARDSU SELECTIONU")
        if (viewModel.getCurrentPlayer().type == PlayerType.PLAYER) {
            val fragment = CardSelectionFragment(onReroll = { onRerollButtonCardSelection() },
                onConfirm = { card -> onConfirmButtonCardSelection(card) },
                getCardsInDeck = { getCardsInDeck() },
                beforeReroll = { cost -> beforeReroll(cost) })
            openFragment(fragment)
            viewModel.waitEndTurn()
        } else {
            //if bot
            val deck = viewModel.getCardsInDeck()
            val cardRandom = deck.random()
            if (cardRandom.cost <= viewModel.getCurrentPlayer().smashMeter) {
                Log.d(
                    "MainFragment",
                    "current player : ${viewModel.getCurrentPlayer().character}, smash meter : ${viewModel.getCurrentPlayer().smashMeter}"
                )
                viewModel.useCard(cardRandom)
                viewModel.setExecuteCard()
            } else {
                viewModel.waitEndTurn()
            }
        }
    }

    private suspend fun executeCards() {
        Log.d("MainFragment", "EXECUTE CARDSU")
        val card = viewModel.getSelectedCard()
        //if type random kill
        if (card?.type == CardType.RANDOM_KILL_ONE || card?.type == CardType.RANDOM_KILL_TWO) {
            val killed = viewModel.getKillBoolean(card.type)
            if (killed) {
                val targetPlayer = viewModel.getRandomPlayer()
                Log.d("Main Fragment", "target is ${targetPlayer.character}")
                //computeAnim
                val anims = viewModel.computeCardAnimation(
                    cardType = card.type,
                    randomPlayer = targetPlayer
                )
                //PlayAnim
                animateCardsEffects(anims)
                //Exec
                viewModel.executeCardAction(cardType = card.type, randomPlayer = targetPlayer)
            }
        } else if (card?.type != CardType.RANDOM_KILL_TWO && card?.type != CardType.RANDOM_KILL_ONE) {
            val targetPlayer = viewModel.getRandomPlayer()
            //compute
            val anims = viewModel.computeCardAnimation(cardType = card!!.type, targetPlayer)
            //play anim
            animateCardsEffects(anims)
            //exec
            viewModel.executeCardAction(cardType = card.type, targetPlayer)
        }
        viewModel.setCheckGameOverAfterCards()
    }

    private fun waitEndTurn(currentPlayer: Player) {
        Log.d("MainFragment", "WAIT END TURNU")
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

    private fun setCrown(playerInDf: Player) {
        for (card in this.playerCards) {
            card.crown.visibility =
                if (card.name.text == playerInDf.character.character) View.VISIBLE else View.INVISIBLE
        }
    }

    private fun setPlayerCardUp(currentPlayer: Player) {
        for (card in this.playerCards) {
            val params = card.view.layoutParams as ConstraintLayout.LayoutParams
            params.topToBottom =
                if (card.name.text == currentPlayer.character.character) binding.fragmentMainGuidelineDf.id else -1
            card.view.layoutParams = params
        }
    }

    private fun initPlayerCards() {
        val players = viewModel.getPlayers()
        for (card in this.playerCards) {
            val player = players[card.id]
            card.name.text = player.character.character
            card.icon.setImageResource(player.character.icon)
            card.stock.text = player.stock.toString()
            card.smashMeter.text = player.smashMeter.toString()
            card.game.text = player.game.toString()
            card.background.setColor(resources.getColor(if (player.type == PlayerType.BOT) R.color.grey_transparent else R.color.green_transparent))
            card.crown.visibility = View.INVISIBLE
            card.actionText.visibility = View.INVISIBLE
            card.actionIcon.visibility = View.INVISIBLE
        }
    }

    private fun getPlayerCard(player: Player): PlayerCard {
        for (card in playerCards) {
            if (card.name.text == player.character.character)
                return card
        }
        return playerCards[0]
    }

    private fun getDice() = listOf(
        binding.fragmentMainDice1,
        binding.fragmentMainDice2,
        binding.fragmentMainDice3,
        binding.fragmentMainDice4,
        binding.fragmentMainDice5,
        binding.fragmentMainDice6
    )

    private fun getPlayerCards() = listOf(
        PlayerCard(
            id = 0,
            icon = binding.fragmentMainImgPlayer1Icon,
            name = binding.fragmentMainTxtPlayer1Name,
            stock = binding.fragmentMainTxtPlayer1Stock,
            smashMeter = binding.fragmentMainTxtPlayer1SmashMeter,
            game = binding.fragmentMainTxtPlayer1Game,
            background = binding.fragmentMainViewPlayer1Card.background as GradientDrawable,
            crown = binding.fragmentMainPlayer1Crown,
            view = binding.fragmentMainViewPlayer1Card,
            actionText = binding.fragmentMainDiceActionTextP1,
            actionIcon = binding.fragmentMainDiceActionIconP1
        ),
        PlayerCard(
            id = 1,
            icon = binding.fragmentMainImgPlayer2Icon,
            name = binding.fragmentMainTxtPlayer2Name,
            stock = binding.fragmentMainTxtPlayer2Stock,
            smashMeter = binding.fragmentMainTxtPlayer2SmashMeter,
            game = binding.fragmentMainTxtPlayer2Game,
            background = binding.fragmentMainViewPlayer2Card.background as GradientDrawable,
            crown = binding.fragmentMainPlayer2Crown,
            view = binding.fragmentMainViewPlayer2Card,
            actionText = binding.fragmentMainDiceActionTextP2,
            actionIcon = binding.fragmentMainDiceActionIconP2
        ),
        PlayerCard(
            id = 2,
            icon = binding.fragmentMainImgPlayer3Icon,
            name = binding.fragmentMainTxtPlayer3Name,
            stock = binding.fragmentMainTxtPlayer3Stock,
            smashMeter = binding.fragmentMainTxtPlayer3SmashMeter,
            game = binding.fragmentMainTxtPlayer3Game,
            background = binding.fragmentMainViewPlayer3Card.background as GradientDrawable,
            crown = binding.fragmentMainPlayer3Crown,
            view = binding.fragmentMainViewPlayer3Card,
            actionText = binding.fragmentMainDiceActionTextP3,
            actionIcon = binding.fragmentMainDiceActionIconP3
        ),
        PlayerCard(
            id = 3,
            icon = binding.fragmentMainImgPlayer4Icon,
            name = binding.fragmentMainTxtPlayer4Name,
            stock = binding.fragmentMainTxtPlayer4Stock,
            smashMeter = binding.fragmentMainTxtPlayer4SmashMeter,
            game = binding.fragmentMainTxtPlayer4Game,
            background = binding.fragmentMainViewPlayer4Card.background as GradientDrawable,
            crown = binding.fragmentMainPlayer4Crown,
            view = binding.fragmentMainViewPlayer4Card,
            actionText = binding.fragmentMainDiceActionTextP4,
            actionIcon = binding.fragmentMainDiceActionIconP4
        ),
    )

    private fun showSelectedCard() {
        val card = binding.fragmentMainSelectedCardView
        val cardImage = binding.fragmentMainSelectedCardImageview
        val cardDesc = binding.fragmentMainSelectedCardDescription

        card.visibility = View.VISIBLE
        cardImage.visibility = View.VISIBLE
        cardDesc.visibility = View.VISIBLE
        cardDesc.setText(viewModel.getSelectedCard()?.description)
    }

    private fun hideSelectedCard() {
        val card = binding.fragmentMainSelectedCardView
        val cardImage = binding.fragmentMainSelectedCardImageview
        val cardDesc = binding.fragmentMainSelectedCardDescription

        card.visibility = View.INVISIBLE
        cardImage.visibility = View.INVISIBLE
        cardDesc.visibility = View.INVISIBLE
    }

    private fun initCards() {
        val cards = binding.fragmentMainCardsDeck
        hideSelectedCard()
        if (viewModel.getCards().isEmpty()) {
            val allCards = initAllCards()
            viewModel.setCards(allCards)
            getNewCardsInDeck()
        }
        cards.setOnClickListener {
            val fragment = CardSelectionFragment(onReroll = { onRerollButtonCardSelection() },
                onConfirm = { card -> onConfirmButtonCardSelection(card) },
                getCardsInDeck = { getCardsInDeck() },
                beforeReroll = { cost -> beforeReroll(cost) })
            if (viewModel.getCurrentAction() == Action.WAIT_END_TURN)
                openFragment(fragment)
        }
    }

    private fun setOnClickPlayerCard() {
        val players = viewModel.getPlayers()
        val card1 = binding.fragmentMainViewPlayer1Card
        card1.setOnClickListener {
            val fragment =
                PlayerDetailsFragment(cards = players[0].cards, character = players[0].character)
            openFragment(fragment)
        }
        val card2 = binding.fragmentMainViewPlayer2Card
        card2.setOnClickListener {
            val fragment =
                PlayerDetailsFragment(cards = players[1].cards, character = players[1].character)
            openFragment(fragment)
        }
        val card3 = binding.fragmentMainViewPlayer3Card
        card3.setOnClickListener {
            val fragment =
                PlayerDetailsFragment(cards = players[2].cards, character = players[2].character)
            openFragment(fragment)
        }
        val card4 = binding.fragmentMainViewPlayer4Card
        card4.setOnClickListener {
            val fragment =
                PlayerDetailsFragment(cards = players[3].cards, character = players[3].character)
            openFragment(fragment)
        }
    }
}