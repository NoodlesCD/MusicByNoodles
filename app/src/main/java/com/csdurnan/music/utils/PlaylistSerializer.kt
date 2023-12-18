//package com.csdurnan.music.utils
//
//import androidx.datastore.core.Serializer
//import com.csdurnan.music.dc.Playlist
//import kotlinx.serialization.ExperimentalSerializationApi
//import kotlinx.serialization.SerializationException
//import kotlinx.serialization.json.Json
//import java.io.InputStream
//import java.io.OutputStream
//
//@OptIn(ExperimentalSerializationApi::class)
//object PlaylistSerializer : Serializer<Playlist> {
//    override val defaultValue: Playlist
//        get() = Playlist("")
//
//    override suspend fun readFrom(input: InputStream): Playlist {
//        return try {
//            Json.decodeFromString(
//                deserializer = Playlist.serializer(),
//                string = input.readBytes().decodeToString()
//            )
//        } catch (e: SerializationException) {
//            e.printStackTrace()
//            defaultValue
//        }
//    }
//
//    override suspend fun writeTo(t: Playlist, output: OutputStream) {
//        output.write(
//            Json.encodeToString(
//                serializer = Playlist.serializer(),
//                value = t
//            ).encodeToByteArray()
//        )
//    }
//}