package com.example.photoapplication.firstLineTools

import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.photoapplication.R
import com.example.photoapplication.SharedViewModel
import jp.co.cyberagent.android.gpuimage.GPUImage
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSaturationFilter

class SaturationFragment : Fragment() {
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var gpuImage: GPUImage
    private lateinit var saturationFilter: GPUImageSaturationFilter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_saturation, container, false)
        val imageView: ImageView = view.findViewById(R.id.uploadImage)
        val seekBar: SeekBar = view.findViewById(R.id.seekBarSaturation)
        val applyButton: ImageView = view.findViewById(R.id.ApplySaturation)
        val deleteButton: ImageView = view.findViewById(R.id.DeleteSaturation)

        //ініціалізація GPUImage і фільтра насиченості
        gpuImage = GPUImage(context)
        saturationFilter = GPUImageSaturationFilter(1.0f)
        gpuImage.setFilter(saturationFilter)

        //початковий стан seekBar
        seekBar.progress = 50

        sharedViewModel.image.observe(viewLifecycleOwner, { image ->
            imageView.setImageBitmap(image)
            gpuImage.setImage(image)
        })

        //setOnClickListner для seekBar
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val saturation = progress / 50.0f
                saturationFilter.setSaturation(saturation)
                imageView.setImageBitmap(gpuImage.bitmapWithFilterApplied)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        //збереження насиченості
        applyButton.setOnClickListener {
            sharedViewModel.image.value = (imageView.drawable as BitmapDrawable).bitmap
            activity?.onBackPressed()
        }

        //повертаюсь без збереження
        deleteButton.setOnClickListener {
            activity?.onBackPressed()
        }
        return view
    }
}