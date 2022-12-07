package com.example.kingofsmash.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.kingofsmash.R
import com.example.kingofsmash.databinding.FragmentGameOverBinding

/**
 * A simple [Fragment] subclass.
 * Use the [GameOverFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GameOverFragment : Fragment() {
    private lateinit var binding: FragmentGameOverBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGameOverBinding.inflate(inflater, container, false)
        binding.fragmentGameOverBtnMainMenu.setOnClickListener {
            findNavController().navigate(R.id.action_gameOverFragment_to_characterSelectionFragment)
        }
        return binding.root
    }
}