package com.strese.pocketapp.ui.generators

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.strese.pocketapp.databinding.FragmentGeneratorsBinding

class GeneratorsFragment : Fragment() {

    private var _binding: FragmentGeneratorsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val generatorsViewModel = ViewModelProvider(this)[GeneratorsViewModel::class.java]

        _binding = FragmentGeneratorsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}