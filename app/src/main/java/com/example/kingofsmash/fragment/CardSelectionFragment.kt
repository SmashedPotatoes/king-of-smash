package com.example.kingofsmash.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.kingofsmash.R
import com.example.kingofsmash.databinding.FragmentCardSelectionBinding
import com.example.kingofsmash.models.Card
import kotlinx.coroutines.delay

/**
 * A simple [Fragment] subclass.
 * Use the [CardSelectionFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CardSelectionFragment(val onReroll : () -> List<Card>, val onConfirm : (card : Card?) -> Boolean, val getCardsInDeck: () -> List<Card>, val beforeReroll: (cost : Int) -> Boolean): Fragment() {

    private lateinit var binding: FragmentCardSelectionBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val colorSelected = Color.argb(190, 62, 191, 137)
        val colorUnselected = Color.argb(127, 13, 13, 13)

        val view = inflater.inflate(R.layout.fragment_card_selection, container, false)
        binding = FragmentCardSelectionBinding.bind(view)

        var selectedCard : Int = -1
        var cardsInDeck: MutableList<Card>
        cardsInDeck = getCardsInDeck() as MutableList<Card>

        val viewCard1 = binding.fragmentCardSelectionViewCard1;
        val viewCard2 = binding.fragmentCardSelectionViewCard2
        val viewCard3 = binding.fragmentCardSelectionViewCard3

        viewCard1.setBackgroundColor(colorUnselected)
        viewCard2.setBackgroundColor(colorUnselected)
        viewCard3.setBackgroundColor(colorUnselected)

        fun onSelectCard(cardIdx: Int){
            if(selectedCard == cardIdx){
                selectedCard = -1
            }
            else{
                selectedCard = cardIdx
            }
            viewCard1.setBackgroundColor(if (selectedCard == 0) colorSelected else colorUnselected)
            viewCard2.setBackgroundColor(if (selectedCard == 1) colorSelected else colorUnselected)
            viewCard3.setBackgroundColor(if (selectedCard == 2) colorSelected else colorUnselected)
        }


        viewCard1.setOnClickListener{
            onSelectCard(0)
        }
        viewCard2.setOnClickListener{
            onSelectCard(1)
        }
        viewCard3.setOnClickListener{
            onSelectCard(2)
        }
        val card1Text = binding.fragmentCardSelectionCard1TextDescriptionCard
        val card2Text = binding.fragmentCardSelectionCard2TextDescriptionCard
        val card3Text = binding.fragmentCardSelectionCard3TextDescriptionCard

        val card1SmashMeterCost = binding.fragmentCardSelectionCard1TextSmashMeterCost
        val card2SmashMeterCost = binding.fragmentCardSelectionCard2TextSmashMeterCost
        val card3SmashMeterCost = binding.fragmentCardSelectionCard3TextSmashMeterCost

        fun updateCardsTexts(){
            Log.d("cardselection", "new cards : $cardsInDeck")
            //replace text and cost with these new cards
            card1Text.setText(cardsInDeck[0].description)
            card2Text.setText(cardsInDeck[1].description)
            card3Text.setText(cardsInDeck[2].description)

            card1SmashMeterCost.setText(cardsInDeck[0].cost.toString())
            card2SmashMeterCost.setText(cardsInDeck[1].cost.toString())
            card3SmashMeterCost.setText(cardsInDeck[2].cost.toString())
        }
        val confirmButton = binding.fragmentCardSelectionBtnConfirm
        confirmButton.setOnClickListener{
            if(selectedCard != -1){
                //heck if current player has enough energy to buy it
                val cardUsed = onConfirm(cardsInDeck[selectedCard])
                if(!cardUsed){
                    //not enough smash meter
                    Log.d("CardSelectio,Fragment", "Card not used, not enough smash meter")
                    Toast.makeText(this.context, "Not enough smash meter !", Toast.LENGTH_LONG).show()
                }
                else {
                    closeFragment()
                }
            }
            else {
                closeFragment()
            }
        }
        
        val rerollButton = binding.fragmentCardSelectionBtnReroll
        rerollButton.setOnClickListener{
            //need to check if enough money
            if(beforeReroll(2)){
                cardsInDeck = onReroll() as MutableList<Card>
                updateCardsTexts()
            }
            else {
                Toast.makeText(this.context, "Not enough smash meter !", Toast.LENGTH_LONG).show()
            }
        }
        updateCardsTexts()
        // Inflate the layout for this fragment
        return view
    }
    private fun closeFragment() {
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.remove(this)
        fragmentTransaction.commit()
    }
}