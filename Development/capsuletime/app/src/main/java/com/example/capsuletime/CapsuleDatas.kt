package com.example.capsuletime

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parceler
import kotlinx.android.parcel.Parcelize
import okhttp3.MultipartBody

data class Success (
        val success: String
)

data class Logined(
        val nick_name: String?
)

data class Content (
        val content_id: Int?,
        val url: String?
)

data class Capsule (
        val capsule_id: Int,
        val user_id: String,
        val nick_name: String?,
        val title: String,
        val text: String?,
        val likes: Int,
        val views: Int,
        val date_created: String,
        val date_opened: String,
        val status_temp: Int,
        val lat: Double,
        val lng: Double,
        val content: List<Content>?
)

data class CapsuleLogData (
        val user_id: String,
        val capsule_id: Int,
        val d_day: String,
        val iv_url: String,
        val tv_title: String,
        val tv_text: String?,
        val tv_tags: String,
        val tv_created_date: String,
        val tv_opened_date: String,
        val tv_location: String,
        val state_temp: Int,
        val contentList: List<Content>?
)

data class CommnetLogData(
        val nick_name: String,
        val comment : String,
        val date_created: String,
        val date_updated: String,
        val replies : List<Commnetwo>,
        val viewtype : Int,
        val user_image_url : String
)

data class Commnetwo(
        val nick_name: String?,
        val comment : String?,
        val date_created: String?,
        val date_updated: String?,
        val viewtype : Int,
        val user_image_url : String
)


data class FollowLogData (
        val nick_name: String,
        val first_name: String,
        val last_name: String?,
        val iv_url: String
)

data class User(
        var user_id: String?,
        var nick_name: String?,
        var first_name: String?,
        var last_name: String?,
        var email_id: String?,
        var email_domain: String?,
        var date_created: String?,
        var date_updated: String?,
        var image_url: String?,
        var image_name: String?) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(user_id)
        parcel.writeString(nick_name)
        parcel.writeString(first_name)
        parcel.writeString(last_name)
        parcel.writeString(email_id)
        parcel.writeString(email_domain)
        parcel.writeString(date_created)
        parcel.writeString(date_updated)
        parcel.writeString(image_url)
        parcel.writeString(image_name)
    }
    override fun describeContents(): Int {
        return 0;
    }
    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}

data class Setting(
        var pre_nick_name: String,
        var password: String,
        var new_nick_name: String
)

data class cap (
        var capsule_id: Int,
        var user_id: String,
        var nick_name: String,
        var title: String,
        var text: String,
        var likes: Int,
        var views: Int,
        var date_created: String,
        var date_opened: String,
        var status_temp: Int,
        var lat: Double,
        var lng: Double
)

