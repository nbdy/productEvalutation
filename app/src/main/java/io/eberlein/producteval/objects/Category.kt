package io.eberlein.producteval.objects

import androidx.room.*

@Entity
data class Category(
    @PrimaryKey(autoGenerate = true) val cid: Long,
    @ColumnInfo val name: String
){
    constructor(name: String): this(0, name)
}

data class CategoryWithProducts(
    @Embedded val category: Category,
    @Relation(
        parentColumn = "cid",
        entityColumn = "pid"
    )
    val products: List<Product>
)

@Dao
interface CategoryDao{
    @Transaction @Query("select * from category where cid = :cid") fun getCategoryWithProducts(cid: Long): List<CategoryWithProducts>
    @Query("select * from category") fun getAll(): List<Category>
    @Insert fun insert(category: Category)
    @Delete fun delete(category: Category)
}