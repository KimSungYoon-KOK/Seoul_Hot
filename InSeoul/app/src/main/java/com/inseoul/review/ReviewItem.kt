package com.inseoul.review

import android.graphics.drawable.Drawable
import com.google.android.gms.maps.model.LatLng

data class reviewSummary(
    var list:ArrayList<LatLng>
)
data class reviewInfo(
    val title:String,
    val date:String,
    val userId:String,
    val coverImg:String
)
data class ReviewItem(      // 내용
    val info:reviewInfo?,
    val summary:reviewSummary?,
    var type:Int,

    val id:Int,
    val reviewHash:ArrayList<String>?,
    var write:ArrayList<String>?,

    val num:Int,
    val upso_name:String,
    var hashTag:String?,
    var imageList:ArrayList<String?>?,
    var review_content:String?,
    val posX:Double,
    val posY:Double,
    val location:String,
    val call:String,
    var like:Int,
    var dislike:Int
){

}
