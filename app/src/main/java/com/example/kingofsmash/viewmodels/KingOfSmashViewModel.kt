package com.example.kingofsmash.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.kingofsmash.enums.*
import com.example.kingofsmash.models.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.cos

class KingOfSmashViewModel(character: Character) : ViewModel() {
    private val state = MutableStateFlow(
        KingOfSmash(
            Character.values().map { Player(if (character == it) PlayerType.PLAYER else PlayerType.BOT, it) }.shuffled(), 0, null
        )
    )


    val stateFlow = state.asStateFlow()

    fun getPlayers(): List<Player> = state.value.players
    fun getCurrentPlayer(): Player = state.value.players[state.value.currentPlayerIdx]
    fun throwDices(dices: List<Dice>) {
        state.value = state.value.copy(
            currentAction = Action.EXECUTE_DICES, dices = dices
        )
    }

    fun computeDiceAnimations(): EffectAnimations {
        var ones = 0
        var twos = 0
        var threes = 0
        var smash = 0
        var smashMeter = 0
        var stock = 0
        state.value.dices.forEach { dice ->
            when (dice) {
                Dice.ONE -> ones++
                Dice.TWO -> twos++
                Dice.THREE -> threes++
                Dice.SMASH -> smash++
                Dice.SMASH_METER -> smashMeter++
                Dice.STOCK -> stock++
            }
        }
        val game = (if (ones > 2) ones - 2 else 0) + (if (twos > 2) twos - 1 else 0) + (if (threes > 2) threes else 0)
        val currentPlayer = getCurrentPlayer()
        val playerInDF = state.value.playerInDF

        val stockAnim = mutableListOf<EffectAnim>()
        val stockAnimUpperBound = (currentPlayer.stock + stock).coerceAtMost(currentPlayer.maxStock)
        if (stock > 0 && stockAnimUpperBound > currentPlayer.stock)
            stockAnim.add(EffectAnim(currentPlayer, (currentPlayer.stock + 1..stockAnimUpperBound).toList(), stock))
        val smashMeterAnim = mutableListOf<EffectAnim>()
        if (smashMeter > 0)
            smashMeterAnim.add(EffectAnim(currentPlayer, (currentPlayer.smashMeter + 1..currentPlayer.smashMeter + smashMeter).toList(), smashMeter))
        val gameAnim = mutableListOf<EffectAnim>()
        if (game > 0)
            gameAnim.add(EffectAnim(currentPlayer, (currentPlayer.game + 1..currentPlayer.game + game).toList(), game))

        val smashAnim = mutableListOf<EffectAnim>()
        if (smash > 0 && playerInDF != null) {
            if (currentPlayer == playerInDF) {
                for (player in state.value.players) {
                    if (player != currentPlayer && player.isAlive) {
                        smashAnim.add(EffectAnim(player, (player.stock - 1 downTo  (player.stock - smash).coerceAtLeast(0)).toList(), -smash))
                    }
                }
            } else {
                smashAnim.add(EffectAnim(playerInDF, (playerInDF.stock - 1 downTo  (playerInDF.stock - smash).coerceAtLeast(0)).toList(), -smash))
            }
        }

        return EffectAnimations(stockAnim, smashMeterAnim, gameAnim, smashAnim, state.value.dices)
    }

    fun executeDices(): Boolean {
        var ones = 0
        var twos = 0
        var threes = 0
        var smash = 0
        var smashMeter = 0
        var stock = 0
        state.value.dices.forEach { dice ->
            when (dice) {
                Dice.ONE -> ones++
                Dice.TWO -> twos++
                Dice.THREE -> threes++
                Dice.SMASH -> smash++
                Dice.SMASH_METER -> smashMeter++
                Dice.STOCK -> stock++
            }
        }

        val game = (if (ones > 2) ones - 2 else 0) + (if (twos > 2) twos - 1 else 0) + (if (threes > 2) threes else 0)

        var isPlayerAttackedAndInDF = false
        val currentPlayer = getCurrentPlayer()
        Log.d(
            "ExecuteDices",
            "player ${currentPlayer.character} plays smash: $smash, game: $game, smashMeter: $smashMeter, stock: $stock"
        )
        currentPlayer.play(stock, smashMeter, game)
        if (state.value.playerInDF == currentPlayer) {
            state.value.players.forEach { player ->
                if (player != currentPlayer) {
                    Log.d("ExecuteDices", "player ${player.character} take $smash dmg from ${currentPlayer.character}")
                    state.value.rank = player.damaged(smash, state.value.rank, currentPlayer)
                }
            }
        } else {
            Log.d("ExecuteDices", "DF Player ${state.value.playerInDF?.character} take $smash dmg from ${currentPlayer.character}")
            val res = state.value.playerInDF?.damaged(smash, state.value.rank, currentPlayer)
            if (res != null)
                state.value.rank = res
            if (state.value.playerInDF != null) {
                if (!state.value.playerInDF!!.isAlive) {
                    Log.d("ExecuteDices", "player ${state.value.playerInDF?.character} is dead")
                    state.value = state.value.copy(playerInDF = null)
                } else if (state.value.playerInDF!!.type == PlayerType.PLAYER) {
                    Log.d("ExecuteDices", "ask player in DF ${state.value.playerInDF?.character}")
                    isPlayerAttackedAndInDF = true
                }
            }
        }
        return isPlayerAttackedAndInDF
    }

