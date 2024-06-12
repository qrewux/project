package com.example.photoapplication

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import android.os.Build
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.ContentValues
import android.graphics.drawable.BitmapDrawable
import android.os.Environment
import android.widget.LinearLayout
import androidx.lifecycle.ViewModelProvider
import com.example.photoapplication.filters.FiltersFragment
import com.example.photoapplication.firstLineTools.BrightnessFragment
import com.example.photoapplication.firstLineTools.ContrastFragment
import com.example.photoapplication.firstLineTools.ExposureFragment
import com.example.photoapplication.firstLineTools.HighlightsFragment
import com.example.photoapplication.firstLineTools.SaturationFragment
import com.example.photoapplication.firstLineTools.ShadowsFragment
import com.example.photoapplication.firstLineTools.SharpenFragment
import com.example.photoapplication.firstLineTools.VignetteFragment
import com.example.photoapplication.firstLineTools.WarmthFragment
import com.example.photoapplication.fourthLineTools.CropFragment
import com.example.photoapplication.fourthLineTools.FrameFragment
import com.example.photoapplication.fourthLineTools.RotateFragment
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private val GALLERY_REQUEST_CODE = 100
    private val CAMERA_PERMISSION_REQUEST_CODE = 101
    private val CAMERA_REQUEST_CODE = 102
    private var imageSelected = false
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)
        val imageView: ImageView = findViewById(R.id.uploadImage)
        sharedViewModel.image.observe(this, { image ->
            imageView.setImageBitmap(image)
        })

        val reselectImage: TextView = findViewById(R.id.reselectImage)
        reselectImage.visibility = View.GONE

        //колір статус бару
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = ContextCompat.getColor(this, R.color.status_bar_color)
        }

        //відкриваю діалог вибору фото
        val uploadImage: ImageView = findViewById(R.id.uploadImage)
        val tvForChoosingPhoto: TextView = findViewById(R.id.tvForChoosingPhoto)
        val ivAddPicture: ImageView = findViewById(R.id.ivAddPicture)

        val clickListener = View.OnClickListener {
            if (!imageSelected) {
                showBottomSheetDialog()
            }
        }
        uploadImage.setOnClickListener(clickListener)
        tvForChoosingPhoto.setOnClickListener(clickListener)
        ivAddPicture.setOnClickListener(clickListener)
    }

    private fun showBottomSheetDialog() {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.choose_layout, null)
        dialog.setContentView(view)

        val chooseGallery: MaterialButton = view.findViewById(R.id.chooseGallery)
        val chooseCamera: MaterialButton = view.findViewById(R.id.chooseCamera)

        chooseGallery.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, GALLERY_REQUEST_CODE)
            dialog.dismiss()
        }
        chooseCamera.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
            } else {
                openCamera()
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setImage(bitmap: Bitmap) {
        sharedViewModel.image.value = bitmap
        val tvForChoosingPhoto: TextView = findViewById(R.id.tvForChoosingPhoto)
        val ivAddPicture: ImageView = findViewById(R.id.ivAddPicture)
        val reselectImage: TextView = findViewById(R.id.reselectImage)
        tvForChoosingPhoto.visibility = View.GONE
        ivAddPicture.visibility = View.GONE
        reselectImage.visibility = View.VISIBLE
        imageSelected = true
    }

    private fun openExportDialog() {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.export_layout, null)
        dialog.setContentView(view)

        //оптимізовний jpeg format
        val firstFormat = dialog.findViewById<LinearLayout>(R.id.firstFormat)
        firstFormat?.setOnClickListener {
            val imageView: ImageView = findViewById(R.id.uploadImage)
            val bitmap = (imageView.drawable as BitmapDrawable).bitmap
            val desiredWidth = 1200
            val desiredHeight = 1600
            val widthRatio = bitmap.width.toFloat() / desiredWidth
            val heightRatio = bitmap.height.toFloat() / desiredHeight
            val ratio = if (widthRatio < heightRatio) widthRatio else heightRatio
            val finalWidth = (bitmap.width / ratio).toInt()
            val finalHeight = (bitmap.height / ratio).toInt()
            val scaledBitmap = Bitmap.createScaledBitmap(bitmap, finalWidth, finalHeight, true)
            saveImageToGallery(scaledBitmap, "MyImage", Bitmap.CompressFormat.JPEG, 80)
        }

        //оптимізовний png format
        val secondFormat = dialog.findViewById<LinearLayout>(R.id.secondFormat)
        secondFormat?.setOnClickListener {
            val imageView: ImageView = findViewById(R.id.uploadImage)
            val bitmap = (imageView.drawable as BitmapDrawable).bitmap
            //бажана висота і ширина
            val desiredWidth = bitmap.width / 2
            val desiredHeight = bitmap.height / 2
            //співвідношення між оригінальними та бажаними розмірами
            val widthRatio = bitmap.width.toFloat() / desiredWidth
            val heightRatio = bitmap.height.toFloat() / desiredHeight
            //використовую найменше співвідношення щоб зображення було в бажаному розмірі
            val ratio = if (widthRatio < heightRatio) widthRatio else heightRatio
            //вираховую кінцеву ширину і висоту
            val finalWidth = (bitmap.width / ratio).toInt()
            val finalHeight = (bitmap.height / ratio).toInt()
            //змінюю розмір зображення
            val scaledBitmap = Bitmap.createScaledBitmap(bitmap, finalWidth, finalHeight, true)
            //зберігаю
            saveImageToGallery(scaledBitmap, "MyImage", Bitmap.CompressFormat.PNG, 100)
        }
        //оптимізовний webp format
        val thirdFormat = dialog.findViewById<LinearLayout>(R.id.thirdFormat)
        thirdFormat?.setOnClickListener {
            val imageView: ImageView = findViewById(R.id.uploadImage)
            val bitmap = (imageView.drawable as BitmapDrawable).bitmap
            // Resize the bitmap to a smaller resolution
            val scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.width / 2, bitmap.height / 2, true)
            // Determine the quality setting based on the size of the image
            val quality = if (bitmap.width * bitmap.height > 1000000) { // 1 megapixel
                70 // Lower quality for larger images
            } else {
                90 // Higher quality for smaller images
            }
            // Save with the determined quality setting
            saveImageToGallery(scaledBitmap, "MyImage", Bitmap.CompressFormat.WEBP, quality)
        }

        dialog.show()
    }

    //збереження оптимізованого зображення
    fun saveImageToGallery(bitmap: Bitmap, fileName: String, format: Bitmap.CompressFormat, quality: Int) {
        val mimeType = when (format) {
            Bitmap.CompressFormat.JPEG -> "image/jpeg"
            Bitmap.CompressFormat.PNG -> "image/png"
            Bitmap.CompressFormat.WEBP -> "image/webp"
            else -> "image/*"
        }

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }
        }

        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        uri?.let {
            contentResolver.openOutputStream(it)?.use { outputStream ->
                bitmap.compress(format, quality, outputStream)
                Toast.makeText(this, "Photo saved", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            Toast.makeText(this, "Error saving image", Toast.LENGTH_SHORT).show()
        }
    }

    //для відкриття інструментів та навігації між ними
    private fun openToolsDialog() {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.tools_layout, null)
        dialog.setContentView(view)
        dialog.show()

        val brightnessView: TextView = view.findViewById(R.id.brightnessView)
        brightnessView.setOnClickListener {
            dialog.dismiss()
            val fragment = BrightnessFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        val contrastView: TextView = view.findViewById(R.id.contrastView)
        contrastView.setOnClickListener {
            dialog.dismiss()
            val fragment = ContrastFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        val exposureView: TextView = view.findViewById(R.id.exposureView)
        exposureView.setOnClickListener {
            dialog.dismiss()
            val fragment = ExposureFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        val cropView: TextView = view.findViewById(R.id.cropView)
        cropView.setOnClickListener {
            dialog.dismiss()
            val fragment = CropFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        val frameView: TextView = view.findViewById(R.id.frameView)
        frameView.setOnClickListener {
            dialog.dismiss()
            val fragment = FrameFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        val rotateView: TextView = view.findViewById(R.id.rotateView)
        rotateView.setOnClickListener {
            dialog.dismiss()
            val fragment = RotateFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        val highlightsView: TextView = view.findViewById(R.id.highlightsView)
        highlightsView.setOnClickListener {
            dialog.dismiss()
            val fragment = HighlightsFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        val saturationView: TextView = view.findViewById(R.id.saturationView)
        saturationView.setOnClickListener {
            dialog.dismiss()
            val fragment = SaturationFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        val sharpenView: TextView = view.findViewById(R.id.sharpenView)
        sharpenView.setOnClickListener {
            dialog.dismiss()
            val fragment = SharpenFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        val shadowsView: TextView = view.findViewById(R.id.shadowsView)
        shadowsView.setOnClickListener {
            dialog.dismiss()
            val fragment = ShadowsFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        val vignetteView: TextView = view.findViewById(R.id.vignetteView)
        vignetteView.setOnClickListener {
            dialog.dismiss()
            val fragment = VignetteFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        val warmthView: TextView = view.findViewById(R.id.warmthView)
        warmthView.setOnClickListener {
            dialog.dismiss()
            val fragment = WarmthFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }
    }

    //для відкриття фільтрів
    private fun openFilters() {
        val fragment = FiltersFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //якщо обираю фото з галереї
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val imageUri: Uri? = data?.data
            if (imageUri != null) {
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
                setImage(bitmap)
                val reselectImage: TextView = findViewById(R.id.reselectImage)
                reselectImage.setOnClickListener { showBottomSheetDialog() }
                val buttonTools: MaterialButton = findViewById(R.id.buttonTools)
                buttonTools.setOnClickListener { openToolsDialog() }
                val buttonExport: MaterialButton = findViewById(R.id.buttonExport)
                buttonExport.setOnClickListener { openExportDialog() }
                val buttonFilters: MaterialButton = findViewById(R.id.buttonFilters)
                buttonFilters.setOnClickListener { openFilters() }
            }

        //якщо з камери
        } else if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val bitmap = data?.extras?.get("data") as Bitmap
            setImage(bitmap)
            val reselectImage: TextView = findViewById(R.id.reselectImage)
            reselectImage.setOnClickListener { showBottomSheetDialog() }
            val buttonTools: MaterialButton = findViewById(R.id.buttonTools)
            buttonTools.setOnClickListener { openToolsDialog() }
            val buttonExport: MaterialButton = findViewById(R.id.buttonExport)
            buttonExport.setOnClickListener { openExportDialog() }
            val buttonFilters: MaterialButton = findViewById(R.id.buttonFilters)
            buttonFilters.setOnClickListener { openFilters() }
        }
    }
}