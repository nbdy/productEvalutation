package io.eberlein.producteval.objects

import android.graphics.Bitmap

class Product(
    var name:String,
    val code:String,
    val codeType:String,
    var rating:Int,
    var description:String,
    var image: Bitmap?,
    var imageRotation: Float
):
    DBObject<Product>("product") {
    constructor(code:String, codeType: String): this("", code, codeType, 0, "", null, 0F)
    constructor(): this("", "", "", 0, "", null, 0F)
}