    fun leaveDFAndCheck() {
        Log.d("LeaveDF", "player leaving DF")
        state.value = state.value.copy(playerInDF = null, currentAction = Action.CHECK_DF)
    }

    fun setDFAttacked() {
        state.value = state.value.copy(currentAction = Action.DF_ATTACKED)
    }

    fun setCheckDF() {
        state.value = state.value.copy(currentAction = Action.CHECK_DF)
    }
    fun setCardSelection(){
        state.value = state.value.copy(currentAction = Action.CARD_SELECTION)
    }
    fun setExecuteCard(){
        state.value = state.value.copy(currentAction = Action.EXECUTE_CARDS)
    }

    fun setCheckGameOverAfterCards(){
        state.value = state.value.copy(currentAction = Action.CHECK_GAME_OVER_AFTER_CARDS)
    }

    fun checkDF(): Boolean {
        Log.d("CheckDF", "Checking DF")
        val currentPlayer = getCurrentPlayer()
        var isPlayerInDF = false
        if (state.value.playerInDF == null) {
            Log.d("CheckDF", "player ${currentPlayer.character} is now in DF")
            state.value = state.value.copy(playerInDF = currentPlayer)
            isPlayerInDF = true
        }
        return isPlayerInDF
    }

    fun setCheckGameOver() {
        state.value = state.value.copy(currentAction = Action.CHECK_GAME_OVER)
    }

    fun waitEndTurn() {
        state.value = state.value.copy(
            currentAction = Action.WAIT_END_TURN
        )
    }

    fun endTurn() {
        state.value = state.value.copy(
            currentPlayerIdx = (state.value.currentPlayerIdx + 1) % 4, currentAction = Action.THROW_DICES
        )
    }

    fun getCurrentAction(): Action {
        return state.value.currentAction
    }

    fun getWinner(): Player? {
        var winner: Player? = null
        var alivePlayer = 0
        for (player in state.value.players) {
            if (player.game >= NB_GAME_TO_WIN) {
                return player
            }
            if (player.isAlive) {
                alivePlayer++
                winner = player
            }
        }
        return if (alivePlayer == 1) winner else null
    }

    fun getSelectedCard() : Card? {
        return state.value.selectedCard
    }
    fun getCards() : MutableList<Card>{
        return state.value.cards
    }
    fun getCardsInDeck(): MutableList<Card>{
        return state.value.cardsInDeck
    }
    fun setCards(allCards : MutableList<Card>){
        state.value = state.value.copy(cards = allCards)
    }
    fun setCardsInDeck(cards : MutableList<Card>){
        state.value = state.value.copy(cardsInDeck = cards)
    }

    fun appendCards(cards: List<Card>){
        state.value.cards.addAll(cards)
    }

    private fun cardRandomKill(cardType : CardType, player : Player){
        var currentPlayer = getCurrentPlayer()
        if(cardType == CardType.RANDOM_KILL_ONE){
                state.value.rank = player.damaged(100, state.value.rank, currentPlayer)
        }
        else {
            state.value.rank = player.damaged(100, state.value.rank, currentPlayer)
        }

        if(state.value.playerInDF?.isAlive == false){
            state.value = state.value.copy(playerInDF = null)
            Log.d("KOSVM", "player in DF is null")
        }
    }

    private fun cardRandomDamage(cardType: CardType, player: Player){
        var dmg : Int = 0
        dmg = when(cardType){
            CardType.DAMAGE_RANDOM_ONE-> 1
            CardType.DAMAGE_RANDOM_TWO -> 2
            else-> 3
        }
        state.value.rank = player.damaged(dmg, state.value.rank, getCurrentPlayer())
        if(state.value.playerInDF?.isAlive == false)
            state.value = state.value.copy(playerInDF = null)
    }

