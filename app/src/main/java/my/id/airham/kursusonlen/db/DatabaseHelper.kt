package my.id.airham.kursusonlen.db

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import my.id.airham.kursusonlen.db.DatabaseContract.ParticipantColumns
import my.id.airham.kursusonlen.db.DatabaseContract.ParticipantColumns.Companion.TABLE_NAME

internal class DatabaseHelper(context : Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "kursusonlendb"

        private const val DATABASE_VERSION = 1

        private const val SQL_CREATE_TABLE_PARTICIPANT = "CREATE TABLE $TABLE_NAME" +
                " (${ParticipantColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
                " ${ParticipantColumns.NAME} TEXT NOT NULL," +
                " ${ParticipantColumns.ADDRESS} TEXT NOT NULL," +
                " ${ParticipantColumns.GENDER} TEXT NOT NULL," +
                " ${ParticipantColumns.PHONE_NUMBER} TEXT NOT NULL," +
                " ${ParticipantColumns.LATITUDE} DOUBLE NOT NULL," +
                " ${ParticipantColumns.LONGITUDE} DOUBLE NOT NULL," +
                " ${ParticipantColumns.PHOTO_URI} TEXT NOT NULL)"
    }

    // membuat database
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_TABLE_PARTICIPANT)
    }

    // menghapus table (semacam rollback table) dan membangun kembali database dengan table baru
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

}