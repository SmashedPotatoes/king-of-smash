package com.example.kingofsmash.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.kingofsmash.R
import com.example.kingofsmash.databinding.FragmentCardSelectionBinding

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
        val view = inflater.inflate(R.layout.fragment_card_selection, container, false)
        binding = FragmentCardSelectionBinding.bind(view)
        val viewCard1 = binding.fragmentCardSelectionViewCard1;
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