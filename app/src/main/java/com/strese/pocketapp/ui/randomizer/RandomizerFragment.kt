package com.strese.pocketapp.ui.randomizer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.strese.pocketapp.databinding.FragmentRandomizerBinding

class RandomizerFragment : Fragment() {

    private var _binding: FragmentRandomizerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val randomizerViewModel = ViewModelProvider(this)[RandomizerViewModel::class.java]

        _binding = FragmentRandomizerBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}