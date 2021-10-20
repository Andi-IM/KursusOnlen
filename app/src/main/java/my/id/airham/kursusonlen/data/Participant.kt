package my.id.airham.kursusonlen.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Participant(
    var id: Int = 0,
    var name: String? = null,
    var address: String? = null,
    var phoneNumber: String? = null,
    var gender : String? = null,
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var photoUri: String? = null,
) : Parcelable
