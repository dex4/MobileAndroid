package com.mobile.diastore.service

import android.os.Parcelable
import com.mobile.diastore.model.Entry
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.Deferred
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

@Parcelize
@JsonClass(generateAdapter = true)
data class EntryTest(
    @Json(name = "userId") val userId: Long,
    @Json(name = "id") val id: Long,
    @Json(name = "title") val title: String,
    @Json(name = "body") val body: String

) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class EntriesListDTO(
    @Json(name = "entriesList") val entriesList: List<Entry>
) : Parcelable

interface EntriesService {

    @PUT("/api/values/{id}")
    fun updateEntry(@Path("id") id: String, @Body entry: Entry): Deferred<Entry>

    @POST("/api/values/")
    fun addEntry(@Body entry: Entry): Deferred<Entry>

    @POST("/api/values/dto/")
    fun addMultipleEntries(@Body entries: EntriesListDTO): Deferred<EntriesListDTO>

    @GET("/api/values")
    fun getAllEntries(): Deferred<List<Entry>>

    @DELETE("/api/values/{id}")
    fun deleteEntryById(@Path("id") id: String): Deferred<Entry>
}