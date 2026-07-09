package com.example.digitallocker

data class Document(
    var fileUrl: String? = "",
    var userId: String? = "",
    var fileName: String? = "",
    var fileType: String? = "",   // 🔥 NEW FIELD
    var timestamp: Long? = 0
)