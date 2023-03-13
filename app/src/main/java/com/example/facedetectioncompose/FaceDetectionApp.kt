package com.example.facedetectioncompose

import android.content.Context
import android.graphics.*
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.facedetectioncompose.ui.theme.FaceDetectionComposeTheme
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import java.io.IOException


private const val TAG = "FaceDetectionApp"

@Composable
fun FaceDetectionApp(modifier: Modifier = Modifier) {

    val fileName = "face-test-3.jpg"
//    ImageFromAssets(fileName = fileName)
    val context = LocalContext.current

    val fileList: Array<String> = context.assets.list("") as Array<String>
    for (f1 in fileList) {
//        Log.v(TAG, f1!!)
    }


    val bitmap: Bitmap? =  context.assetsToBitMap(fileName)
    if (bitmap == null) {
        Log.d(TAG, "success")
        return
    }
    var bitmapState by remember { mutableStateOf(bitmap!!) }
    Column(modifier = Modifier
        .fillMaxHeight()
        .wrapContentWidth()
        .padding(bottom = 10.dp)

        , horizontalAlignment = Alignment.CenterHorizontally ) {


        Image(
            bitmap = bitmapState.asImageBitmap()
            , contentDescription = "some useful description"
            , modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .padding(all = 0.dp)
                .weight(1f)
        )


        Button(modifier = Modifier
            .wrapContentSize()
            .weight(0.6f)
            , onClick = {
                val highAccuracyOpts = FaceDetectorOptions.Builder()
                    .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                    .build()
                val detector = FaceDetection.getClient(highAccuracyOpts)
                val image = InputImage.fromBitmap(bitmap!!, 0)
                val result = detector.process(image)
                    .addOnSuccessListener { faces ->
                        // Task completed successfully
                        // ...
                        bitmap!!.apply {
//                    img.setImageBitmap(drawWithRectangle(faces))
                            bitmapState = drawWithRectangle(faces)!!
                        }
                    }
                    .addOnFailureListener { e ->
                        // Task failed with an exception
                        // ...
                    }
            }
            , content = { Text("Detect") }
//        , elevation =  ButtonDefaults.elevation(
//            defaultElevation = 10.dp,
//            pressedElevation = 15.dp,
//            disabledElevation = 0.dp
//        )
        )
    }

}


fun Bitmap.drawWithRectangle(faces: List<Face>):Bitmap?{
    val bitmap = copy(config, true)
    val canvas = Canvas(bitmap)
    for (face in faces){
        val bounds = face.boundingBox
        Paint().apply {
            color = Color.GREEN
            style = Paint.Style.STROKE
            strokeWidth = 4.0f
            isAntiAlias = true
            // draw rectangle on canvas
            canvas.drawRect(
                bounds, this
            )
        }
    }
    return bitmap
}

fun Context.assetsToBitMap(fileName: String): Bitmap? {
    return try {
        with(assets.open(fileName)) {
            BitmapFactory.decodeStream(this)
        }
    } catch (e: IOException) { null }
}




@Composable
fun ImageFromAssets(fileName: String) {
    val context = LocalContext.current
    val bitmap: Bitmap? = context.assetsToBitMap(fileName)
    if (bitmap != null) {
        Log.d(TAG, "success")
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "some useful description",
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    FaceDetectionComposeTheme {
        FaceDetectionApp()
    }
}