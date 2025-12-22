package com.example.shaadi.network

import android.content.Context
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.File

interface SupabaseStorageService {
    @JsonClass(generateAdapter = true)
    data class UploadResponse(@Json(name = "Key") val key: String?)

    @Multipart
    @POST("object/{bucket}")
    suspend fun upload(
        @Path("bucket") bucket: String,
        @Query("path") path: String,
        @Part file: MultipartBody.Part
    ): Response<UploadResponse>

    companion object {
        fun buildFilePart(context: Context, uri: Uri, formName: String = "file", filename: String = "upload.jpg"): MultipartBody.Part {
            val contentResolver = context.contentResolver
            val mime = contentResolver.getType(uri) ?: "image/jpeg"
            val input = contentResolver.openInputStream(uri) ?: throw IllegalStateException("Cannot open image stream")
            val bytes = input.readBytes()
            input.close()
            val rb: RequestBody = bytes.toRequestBody(mime.toMediaTypeOrNull())
            return MultipartBody.Part.createFormData(formName, filename, rb)
        }
    }
}
