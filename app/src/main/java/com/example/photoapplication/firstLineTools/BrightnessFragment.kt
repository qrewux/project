package com.example.photoapplication.firstLineTools

import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.photoapplication.R
import com.example.photoapplication.SharedViewModel
import android.widget.ImageView
import android.widget.SeekBar
import com.google.android.material.button.MaterialButton
import jp.co.cyberagent.android.gpuimage.GPUImage
import jp.co.cyberagent.android.gpuimage.filter.GPUImageBrightnessFilter

class BrightnessFragment : Fragment() {
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var gpuImage: GPUImage
    private lateinit var brightnessFilter: GPUImageBrightnessFilter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_brightness, container, false)
        val imageView: ImageView = view.findViewById(R.id.uploadImage)
        val seekBar: SeekBar = view.findViewById(R.id.seekBarBrightness)
        val applyButton: ImageView = view.findViewById(R.id.ApplyBrightness)
        val deleteButton: ImageView = view.findViewById(R.id.DeleteBrightness)



        //ініціалізація GPUImage та фільтра яскравості
        gpuImage = GPUImage(context)
        brightnessFilter = GPUImageBrightnessFilter(1.0f)
        gpuImage.setFilter(brightnessFilter)

        //початковий стан seekBar
        seekBar.progress = 50

        sharedViewModel.image.observe(viewLifecycleOwner, { image ->
            imageView.setImageBitmap(image)
            gpuImage.setImage(image)
        })

        //setOnClickListner для seekBar
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val brightness = (progress - 50) / 50.0f
                brightnessFilter.setBrightness(brightness)
                imageView.setImageBitmap(gpuImage.bitmapWithFilterApplied)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        //збереження яскравості
        applyButton.setOnClickListener {
            sharedViewModel.image.value = (imageView.drawable as BitmapDrawable).bitmap
            activity?.onBackPressed()
        }

        //повертаємось без збереження
        deleteButton.setOnClickListener {
            activity?.onBackPressed()
        }
        return view
    }
}