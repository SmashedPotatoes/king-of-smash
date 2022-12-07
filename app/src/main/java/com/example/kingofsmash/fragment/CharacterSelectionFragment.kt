package com.example.kingofsmash.fragment

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        var selectedCharacter = Character.LUCAS
        val colorSelected = Color.argb(190, 62, 191, 137)
        val colorUnselected = Color.argb(127, 13, 13, 13)

        val viewLucas = view.findViewById<View>(R.id.viewLucas)
        val viewRoy = view.findViewById<View>(R.id.viewRoy)
        val viewKingDDD = view.findViewById<View>(R.id.viewKingDDD)
        val viewCorrin = view.findViewById<View>(R.id.viewCorrin)

        // set viewLucas background shape solid color
        val shapeLucas = viewLucas.background as GradientDrawable
        val shapeRoy = viewRoy.background as GradientDrawable
        val shapeKingDDD = viewKingDDD.background as GradientDrawable
        val shapeCorrin = viewCorrin.background as GradientDrawable
        shapeLucas.setColor(if (selectedCharacter == Character.LUCAS)  colorSelected else colorUnselected)
        shapeRoy.setColor(if (selectedCharacter == Character.ROY)  colorSelected else colorUnselected)
        shapeKingDDD.setColor(if (selectedCharacter == Character.KINGDDD)  colorSelected else colorUnselected)
        shapeCorrin.setColor(if (selectedCharacter == Character.CORRIN)  colorSelected else colorUnselected)
        
        // on clicks
        viewLucas.setOnClickListener{
            selectedCharacter = Character.LUCAS

            shapeLucas.setColor(colorSelected)
            shapeRoy.setColor(colorUnselected)
            shapeKingDDD.setColor(colorUnselected)
            shapeCorrin.setColor(colorUnselected)
        }
        viewRoy.setOnClickListener{
            selectedCharacter = Character.ROY

            shapeLucas.setColor(colorUnselected)
            shapeRoy.setColor(colorSelected)
            shapeKingDDD.setColor(colorUnselected)
            shapeCorrin.setColor(colorUnselected)
        }
        viewKingDDD.setOnClickListener{
            selectedCharacter = Character.KINGDDD

            shapeLucas.setColor(colorUnselected)
            shapeRoy.setColor(colorUnselected)
            shapeKingDDD.setColor(colorSelected)
            shapeCorrin.setColor(colorUnselected)
        }
        viewCorrin.setOnClickListener{
            selectedCharacter = Character.CORRIN

            shapeLucas.setColor(colorUnselected)
            shapeRoy.setColor(colorUnselected)
            shapeKingDDD.setColor(colorUnselected)
            shapeCorrin.setColor(colorSelected)
        }

        binding = FragmentCharacterSelectionBinding.bind(view)
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