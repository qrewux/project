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
import jp.co.cyberagent.android.gpuimage.filter.GPUImageVignetteFilter
import android.graphics.PointF

class VignetteFragment : Fragment() {
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var gpuImage: GPUImage
    private lateinit var vignetteFilter: GPUImageVignetteFilter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_vignette, container, false)
        val imageView: ImageView = view.findViewById(R.id.uploadImage)
        val seekBar: SeekBar = view.findViewById(R.id.seekBarVignette)
        val applyButton: ImageView = view.findViewById(R.id.ApplyVignette)
        val deleteButton: ImageView = view.findViewById(R.id.DeleteVignette)


        gpuImage = GPUImage(context)
        vignetteFilter = GPUImageVignetteFilter(PointF(0.5f, 0.5f), floatArrayOf(0.0f, 0.0f, 0.0f), 0.0f, 0.75f)
        gpuImage.setFilter(vignetteFilter)


        seekBar.progress = 50

        sharedViewModel.image.observe(viewLifecycleOwner, { image ->
            imageView.setImageBitmap(image)
            gpuImage.setImage(image)
        })


        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val vignette = progress / 100.0f
                vignetteFilter.setVignetteEnd(vignette)
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