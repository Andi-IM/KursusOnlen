package my.id.airham.kursusonlen.db

import android.provider.BaseColumns

internal class DatabaseContract {
    // mengurus table participant
    internal class ParticipantColumns : BaseColumns {
        companion object {
            const val TABLE_NAME = "participant"
            const val _ID = "_id"
            const val NAME = "name"
            const val ADDRESS = "address"
            const val PHONE_NUMBER = "phone_number"
            const val GENDER = "gender"
            const val LATITUDE = "latitude"
            const val LONGITUDE = "longitude"
            const val PHOTO_URI = "photo_uri"
        }
    }
}