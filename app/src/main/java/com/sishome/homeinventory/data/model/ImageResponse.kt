package com.sishome.homeinventory.data.model

import com.google.gson.annotations.SerializedName

class ImageResponse (
    @SerializedName("message") val message :String,
    @SerializedName("image") val image :String,
)