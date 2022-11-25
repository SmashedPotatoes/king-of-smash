package com.example.kingofsmash.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.example.kingofsmash.R
import com.example.kingofsmash.databinding.FragmentCharacterSelectionBinding
import com.example.kingofsmash.enums.Character

/**
 * A simple [Fragment] subclass.
 * Use the [CharacterSelectionFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CharacterSelectionFragment : Fragment() {

    private lateinit var binding: FragmentCharacterSelectionBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_character_selection, container, false)
        binding = FragmentCharacterSelectionBinding.bind(view)
        val button = binding.buttonNavigateToMainFragment
        button.setOnClickListener {
            findNavController().navigate(CharacterSelectionFragmentDirections.actionCharacterSelectionFragmentToFragmentMain(
                Character.LUCAS
            ))
        }
        return view
    }
}