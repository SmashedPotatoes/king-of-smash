package com.example.kingofsmash.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.kingofsmash.R
import com.example.kingofsmash.databinding.FragmentCardSelectionBinding
import com.example.kingofsmash.models.Card

/**
 * A simple [Fragment] subclass.
 * Use the [CardSelectionFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CardSelectionFragment : Fragment() {

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
        card1Text.text = "aa"
        val confirmButton = binding.fragmentCardSelectionBtnConfirm
        confirmButton.setOnClickListener{
            closeFragment()
        }
        
        val rerollButton = binding.fragmentCardSelectionBtnReroll
        rerollButton.setOnClickListener{
            card1Text.setText("Rerolled")
        }
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