    fun computeCardAnimation(cardType : CardType, randomPlayer: Player = getCurrentPlayer()) : EffectAnimations{
        var currentPlayer = getCurrentPlayer()

        var dices : List<Dice> = listOf()
        val stockAnim : MutableList<EffectAnim> = mutableListOf()
        val smashMeterAnim : MutableList<EffectAnim> = mutableListOf()
        val gameAnim : MutableList<EffectAnim> = mutableListOf()
        val smashAnim : MutableList<EffectAnim> = mutableListOf()

        var cost = 0;

        when (cardType){
            CardType.HEAL_ONE -> {
                cost = 1
                val stockAnimUpperBound = (currentPlayer.stock + 1).coerceAtMost(currentPlayer.maxStock)
                if (stockAnimUpperBound > currentPlayer.stock)
                    stockAnim.add(EffectAnim(currentPlayer, (currentPlayer.stock + 1..stockAnimUpperBound).toList(), 1))
            }
            CardType.HEAl_TWO -> {
                cost = 2
                val stockAnimUpperBound = (currentPlayer.stock + 2).coerceAtMost(currentPlayer.maxStock)
                if (stockAnimUpperBound > currentPlayer.stock)
                    stockAnim.add(EffectAnim(currentPlayer, (currentPlayer.stock + 1..stockAnimUpperBound).toList(), 2))
            }
            CardType.GAME_UP_ONE -> {
                cost = 3
                gameAnim.add(EffectAnim(currentPlayer, (currentPlayer.game..currentPlayer.game + 1).toList(), 1))
            }
            CardType.GAME_UP_TWO -> {
                cost = 6
                gameAnim.add(EffectAnim(currentPlayer, (currentPlayer.game..currentPlayer.game+2).toList(), 2))
            }
            CardType.RANDOM_KILL_ONE ->{
                cost = 6
                stockAnim.add(EffectAnim(randomPlayer, (randomPlayer.stock downTo 0).toList(), -1))
            }
            CardType.RANDOM_KILL_TWO -> {
                cost = 9
                stockAnim.add(EffectAnim(randomPlayer, (randomPlayer.stock downTo 0).toList(), -1))
            }
            CardType.DAMAGE_RANDOM_ONE -> {
                cost = 1
                stockAnim.add(EffectAnim(randomPlayer,(randomPlayer.stock downTo  (randomPlayer.stock - 1).coerceAtLeast(0)).toList(), -1))
            }
            CardType.DAMAGE_RANDOM_TWO -> {
                cost = 2
                stockAnim.add(EffectAnim(randomPlayer, (randomPlayer.stock downTo  (randomPlayer.stock - 2).coerceAtLeast(0)).toList(), -2))
            }
            CardType.DAMAGE_RANDOM_THREE -> {
                cost = 3
                stockAnim.add(EffectAnim(randomPlayer, (randomPlayer.stock downTo  (randomPlayer.stock - 3).coerceAtLeast(0)).toList(), -3))
            }
        }
        smashMeterAnim.add(EffectAnim(currentPlayer, (currentPlayer.smashMeter downTo (currentPlayer.smashMeter - cost).coerceAtLeast(0)).toList(), -cost))
        return EffectAnimations(stockAnim, smashMeterAnim, gameAnim, smashAnim, dices)
    }

    fun executeCardAction(cardType : CardType, randomPlayer : Player = getCurrentPlayer()){
        var currentPlayer = getCurrentPlayer()
        when (cardType){
            CardType.HEAL_ONE -> currentPlayer.play(1, 0, 0)
            CardType.HEAl_TWO -> currentPlayer.play( 2, 0, 0)
            CardType.GAME_UP_ONE -> currentPlayer.play(0, 0,  1)
            CardType.GAME_UP_TWO -> currentPlayer.play(0, 0, 2)
            CardType.RANDOM_KILL_ONE -> cardRandomKill(cardType, randomPlayer)
            CardType.RANDOM_KILL_TWO -> cardRandomKill(cardType, randomPlayer)
            CardType.DAMAGE_RANDOM_ONE -> cardRandomDamage(cardType, randomPlayer)
            CardType.DAMAGE_RANDOM_TWO -> cardRandomDamage(cardType, randomPlayer)
            CardType.DAMAGE_RANDOM_THREE -> cardRandomDamage(cardType, randomPlayer)
        }
    }

    fun getKillBoolean(cardType: CardType) : Boolean {
        val randomValue = (0..100).random()
        return cardType == CardType.RANDOM_KILL_ONE && randomValue <= 25 || cardType == CardType.RANDOM_KILL_TWO && randomValue <= 55
    }

    fun getRandomPlayer() : Player{
        var currentPlayer = getCurrentPlayer()
        var playersWithoutCurrent = getPlayers().filter { it != currentPlayer && it.isAlive }
        return playersWithoutCurrent.random()
    }

    private fun replenishDeck(card : Card){
        val cardInDeckIndex = getCardsInDeck().indexOfFirst { cardInDeck -> cardInDeck.type == card.type }
        val cardsLen = getCards().count() - 1
        val randomCardIndex = (0..cardsLen).random()

        val cards = getCards()
        val deck = getCardsInDeck()

        deck[cardInDeckIndex] = cards[randomCardIndex]
        cards.add(card)
        cards.removeAt(randomCardIndex)
    }

    fun useCard(card : Card){
        val currentPlayer = getCurrentPlayer()
        currentPlayer.cards.add(card)
        currentPlayer.play(0, -card.cost,0)
        state.value = state.value.copy(selectedCard = card)
        replenishDeck(card)
    }
    fun cardReroll(){
        val currentPlayer = getCurrentPlayer()
        currentPlayer.play(0,-2,0)
    }

    companion object {
        private const val NB_GAME_TO_WIN = 20
    }
}