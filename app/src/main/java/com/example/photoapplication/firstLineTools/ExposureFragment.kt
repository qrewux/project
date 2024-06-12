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
import jp.co.cyberagent.android.gpuimage.filter.GPUImageExposureFilter

class ExposureFragment : Fragment() {
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var gpuImage: GPUImage
    private lateinit var exposureFilter: GPUImageExposureFilter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_exposure, container, false)
        val imageView: ImageView = view.findViewById(R.id.uploadImage)
        val seekBar: SeekBar = view.findViewById(R.id.seekBarExposure)
        val applyButton: ImageView = view.findViewById(R.id.ApplyExposure)
        val deleteButton: ImageView = view.findViewById(R.id.DeleteExposure)

        //ініціалізація GPUImage та експозиції
        gpuImage = GPUImage(context)
        exposureFilter = GPUImageExposureFilter(0.0f)
        gpuImage.setFilter(exposureFilter)

        // початковий стан seekBar
        seekBar.progress = 50

        sharedViewModel.image.observe(viewLifecycleOwner, { image ->
            imageView.setImageBitmap(image)
            gpuImage.setImage(image)
        })

        //setOnClickListner для seekBar
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val exposure = (progress - 50) / 50.0f
                exposureFilter.setExposure(exposure)
                imageView.setImageBitmap(gpuImage.bitmapWithFilterApplied)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        //збереження експозиції
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