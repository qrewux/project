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
import jp.co.cyberagent.android.gpuimage.filter.GPUImageContrastFilter

class ContrastFragment : Fragment() {
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var gpuImage: GPUImage
    private lateinit var contrastFilter: GPUImageContrastFilter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_contrast, container, false)
        val imageView: ImageView = view.findViewById(R.id.uploadImage)
        val seekBar: SeekBar = view.findViewById(R.id.seekBarContrast)
        val applyButton: ImageView = view.findViewById(R.id.ApplyContrast)
        val deleteButton: ImageView = view.findViewById(R.id.DeleteContrast)

        // ініціалізація GPUImage та фільтра контрасту
        gpuImage = GPUImage(context)
        contrastFilter = GPUImageContrastFilter(1.0f)
        gpuImage.setFilter(contrastFilter)

        // початковий стан seekBar
        seekBar.progress = 50

        sharedViewModel.image.observe(viewLifecycleOwner, { image ->
            imageView.setImageBitmap(image)
            gpuImage.setImage(image)
        })

        //setOnClickListner для seekBar
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val contrast = progress / 50.0f
                contrastFilter.setContrast(contrast)
                imageView.setImageBitmap(gpuImage.bitmapWithFilterApplied)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // збереження контрасту
        applyButton.setOnClickListener {
            sharedViewModel.image.value = (imageView.drawable as BitmapDrawable).bitmap
            activity?.onBackPressed()
        }

        // повертаємось без збереження
        deleteButton.setOnClickListener {
            activity?.onBackPressed()
        }

        return view
    }
}