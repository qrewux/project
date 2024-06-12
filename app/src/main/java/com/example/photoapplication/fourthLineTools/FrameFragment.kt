package com.example.photoapplication.fourthLineTools

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.photoapplication.R


class FrameFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_frame, container, false)

        val deleteSaturation: ImageView = view.findViewById(R.id.DeleteFrame)
        deleteSaturation.setOnClickListener {

            fragmentManager?.popBackStack()
        }

        return view
    }
}