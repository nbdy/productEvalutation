package io.eberlein.producteval.objects

import androidx.room.*

@Entity
data class Product(
    @ColumnInfo val cid:Long,
    @ColumnInfo val code:String,
    @ColumnInfo val codeType:String,
    @PrimaryKey(autoGenerate = true) val pid: Long = 0,
    @ColumnInfo var name:String = "",
    @ColumnInfo var rating:Int = 0,
    @ColumnInfo var description:String = "",
    @ColumnInfo var image:String? = null,
    @ColumnInfo var imageRotation:Float = 0F
)

@Dao
interface ProductDao{
    @Query("select * from product where cid = :cid") fun getProductsOfCategory(cid: Long): List<Product>
    @Query("select * from product where code = :code") fun getByCode(code: String): Product?
    @Query("select * from product where pid = :pid") fun getById(pid: Long): Product
    @Insert(onConflict = OnConflictStrategy.REPLACE) fun insert(product: Product): Long
    @Delete fun delete(product: Product)
}