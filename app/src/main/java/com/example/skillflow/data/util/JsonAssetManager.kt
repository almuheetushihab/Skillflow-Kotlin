package com.example.skillflow.data.util

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.json.Json
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manager class for reading JSON files from the assets folder.
 */
@Singleton
class JsonAssetManager @Inject constructor(
    @ApplicationContext @PublishedApi internal val context: Context,
    @PublishedApi internal val json: Json
) {
    /**
     * Reads a JSON file from assets and decodes it into the specified type.
     */
    inline fun <reified T> readAsset(fileName: String): T? {
        return try {
            val inputStream: InputStream = context.assets.open(fileName)
            val size: Int = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            val jsonString = String(buffer, Charsets.UTF_8)
            json.decodeFromString<T>(jsonString)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
