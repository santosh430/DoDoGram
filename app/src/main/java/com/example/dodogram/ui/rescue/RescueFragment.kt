package com.example.dodogram.ui.rescue

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.dodogram.databinding.FragmentRescueBinding
import com.example.dodogram.ui.rescue.activity.RescueActivity

class RescueFragment : Fragment() {

    private var _binding: FragmentRescueBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(RescueViewModel::class.java)

        _binding = FragmentRescueBinding.inflate(inflater, container, false)
        val root: View = binding.root

        startActivity(Intent(context,RescueActivity::class.java))

        val textView: TextView = binding.textDashboard
        dashboardViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}