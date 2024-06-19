package com.example.wellfish.ui.camera

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.SystemClock
import android.util.Log
import androidx.camera.core.ImageProxy
import com.example.wellfish.R
import com.google.android.gms.tflite.client.TfLiteInitializationOptions
import com.google.android.gms.tflite.gpu.support.TfLiteGpu
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.image.ops.Rot90Op
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.gms.vision.TfLiteVision
import org.tensorflow.lite.task.gms.vision.detector.Detection
import org.tensorflow.lite.task.gms.vision.detector.ObjectDetector

class ObjectDetectorHelper(
    var threshold: Float = 0.5f,
    var maxResults: Int = 3,
    val modelFile: String = "1.tflite", //nama asset ML
    val context: Context,
    val detectorListener: DetectorListener?
) {
    private var objectDetector: ObjectDetector? = null

    init {
        TfLiteGpu.isGpuDelegateAvailable(context).onSuccessTask { gpuAvailable ->
            val optionsBuilder = TfLiteInitializationOptions.builder()
            if (gpuAvailable) {
                optionsBuilder.setEnableGpuDelegateSupport(true)
            }
            TfLiteVision.initialize(context, optionsBuilder.build())
        }.addOnSuccessListener {
            setupObjectDetector()
        }.addOnFailureListener {
            detectorListener?.onError(context.getString(R.string.tflitevision_is_not_initialized_yet))
        }
    }

    private fun setupObjectDetector() {
        val optionsBuilder = ObjectDetector.ObjectDetectorOptions.builder()
            .setScoreThreshold(threshold)
            .setMaxResults(maxResults)

        val baseOptionsBuilder = BaseOptions.builder()
        if (CompatibilityList().isDelegateSupportedOnThisDevice) {
            baseOptionsBuilder.useGpu()
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            baseOptionsBuilder.useNnapi()
        } else {
            baseOptionsBuilder.setNumThreads(4)
        }
        optionsBuilder.setBaseOptions(baseOptionsBuilder.build())

        try {
            objectDetector = ObjectDetector.createFromFileAndOptions(
                context,
                modelFile,
                optionsBuilder.build()
            )
        } catch (e: IllegalStateException) {
            detectorListener?.onError(context.getString(R.string.image_classifier_failed))
            Log.e(TAG, e.message.toString())
        }
    }

    //proses detection
    fun detectObject(image: ImageProxy) {
        if (!TfLiteVision.isInitialized()) {
            val errorMessage = context.getString(R.string.tflitevision_is_not_initialized_yet)
            Log.e(TAG, errorMessage)
            detectorListener?.onError(errorMessage)
            return
        }

        if (objectDetector == null) {
            setupObjectDetector()
        }

        //mengatur orientasi gambar supaya sesuai dengan model
        val imageProcessor = ImageProcessor.Builder()
            .add(Rot90Op(-image.imageInfo.rotationDegrees / 90))
            .add(ResizeOp(160, 160, ResizeOp.ResizeMethod.BILINEAR))
            .build()

        val tensorImage = imageProcessor.process(TensorImage.fromBitmap(toBitmap(image)))

        var inferenceTime = SystemClock.uptimeMillis()
        val results = objectDetector?.detect(tensorImage)
        inferenceTime = SystemClock.uptimeMillis() - inferenceTime

        val processedResults = postProcessResults(results ?: listOf())

        detectorListener?.onResults(
            processedResults.toMutableList(),
            inferenceTime,
            tensorImage.height,
            tensorImage.width
        )
    }


    private fun postProcessResults(detectionResults: List<Detection>): List<Detection> {
        // Implementasikan logika post-processing di sini
    //    val processedResults = detectionResults.map { detection ->
    //        val outputTensor0 = detection.output[0]
    //        // Menambahkan dimensi pada tensor output jika hanya memiliki 2 dimensi
    //        if (outputTensor0.shape().size == 2) {
    //            val expandedOutput0 = expandDims(outputTensor0)
    //            detection.output[0] = expandedOutput0
    //         }
    //        detection
    //    }
        return detectionResults
    }

    private fun expandDims(tensor: TensorBuffer): TensorBuffer {
        val shape = tensor.shape
        val newShape = shape + intArrayOf(1)
        val expandedTensor = TensorBuffer.createDynamic(tensor.dataType)
        expandedTensor.loadArray(tensor.floatArray, newShape)
        return expandedTensor
    }

    private fun toBitmap(image: ImageProxy): Bitmap {
        val bitmapBuffer = Bitmap.createBitmap(
            image.width,
            image.height,
            Bitmap.Config.ARGB_8888
        )
        image.use { bitmapBuffer.copyPixelsFromBuffer(image.planes[0].buffer) }
        image.close()
        return bitmapBuffer
    }

    interface DetectorListener {
        fun onError(error: String)
        fun onResults(
            results: MutableList<Detection>?,
            inferenceTime: Long,
            imageHeight: Int,
            imageWidth: Int
        )
    }

    companion object {
        private const val TAG = "ObjectDetectorHelper"
    }
}
