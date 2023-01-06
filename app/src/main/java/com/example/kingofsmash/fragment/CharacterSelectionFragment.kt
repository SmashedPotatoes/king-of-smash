package com.example.kingofsmash.fragment

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.kingofsmash.R
import com.example.kingofsmash.databinding.FragmentCharacterSelectionBinding
import com.example.kingofsmash.enums.Character

class CharacterSelectionFragment : Fragment() {

    private lateinit var binding: FragmentCharacterSelectionBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_character_selection, container, false)
        binding = FragmentCharacterSelectionBinding.bind(view)

        var selectedCharacter = Character.LUCAS
        val colorSelected = Color.argb(190, 62, 191, 137)
        val colorUnselected = Color.argb(127, 13, 13, 13)

        val viewLucas = binding.viewLucas
        val viewRoy = binding.viewRoy
        val viewKingDDD = binding.viewKingDDD
        val viewCorrin = binding.viewCorrin

        // set viewLucas background shape solid color
        val shapeLucas = viewLucas.background as GradientDrawable
        val shapeRoy = viewRoy.background as GradientDrawable
        val shapeKingDDD = viewKingDDD.background as GradientDrawable
        val shapeCorrin = viewCorrin.background as GradientDrawable

        fun onSelectCharacter(character: Character, isInit: Boolean = false) {
            if (!isInit && selectedCharacter == character) return

            selectedCharacter = character

            shapeLucas.setColor(if (character == Character.LUCAS) colorSelected else colorUnselected)
            shapeRoy.setColor(if (character == Character.ROY) colorSelected else colorUnselected)
            shapeKingDDD.setColor(if (character == Character.KINGDDD) colorSelected else colorUnselected)
            shapeCorrin.setColor(if (character == Character.CORRIN) colorSelected else colorUnselected)
        }
        // selected character by default is Lucas
        onSelectCharacter(Character.LUCAS, true)

        // on clicks
        viewLucas.setOnClickListener { onSelectCharacter(Character.LUCAS) }
        viewRoy.setOnClickListener { onSelectCharacter(Character.ROY) }
        viewKingDDD.setOnClickListener { onSelectCharacter(Character.KINGDDD) }
        viewCorrin.setOnClickListener { onSelectCharacter(Character.CORRIN) }

        val button = binding.buttonNavigateToMainFragment
        button.setOnClickListener {
            findNavController().navigate(
                CharacterSelectionFragmentDirections.actionCharacterSelectionFragmentToFragmentMain(
                    selectedCharacter
                )
            )
        }
        return view
    }
}