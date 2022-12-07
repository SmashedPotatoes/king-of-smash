package com.example.kingofsmash.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.kingofsmash.R
import com.example.kingofsmash.databinding.FragmentPlayerInDfBinding

class PlayerInDFFragment: Fragment() {
    private lateinit var binding: FragmentPlayerInDfBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlayerInDfBinding.inflate(inflater, container, false)
        binding.root.setBackgroundResource(R.color.black_transparent)
        binding.root.setOnClickListener {
            closeFragment()
        }
        return binding.root
    }

    private fun closeFragment() {
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.remove(this)
        fragmentTransaction.commit()
    }
}