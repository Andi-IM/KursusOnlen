package my.id.airham.kursusonlen.data

import java.io.Serializable

data class UserLocation(
    var latitude: Double? = null,
    var longitude: Double? = null
) : Serializable
