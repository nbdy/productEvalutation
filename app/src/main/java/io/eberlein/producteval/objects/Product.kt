package io.eberlein.producteval.objects

import androidx.room.*

@Entity
data class Product(
    @PrimaryKey(autoGenerate = true) val pid: Long,
    @ColumnInfo val cid:Long,
    @ColumnInfo var name:String,
    @ColumnInfo val code:String,
    @ColumnInfo val codeType:String,
    @ColumnInfo var rating:Int,
    @ColumnInfo var description:String,
    @ColumnInfo var image:String?,
    @ColumnInfo var imageRotation:Float
){
    constructor(cid:Long, code:String, codeType: String): this(0, cid,"", code, codeType, 0, "", null, 0F)
}

@Dao
interface ProductDao{
    @Query("select * from product where cid = :cid") suspend fun getProductsOfCategory(cid: Long): List<Product>
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(product: Product)
    @Delete suspend fun delete(product: Product)
}