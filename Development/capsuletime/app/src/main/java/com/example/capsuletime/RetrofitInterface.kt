package com.example.capsuletime

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface RetrofitInterface {
    // input & output interface
    @FormUrlEncoded// form url encode
    @POST("/users/auth") // url
    fun requestLogin(
            // input definition
            @Field("user_id") user_id:String,
            @Field("password") password:String
    ) : Call<Logined>

    @FormUrlEncoded// form url encode
    @POST("/users/logout") // url
    fun requestLogout(
            @Field("nick_name") nick_name: String
    ) : Call<Success>

    @FormUrlEncoded// form url encode
    @POST("/users/") // url
    fun requestPostUser(
            // input definition
            @Field("user_id") user_id:String,
            @Field("password") password:String,
            @Field("nick_name") nick_name:String,
            @Field("first_name") first_name:String,
            @Field("last_name") last_name:String,
            @Field("email") email: String
    ) : Call<Success>

    @Multipart
    @POST("/users/with/image")
    fun requestPostUserWithImage(
            @Part("user_id") user_id: RequestBody,
            @Part("password") password: RequestBody,
            @Part("nick_name") nick_name: RequestBody,
            @Part("first_name") first_name: RequestBody,
            @Part("last_name") last_name: RequestBody,
            @Part("email") email: RequestBody,
            @Part file: MultipartBody.Part
    ) : Call<Success>

    @GET("/capsules/user")
    fun requestSearchUserCapsule(
            @Query("user_id") user_id: String
    ): Call<List<Capsule>>

    @DELETE("/capsules/{capsule_id}")
    fun requestDeleteCapsule(
            @Path("capsule_id") capsule_id: Int
    ): Call<Success>

    @DELETE("/follow")
    fun requestDeleteFollow(
            @Query("nick_name") nick_name: String,
            @Query("dest_nick_name") dest_nick_name: String
    ): Call<Success>

    @FormUrlEncoded
    @POST("/follow")
    fun requestCreateFollow(
            @Field("nick_name") nick_name: String,
            @Field("dest_nick_name") dest_nick_name: String
    ): Call<Success>

    @Multipart
    @PUT ("/capsules/with/images")
    fun requestPutCapsuleWithImages(
            @Part("capsule_id") capsule_id: RequestBody,
            @Part("title") title: RequestBody,
            @Part("text") text: RequestBody,
            @Part file: List<MultipartBody.Part>
    ) : Call<Success>

    @FormUrlEncoded
    @PUT ("/capsules")
    fun requestPutCapsule(
            // input definition
            @Field("capsule_id") capsule_id: Int,
            @Field("title") title: String,
            @Field("text") text: String
    ) : Call<Success>

    @FormUrlEncoded
    @PUT ("/capsules/lock")
    fun requestPutLockCapsule(
            // input definition
            @Field("capsule_id") capsule_id: Int,
            @Field("title") title: String,
            @Field("text") text: String,
            @Field("expire") expire: String,
            @Field("members") members: List<String>?
    ) : Call<Success>

    @Multipart
    @PUT ("/capsules/lock/images")
    fun requestPutLockCapsuleWithImages(
            @Part("capsule_id") capsule_id: RequestBody,
            @Part("title") title: RequestBody,
            @Part("text") text: RequestBody,
            @Part("expire") expire: RequestBody,
            @Part("members") members: List<String>,
            @Part file: List<MultipartBody.Part>
    ) : Call<Success>


    @GET ("/users/{user_id}")
    fun requestUserData (
            @Path("user_id") user_id: String
    ): Call<User>

    @GET("/users/nick/{nick_name}")
    fun requestSearchUser(
            @Path("nick_name") nick_name: String
    ):Call <User>


    @GET("/capsules/nick/{nick_name}")
    fun requestSearchUserNick(
            @Path("nick_name") nick_name: String
    ):Call<List<Capsule>>

    @GET("/comment/list/{capsule_id}")
    fun requestComment(
            @Path("capsule_id") capsule_id: Int
    ):Call<List<CommnetLogData>>

    @GET("/comment/list/{capsule_id}")
    fun requestComment2(
            @Path("capsule_id") capsule_id: Int
    ):Call<List<Commnetwo>>

    @GET("/follow/followlist/{nick_name}")
    fun requestFollow(
            @Path("nick_name") nick_name: String
    ):Call<List<User>>

    @GET("/follow/followerlist/{nick_name}")
    fun requestFollower(
            @Path("nick_name") nick_name: String
    ):Call<List<User>>

    @GET("/follow/forfollow/list/{nick_name}")
    fun requestF4F(
            @Path("nick_name") nick_name: String
    ):Call<List<User>>

    @GET("/users")
    fun requestAllUser(
    ):Call<List<User>>


    @PUT("/users/")
    fun settingUser(
            /*@Part("pre_nick_name") pre_nick_name: RequestBody,
            @Part("password") password: RequestBody,
            @Part("new_nick_name") new_nick_name: RequestBody,
            @Part file: MultipartBody.Part*/
            @Body setting: Setting
    ):Call<Success>

    @PUT("/users/nick")
    fun settingPassword(
            @Body setting: SettingPassword
    ):Call<Success>

    @PUT("/users/nick")
    fun settingNick(
            @Body setting: SettingNick
    ):Call<Success>

    @Multipart
    @PUT("/users/image")
    fun settingUser2(
            @Part("pre_nick_name") pre_nick_name: RequestBody,
            @Part("password") password: RequestBody,
            @Part("new_nick_name") new_nick_name: RequestBody,
            @Part file: MultipartBody.Part
    ):Call<Success>

    @Multipart
    @PUT("/users/image/nick")
    fun settingUser3(
            @Part("pre_nick_name") pre_nick_name: RequestBody,
            @Part("new_nick_name") new_nick_name: RequestBody,
            @Part file: MultipartBody.Part
    ):Call<Success>

    @Multipart
    @PUT("/users/only/image")
    fun settingImage(
            @Part("nick_name") nick_name: RequestBody,
            @Part file: MultipartBody.Part
    ):Call<Success>

    @GET("/capsules/")
    fun requestAllCapsules():Call <List<CapsuleOneOfAll>>

    @GET("/capsules/")
    fun requestUserCapsules()
            :Call <List<CapsuleOneOfAll>>

    @GET("/session/")
    fun requestSessionAuth()
            :Call <Logined>

}