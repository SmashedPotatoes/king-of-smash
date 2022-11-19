package com.example.kingofsmash.fragment

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.navigation.fragment.findNavController
import com.example.kingofsmash.R
import com.example.kingofsmash.enum.Character

/**
 * A simple [Fragment] subclass.
 * Use the [CharacterSelectionFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CharacterSelectionFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_character_selection, container, false)

        /*
        var personnage actif = Charatcter.Lucas

        récupérer les 4 boutons persos
        de base le premier bouton est sélectionné
        quand on appuie sur un bouton => on change d'élément sélectionné (code => personnage actif+ visuel => background color)

        bouton play => navigate to fragment with param (personnage actif)
        */
        val selectedCharacter = Character.LUCAS

        val lucasButton = view.findViewById<ImageButton>(R.id.LucasButton)
        lucasButton.setOnClickListener {

        }
        lucasButton.setBackgroundColor(Color.parseColor("red"))
        val royButton = view.findViewById<ImageButton>(R.id.RoyButton)
        royButton.setOnClickListener {
        }
        val kingDDDButton = view.findViewById<ImageButton>(R.id.KingDDDButton)
        kingDDDButton.setOnClickListener {
        }
        val corrinButton = view.findViewById<ImageButton>(R.id.CorrinButton)
        corrinButton.setOnClickListener {
        }


        val button = view.findViewById<Button>(R.id.button_navigate_to_main_fragment)
        button.setOnClickListener {
            findNavController().navigate(R.id.action_characterSelectionFragment_to_fragment_main)
        }
        return view
    }
}