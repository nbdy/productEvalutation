package io.eberlein.producteval.objects

import androidx.room.*

@Entity
data class Category(
    @PrimaryKey(autoGenerate = true) val cid: Long,
    @ColumnInfo val name: String
){
    constructor(name: String): this(0, name)
}

@Dao
interface CategoryDao{
    @Query("select * from category") fun getAll(): List<Category>
    @Insert fun insert(category: Category)
    @Delete fun delete(category: Category)
}
