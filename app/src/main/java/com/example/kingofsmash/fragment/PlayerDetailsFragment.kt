package com.example.kingofsmash.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kingofsmash.R
import com.example.kingofsmash.databinding.FragmentPlayerCardItemBinding
import com.example.kingofsmash.databinding.FragmentPlayerDetailsBinding
import com.example.kingofsmash.models.Card
import com.example.kingofsmash.utils.playerDetailsAdapter


class PlayerDetailsFragment(val cards : List<Card>) : Fragment() {
    private lateinit var binding : FragmentPlayerDetailsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_player_details, container, false)
        binding = FragmentPlayerDetailsBinding.bind(view)
        // Inflate the layout for this fragment

        val button = binding.fragmentPlayerDetailsButton
        button.setOnClickListener {
            closeFragment()
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = playerDetailsAdapter(cards)
        }
    }
    private fun closeFragment() {
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.remove(this)
        fragmentTransaction.commit()
    }
}