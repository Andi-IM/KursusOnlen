package my.id.airham.kursusonlen.util

import android.database.Cursor
import my.id.airham.kursusonlen.db.DatabaseContract
import my.id.airham.kursusonlen.entity.Participant

// objek ini berguna untuk mengubah (converting) bentuk cursor pada db
// menjadi list data object
object MappingHelper {
    fun mapCursorToArrayList(participantCursor: Cursor?): ArrayList<Participant> {
        val list = ArrayList<Participant>()

        participantCursor?.apply {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(DatabaseContract.ParticipantColumns._ID))
                val name =
                    getString(getColumnIndexOrThrow(DatabaseContract.ParticipantColumns.NAME))
                val address =
                    getString(getColumnIndexOrThrow(DatabaseContract.ParticipantColumns.ADDRESS))
                val phoneNumber =
                    getString(getColumnIndexOrThrow(DatabaseContract.ParticipantColumns.PHONE_NUMBER))
                val gender =
                    getString(getColumnIndexOrThrow(DatabaseContract.ParticipantColumns.GENDER))
                val latitude =
                    getDouble(getColumnIndexOrThrow(DatabaseContract.ParticipantColumns.LATITUDE))
                val longitude =
                    getDouble(getColumnIndexOrThrow(DatabaseContract.ParticipantColumns.LONGITUDE))
                val photoUri =
                    getString(getColumnIndexOrThrow(DatabaseContract.ParticipantColumns.PHOTO_URI))
                list.add(
                    Participant(
                        id,
                        name,
                        address,
                        phoneNumber,
                        gender,
                        latitude,
                        longitude,
                        photoUri
                    )
                )
            }
        }
        return list
    }
}