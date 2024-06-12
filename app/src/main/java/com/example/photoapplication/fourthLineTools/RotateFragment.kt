package com.example.photoapplication.fourthLineTools

import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.photoapplication.R
import com.example.photoapplication.SharedViewModel

class RotateFragment : Fragment() {
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_rotate, container, false)
        val imageView: ImageView = view.findViewById(R.id.uploadImage)
        val rotateButton: ImageView = view.findViewById(R.id.rotateImage)
        val mirrorButton: ImageView = view.findViewById(R.id.mirrorImage)
        val applyButton: ImageView = view.findViewById(R.id.ApplyRotate)
        val deleteButton: ImageView = view.findViewById(R.id.DeleteRotate)

        sharedViewModel.image.observe(viewLifecycleOwner, { image ->
            imageView.setImageBitmap(image)
        })

        rotateButton.setOnClickListener {
            imageView.drawable?.let {
                val bitmap = (it as BitmapDrawable).bitmap
                val rotatedBitmap = rotateBitmap(bitmap, 90f)
                imageView.setImageBitmap(rotatedBitmap)
                sharedViewModel.image.value = rotatedBitmap
            }
        }

        mirrorButton.setOnClickListener {
            imageView.drawable?.let {
                val bitmap = (it as BitmapDrawable).bitmap
                val mirroredBitmap = mirrorBitmap(bitmap)
                imageView.setImageBitmap(mirroredBitmap)
                sharedViewModel.image.value = mirroredBitmap
            }
        }

        applyButton.setOnClickListener {
            activity?.onBackPressed()
        }

        deleteButton.setOnClickListener {
            activity?.onBackPressed()
        }

        return view
    }


    private fun rotateBitmap(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    private fun mirrorBitmap(source: Bitmap): Bitmap {
        val matrix = Matrix()
        matrix.preScale(-1.0f, 1.0f)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }
}