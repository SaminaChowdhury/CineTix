package com.example.cinetix.Models

import android.os.Parcel
import android.os.Parcelable



data class Film(
    var id: String? = null,
    var Title: String? = null,
     var Description: String? = null,
     var Poster: String? = null,
    var bookmarked: Boolean = false,
     var Time: String? = null,
     var Trailer: String? = null,
     var Imdb: Int=0,
     var Year: Int=0,
     var price: Double=0.0,
     var Genre: ArrayList<String>? = ArrayList(),
     var Casts: ArrayList<Cast>? = ArrayList()
) : Parcelable {
 constructor(parcel: Parcel) : this() {
  id = parcel.readString()
  Title = parcel.readString()
  Description = parcel.readString()
  Poster = parcel.readString()

  Time = parcel.readString()
  Trailer = parcel.readString()
  Imdb = parcel.readInt()
  Year = parcel.readInt()
  price = parcel.readDouble()
  Genre = parcel.createStringArrayList()?: ArrayList()
  Casts = parcel.createTypedArrayList(Cast.CREATOR)?: ArrayList()

 }

 override fun writeToParcel(parcel: Parcel, flags: Int) {
  parcel.writeString(id)
  parcel.writeString(Title)
  parcel.writeString(Description)
  parcel.writeString(Poster)
  parcel.writeString(Time)
  parcel.writeString(Trailer)
  parcel.writeInt(Imdb)
  parcel.writeInt(Year)
  parcel.writeDouble(price)
  parcel.writeStringList(Genre)
  parcel.writeTypedList(Casts)

 }

 override fun describeContents(): Int {
  return 0
 }

 companion object CREATOR : Parcelable.Creator<Film> {
  override fun createFromParcel(parcel: Parcel): Film {
   return Film(parcel)
  }

  override fun newArray(size: Int): Array<Film?> {
   return arrayOfNulls(size)
  }
 }


}