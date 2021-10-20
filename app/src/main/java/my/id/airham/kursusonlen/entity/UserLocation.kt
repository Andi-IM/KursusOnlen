package my.id.airham.kursusonlen.entity

import java.io.Serializable

data class UserLocation(
    var latitude: Double? = null,
    var longitude: Double? = null
) : Serializable
