package my.id.airham.kursusonlen.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import my.id.airham.kursusonlen.db.DatabaseContract.ParticipantColumns.Companion.TABLE_NAME
import my.id.airham.kursusonlen.db.DatabaseContract.ParticipantColumns.Companion._ID
import java.sql.SQLException

class ParticipantHelper(context: Context) {
    private var databaseHelper = DatabaseHelper(context)
    private lateinit var database: SQLiteDatabase

    companion object {
        private const val DATABASE_TABLE = TABLE_NAME

        // membuat singleton participant helper
        private var INSTANCE: ParticipantHelper? = null
        fun getInstance(context: Context): ParticipantHelper =
            INSTANCE ?: synchronized(this){
                INSTANCE ?: ParticipantHelper(context)
            }
    }

    // membuka database
    @Throws(SQLException::class)
    fun open(){
        database = databaseHelper.writableDatabase
    }

    // menutup database
    fun close(){
        databaseHelper.close()

        // apakah database masih terbuka?
        if (database.isOpen) databaseHelper.close()
    }

    // mengambil semua data participant
    fun queryAll(): Cursor {
        return database.query(
            DATABASE_TABLE,
            null,
            null,
            null,
            null,
            null,
            "$_ID ASC")
    }

    // mengambil data berdasarkan id dari item
    fun queryById(id: String) : Cursor {
        return database.query(
            DATABASE_TABLE,
            null,
            "$_ID = ?",
            arrayOf(id),
            null,
            null,
            null,
            null)
    }

    // menyimpan data
    fun insert(values: ContentValues?) : Long {
        return database.insert(DATABASE_TABLE, null, values)
    }

    // memperbarui data
    fun update(id: String, values: ContentValues?) : Int {
        return database.update(DATABASE_TABLE, values, "$_ID = ?", arrayOf(id))
    }

    // menghapus data
    fun deleteById(id: String) : Int {
        return database.delete(DATABASE_TABLE, "$_ID = '$id'", null)
    }
}