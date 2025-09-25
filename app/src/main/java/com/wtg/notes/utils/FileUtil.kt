package com.wtg.notes.utils

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.OpenableColumns
import java.io.File
import java.io.FileDescriptor
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

object FileUtil {

    fun copy(sourceFile: File, destinationFile: FileDescriptor) {
        FileInputStream(sourceFile).channel.use { source ->
            FileOutputStream(destinationFile).channel.use { destination ->
                destination.transferFrom(source, 0, source.size())
            }
        }
    }

    fun copy(sourceFile: FileDescriptor, destinationFile: File) {
        FileInputStream(sourceFile).channel.use { source ->
            FileOutputStream(destinationFile).channel.use { destination ->
                destination.transferFrom(source, 0, source.size())
            }
        }
    }

    fun resolveFilename(resolver: ContentResolver, uri: Uri): String {
        val cursor = resolver.query(uri, null, null, null, null)
        var filename = ""

        cursor?.use { c ->
            if (c.moveToFirst()) {
                val columnNameIndex =
                    c.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                filename = c.getString(columnNameIndex)
            }
        }

        return filename
    }

    fun export(context: Context, uri: Uri, databaseFile: File): String {
        return try {
            val parcelFileDescriptor = context.contentResolver.openFileDescriptor(uri, "w")
                ?: return "Can't open file descriptor for writing"

            parcelFileDescriptor.use {
                copy(databaseFile, it.fileDescriptor)
            }

            val absolutePath =
                context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.absolutePath + File.separator + resolveFilename(
                    context.contentResolver,
                    uri
                )

            absolutePath.ifEmpty { "Can't resolve path file" }
        } catch (e: IOException) {
            "Error: ${e.message}"
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }

    fun import(context: Context, uri: Uri, databaseFile: File): String {
        return try {
            val parcelFileDescriptor = context.contentResolver.openFileDescriptor(uri, "r")
                ?: return "Can't open file descriptor for reading"

            parcelFileDescriptor.use {
                copy(it.fileDescriptor, databaseFile)
            }

            "The backup was restored successfully"
        } catch (ioe: IOException) {
            "Error: ${ioe.message}"
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }
}