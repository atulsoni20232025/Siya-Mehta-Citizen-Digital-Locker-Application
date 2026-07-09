package com.example.digitallocker

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import okhttp3.Call
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import okhttp3.*

class UploadActivity : AppCompatActivity() {

    private var fileUri: Uri? = null


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        findViewById<Button>(R.id.selectBtn).setOnClickListener {

            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"
            intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("application/pdf", "image/*"))
            startActivityForResult(intent, 1)

            startActivityForResult(Intent.createChooser(intent, "Select File"), 100)
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        }

        findViewById<Button>(R.id.uploadBtn).setOnClickListener {

            if (fileUri != null) {
                uploadToCloudinary(fileUri!!)
            } else {
                Toast.makeText(this, "Select file first", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == RESULT_OK) {

            if (data != null && data.data != null) {
                fileUri = data.data
                Toast.makeText(this, "File Selected", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "No File Selected", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadToCloudinary(fileUri: Uri) {

        val mimeType = contentResolver.getType(fileUri)

        val fileType = if (mimeType?.contains("image") == true) {
            "image"
        } else {
            "pdf"
        }

        val uploadUrl = if (fileType == "image") {
            "https://api.cloudinary.com/v1_1/dilx7lmxz/image/upload"
        } else {
            "https://api.cloudinary.com/v1_1/dilx7lmxz/raw/upload"
        }

        val inputStream = contentResolver.openInputStream(fileUri)
        val bytes = inputStream!!.readBytes()

        val requestBody = okhttp3.MultipartBody.Builder()
            .setType(okhttp3.MultipartBody.FORM)
            .addFormDataPart("file", "file", okhttp3.RequestBody.create(null, bytes))
            .addFormDataPart("upload_preset", "digitallocker_preset")
            .build()

        val request = okhttp3.Request.Builder()
            .url(uploadUrl)
            .post(requestBody)
            .build()

        val client = okhttp3.OkHttpClient()

        client.newCall(request).enqueue(object : okhttp3.Callback {

            override fun onFailure(call: okhttp3.Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@UploadActivity, "Upload Failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {

                val json = JSONObject(response.body!!.string())

                if (json.has("secure_url")) {

                    val fileUrl = json.getString("secure_url")

                    saveToDatabase(fileUrl, fileType)   // 🔥 PASS TYPE

                    runOnUiThread {
                        Toast.makeText(this@UploadActivity, "Uploaded", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun saveToDatabase(fileUrl: String, fileType: String) {

        val db = FirebaseDatabase.getInstance().reference
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        val docId = db.child("documents").push().key!!

        val document = Document(
            fileUrl = fileUrl,
            userId = userId,
            fileName = "Doc_${System.currentTimeMillis()}",
            fileType = fileType,   // 🔥 IMPORTANT
            timestamp = System.currentTimeMillis()
        )

        db.child("documents").child(docId).setValue(document)
    }
}