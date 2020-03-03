package io.eberlein.producteval.objects

import androidx.room.*

@Entity
data class Product(
    @PrimaryKey(autoGenerate = true) val pid: Long,
    @ColumnInfo var name:String,
    @ColumnInfo val code:String,
    @ColumnInfo val codeType:String,
    @ColumnInfo var rating:Int,
    @ColumnInfo var description:String,
    @ColumnInfo var image:String?,
    @ColumnInfo var imageRotation:Float
){
    constructor(code:String, codeType: String): this(0, "", code, codeType, 0, "", null, 0F)
}

@Dao
interface ProductDao{
    @Insert suspend fun insert(product: Product)
    @Update suspend fun update(product: Product)
    @Delete suspend fun delete(product: Product)
}