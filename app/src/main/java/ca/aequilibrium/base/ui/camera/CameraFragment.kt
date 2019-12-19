package ca.aequilibrium.base.ui.camera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import ca.aequilibrium.base.AutoFitPreviewBuilder
import ca.aequilibrium.base.ui.MainActivity
import ca.aequilibrium.base.R
import kotlinx.android.synthetic.main.activity_camera.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.ByteBuffer
import java.util.concurrent.TimeUnit
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import android.graphics.Matrix
import androidx.annotation.IntDef
import ca.aequilibrium.base.App
import com.orhanobut.logger.Logger

class CameraFragment : Fragment() {

//    companion object {
//        @Retention(AnnotationRetention.SOURCE)
//        @IntDef(STATE_PREVIEWING, STATE_IMAGEVIEW)
//        annotation class CameraState
//        // Declare the constants
//        const val STATE_PREVIEWING = 0
//        const val STATE_IMAGEVIEW = 1
//    }
//
//    @get:CameraState
//    @setparam:CameraState
//    private var state: Int = STATE_PREVIEWING

    private val FLAGS_FULLSCREEN =
        View.SYSTEM_UI_FLAG_LOW_PROFILE or
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION

    private val PERMISSIONS_REQUEST_CODE = 10

    private val BITMAP_MAX_WIDTH = 1536
    private val BITMAP_MAX_HEIGHT = 1024

    private lateinit var imageCaptureUseCase: ImageCapture
    private lateinit var previewUseCase: Preview
    private lateinit var tempCheckImage: Bitmap


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.activity_camera, container, false)
        return root
    }

    fun hasPermissions(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }


