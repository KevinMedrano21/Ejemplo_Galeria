package com.example.ejemplo_galeria

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

data class Album (val id : Long, val nombre: String)



class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    data class Imagen(val id: Long, val albumName: String, val image: ByteArray, val description: String?)

    override fun onCreate(db: SQLiteDatabase) {
        val createAlbumsTable = "CREATE TABLE ${AlbumEntry.TABLE_NAME} (" +
                "${AlbumEntry._ID} INTEGER PRIMARY KEY," +
                "${AlbumEntry.COLUMN_NAME} TEXT NOT NULL);"

        val createImagenesTable = "CREATE TABLE ${ImagenEntry.TABLE_NAME} (" +
                "${ImagenEntry._ID} INTEGER PRIMARY KEY," +
                "${ImagenEntry.COLUMN_ALBUM_NAME} TEXT NOT NULL," +
                "${ImagenEntry.COLUMN_IMAGE} BLOB NOT NULL," +
                "${ImagenEntry.COLUMN_DESCRIPTION} TEXT);"


        db.execSQL(createImagenesTable)
        db.execSQL(createAlbumsTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Implementa esto si necesitas actualizar la base de datos en futuras versiones
    }

    fun insertAlbum(album: Album) {
        val db = writableDatabase
        val values = ContentValues()
        values.put(AlbumEntry.COLUMN_NAME, album.nombre)
        db.insert(AlbumEntry.TABLE_NAME, null, values)
        db.close()
    }

    fun insertImagen(imagen: Imagen) {
        val db = writableDatabase
        val values = ContentValues()
        values.put(ImagenEntry.COLUMN_ALBUM_NAME, imagen.albumName)
        values.put(ImagenEntry.COLUMN_IMAGE, imagen.image)
        values.put(ImagenEntry.COLUMN_DESCRIPTION, imagen.description)
        db.insert(ImagenEntry.TABLE_NAME, null, values)
        db.close()
    }

    fun getAllAlbums(): List<Album> {
        val albumList = mutableListOf<Album>()
        val db = readableDatabase
        val cursor = db.query(
            AlbumEntry.TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            null
        )

        if (cursor != null && cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndex(AlbumEntry._ID))
                val name = cursor.getString(cursor.getColumnIndex(AlbumEntry.COLUMN_NAME))
                val album = Album(id, name)
                albumList.add(album)
            } while (cursor.moveToNext())
        }

        cursor?.close()
        db.close()
        return albumList
    }

    fun getImagenesByAlbum(albumName: String): List<Imagen> {
        val imagenes = mutableListOf<Imagen>()
        val db = readableDatabase
        val cursor = db.query(
            ImagenEntry.TABLE_NAME,
            null,
            "${ImagenEntry.COLUMN_ALBUM_NAME} = ?",
            arrayOf(albumName),
            null,
            null,
            null
        )

        if (cursor != null && cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndex(ImagenEntry._ID))
                val image = cursor.getBlob(cursor.getColumnIndex(ImagenEntry.COLUMN_IMAGE))
                val description = cursor.getString(cursor.getColumnIndex(ImagenEntry.COLUMN_DESCRIPTION))
                val imagen = Imagen(id, albumName, image, description)
                imagenes.add(imagen)
            } while (cursor.moveToNext())
        }

        cursor?.close()
        db.close()
        return imagenes
    }


    companion object {
        const val DATABASE_NAME = "MyGallery.db"
        const val DATABASE_VERSION = 1
    }

    object AlbumEntry : BaseColumns {
        const val TABLE_NAME = "album"
        const val _ID = BaseColumns._ID
        const val COLUMN_NAME = "name"
    }

    object ImagenEntry : BaseColumns {
        const val TABLE_NAME = "imagenes"
        const val _ID = BaseColumns._ID
        const val COLUMN_ALBUM_NAME = "album_name"
        const val COLUMN_IMAGE = "image"
        const val COLUMN_DESCRIPTION = "description"
    }

}