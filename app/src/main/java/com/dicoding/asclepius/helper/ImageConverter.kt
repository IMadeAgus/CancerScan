package com.dicoding.asclepius.helper

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.ByteArrayOutputStream
import java.io.InputStream

object ImageConverter {


        fun uriToByteArray(contentResolver: ContentResolver, uri: Uri?, maxWidth: Int = 1000, maxHeight: Int = 1000): ByteArray? {
            return try {
                val inputStream: InputStream? = uri?.let { contentResolver.openInputStream(it) }
                val bitmap = BitmapFactory.decodeStream(inputStream)
                val resizedBitmap = resizeBitmap(bitmap, maxWidth, maxHeight)
                bitmapToByteArray(resizedBitmap)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        fun byteArrayToBitmap(byteArray: ByteArray): Bitmap? {
            return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        }


        private fun bitmapToByteArray(bitmap: Bitmap?): ByteArray? {
            if (bitmap == null) return null
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 30, stream)
            return stream.toByteArray()
        }

        private fun resizeBitmap(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
            val width = bitmap.width
            val height = bitmap.height
            val ratio: Float = width.toFloat() / height.toFloat()
            val newWidth: Int
            val newHeight: Int

            if (ratio > 1) {
                newWidth = maxWidth
                newHeight = (maxWidth / ratio).toInt()
            } else {
                newHeight = maxHeight
                newWidth = (maxHeight * ratio).toInt()
            }

            return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
        }
}