//    override fun onSaveInstanceState(outState: Bundle) {
//        outState.putInt("state", state);
//        super.onSaveInstanceState(outState);
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            if (hasPermissions(it))
                setupCamera()
            else
                requestPermissions(
                    arrayOf(Manifest.permission.CAMERA),
                    PERMISSIONS_REQUEST_CODE
                )
        }
    }

    override fun onStart() {
        super.onStart()
        (activity as? MainActivity)?.hideBarAndFloatButton()
    }

    override fun onStop() {
        super.onStop()
        (activity as? MainActivity)?.showBarAndFloatButton()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (PackageManager.PERMISSION_GRANTED == grantResults.firstOrNull())
                setupCamera()
            else {
                //DismissibleDialog(this, resources.getString(R.string.deposit_not_available),
                //                    resources.getString(R.string.camera_permission_denied_message)).show()
                //todo error handling
            }
        }
    }

    private fun setupCamera() {
        cameraPreview.post {
            CameraX.unbindAll()
            val metrics = DisplayMetrics()
            cameraPreview.display.getMetrics(metrics)
            val screenAspectRatio = aspectRatio(metrics.widthPixels, metrics.heightPixels)

            val previewConfig = PreviewConfig.Builder()
                .setLensFacing(CameraX.LensFacing.BACK)
                .setTargetRotation(cameraPreview.display.rotation)
                .setTargetAspectRatio(screenAspectRatio)
                .build()
            previewUseCase = AutoFitPreviewBuilder.build(previewConfig, cameraPreview)

            val imageCaptureConfig = ImageCaptureConfig.Builder()
                .setLensFacing(CameraX.LensFacing.BACK)
                .setCaptureMode(ImageCapture.CaptureMode.MIN_LATENCY)
                .setTargetAspectRatio(screenAspectRatio)
                .setTargetRotation(cameraPreview.display.rotation)
                .build()
            imageCaptureUseCase = ImageCapture(imageCaptureConfig)

            // !!! turn on analyzer will cause the emulator performance downgrade a lot.
            // Setup image analysis pipeline that computes average pixel luminance
            //val analyzerConfig = ImageAnalysisConfig.Builder().apply {
            // In our analysis, we care more about the latest image than
            // analyzing *every* image
            //    setImageReaderMode(
            //        ImageAnalysis.ImageReaderMode.ACQUIRE_LATEST_IMAGE)
            //}.build()
            // Build the image analysis use case and instantiate our analyzer
            //val analyzerUseCase = ImageAnalysis(analyzerConfig).apply {
            //    setAnalyzer(executor, LuminosityAnalyzer())
            //}

            CameraX.bindToLifecycle(this, previewUseCase, imageCaptureUseCase)
            //CameraX.bindToLifecycle(this, previewUseCase, imageCaptureUseCase, analyzerUseCase)
        }
        previewViewFinder()
    }

    val captureToMemoryListener = object : ImageCapture.OnImageCapturedListener() {
        override fun onCaptureSuccess(image: ImageProxy, rotationDegrees: Int) {
            tempCheckImage = image.toBitmap()

            confirmCapturedImage()
        }
    }

    val captureToFileListener = object : ImageCapture.OnImageSavedListener {
        override fun onError(
            imageCaptureError: ImageCapture.ImageCaptureError,
            message: String,
            exc: Throwable?
        ) {
            val msg = "Photo capture failed: $message"
            Logger.e("CameraXApp", msg, exc)
            cameraPreview.post {
                Toast.makeText(App.instance, msg, Toast.LENGTH_SHORT).show()
            }
        }

        override fun onImageSaved(file: File) {
            val msg = "Photo capture succeeded"
            Logger.d("CameraXApp", msg)
            cameraPreview.post {
                Toast.makeText(App.instance, msg, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun previewViewFinder() {
        cameraPreview.visibility = View.VISIBLE
        imagePreview.visibility = View.GONE
        setLabels(
            getString(R.string.title_take_photo),
            getString(R.string.label_cancel),
            getString(R.string.label_take_photo)
        )

        decline.setOnClickListener {
            //todo back
        }

        accept.setOnClickListener {
            imageCaptureUseCase.takePicture(
                ContextCompat.getMainExecutor(App.instance), captureToMemoryListener
            )
        }

        /**        accept.setOnClickListener {
         *            val file = File(externalMediaDirs.first(),"{System.currentTimeMillis()}.jpg")
         *            imageCaptureUseCase.takePicture(file,
         *                ContextCompat.getMainExecutor(this), captureToFileListener)
         *                }
         */
    }

    private fun confirmCapturedImage() {
        cameraPreview.visibility = View.GONE
        imagePreview.visibility = View.VISIBLE

        setLabels(
            getString(R.string.label_review_picture),
            getString(R.string.label_retake_photo),
            getString(R.string.label_keep_photo)
        )
        imagePreview.setImageBitmap(tempCheckImage)

        decline.setOnClickListener { previewViewFinder() } // Retake

        accept.setOnClickListener {
            //todo dealwith tmpCheckImage
            previewViewFinder()
        }
    }

    /**
     *  [androidx.camera.core.ImageAnalysisConfig] requires enum value of [androidx.camera.core.AspectRatio].
     *  Currently it has values of 4:3 & 16:9.
     *
     *  Detecting the most suitable ratio for dimensions provided in @params by counting absolute
     *  of preview ratio to one of the provided values.
     *
     *  @param width - preview width
     *  @param height - preview height
     *  @return suitable aspect ratio
     */
    private fun aspectRatio(width: Int, height: Int): AspectRatio {
        val ratio_4_3 = 4.0 / 3.0
        val ratio_16_9 = 16.0 / 9.0
        val previewRatio = max(width, height).toDouble() / min(width, height)

        if (abs(previewRatio - ratio_4_3) <= abs(previewRatio - ratio_16_9)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }

    private fun setLabels(title: String, declineText: String, acceptText: String) {
        pageTitle.text = title

        decline.text = declineText
        accept.text = acceptText
    }

    private fun ImageProxy.toBitmap(): Bitmap {
        val buffer = planes.first().buffer
        val byteArray = ByteArray(buffer.capacity())
        buffer.get(byteArray)

        val options = BitmapFactory.Options()
        options.run {
            inJustDecodeBounds = true
            BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size, this)

            inSampleSize = calculateInSampleSize(this,
                BITMAP_MAX_WIDTH,
                BITMAP_MAX_HEIGHT
            )

            // setback to false to do real decoding
            inJustDecodeBounds = false
        }
        close()
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size, options).getRotateImage()
    }

    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        // Raw height and width of image
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            inSampleSize *= 2
            while (height / inSampleSize >= reqHeight || width / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    private fun Bitmap.toBase64(): String {
        val bytesStream = ByteArrayOutputStream()
        compress(Bitmap.CompressFormat.PNG, 100, bytesStream)
        val bytes = bytesStream.toByteArray()
        return String(Base64.encode(bytes, Base64.DEFAULT))
    }

    private fun Bitmap.getRotateImage(): Bitmap {
        when(cameraPreview.display.rotation) {
            1 ->
                return this;
            2 ->
                return rotateImage(this, 270f);
            3 ->
                return rotateImage(this, 180f);
            0 ->
                return rotateImage(this, 90f);
        }
        return this
    }

    fun rotateImage(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height,
            matrix, true
        )
    }
}

private class LuminosityAnalyzer : ImageAnalysis.Analyzer {
    private var lastAnalyzedTimestamp = 0L

    /**
     * Helper extension function used to extract a byte array from an
     * image plane buffer
     */
    private fun ByteBuffer.toByteArray(): ByteArray {
        rewind()    // Rewind the buffer to zero
        val data = ByteArray(remaining())
        get(data)   // Copy the buffer into a byte array
        return data // Return the byte array
    }

    override fun analyze(image: ImageProxy, rotationDegrees: Int) {
        val currentTimestamp = System.currentTimeMillis()
        // Calculate the average luma no more often than every second
        if (currentTimestamp - lastAnalyzedTimestamp >=
            TimeUnit.SECONDS.toMillis(1)
        ) {
            // Since format in ImageAnalysis is YUV, image.planes[0]
            // contains the Y (luminance) plane
            val buffer = image.planes[0].buffer
            // Extract image data from callback object
            val data = buffer.toByteArray()
            // Convert the data into an array of pixel values
            val pixels = data.map { it.toInt() and 0xFF }
            // Compute average luminance for the image
            val luma = pixels.average()
            // Log the new luma value
            Logger.d("CameraXApp", "Average luminosity: $luma")
            // Update timestamp of last analyzed frame
            lastAnalyzedTimestamp = currentTimestamp
        }
    }
}
