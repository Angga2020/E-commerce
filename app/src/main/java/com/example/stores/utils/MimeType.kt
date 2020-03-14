package com.example.stores.utils

import android.webkit.MimeTypeMap
import java.io.File
import java.lang.Character.toLowerCase

fun File.getMimeType(fallback: String = "image/*"): String {
    return MimeTypeMap.getFileExtensionFromUrl(toString())
        ?.run { MimeTypeMap.getSingleton().getMimeTypeFromExtension(toLowerCase()) }
        ?: fallback // You might set it to */*
}