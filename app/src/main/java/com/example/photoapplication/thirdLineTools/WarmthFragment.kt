package com.example.photoapplication.firstLineTools

import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import androidx.fragment.app.activityViewModels
import com.example.photoapplication.R
import com.example.photoapplication.SharedViewModel
import jp.co.cyberagent.android.gpuimage.GPUImage
import jp.co.cyberagent.android.gpuimage.filter.GPUImageWhiteBalanceFilter

class WarmthFragment : Fragment() {
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var gpuImage: GPUImage
    private lateinit var warmthFilter: GPUImageWhiteBalanceFilter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_warmth, container, false)
        val imageView: ImageView = view.findViewById(R.id.uploadImage)
        val seekBar: SeekBar = view.findViewById(R.id.seekBarWarmth)
        val applyButton: ImageView = view.findViewById(R.id.ApplyWarmth)
        val deleteButton: ImageView = view.findViewById(R.id.DeleteWarmth)


        gpuImage = GPUImage(context)
        warmthFilter = GPUImageWhiteBalanceFilter(5000.0f, 1.0f)
        gpuImage.setFilter(warmthFilter)


        seekBar.progress = 50

        sharedViewModel.image.observe(viewLifecycleOwner, { image ->
            imageView.setImageBitmap(image)
            gpuImage.setImage(image)
        })


        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val warmth = (progress - 50) * 100.0f
                warmthFilter.setTemperature(warmth + 5000.0f)
                imageView.setImageBitmap(gpuImage.bitmapWithFilterApplied)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        applyButton.setOnClickListener {
            sharedViewModel.image.value = (imageView.drawable as BitmapDrawable).bitmap
            activity?.onBackPressed()
        }


        deleteButton.setOnClickListener {
            activity?.onBackPressed()
        }

        return view
    }
}