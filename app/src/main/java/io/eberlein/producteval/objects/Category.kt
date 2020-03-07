package io.eberlein.producteval.objects

import androidx.room.*

@Entity
data class Category(
    @ColumnInfo var name: String,
    @PrimaryKey(autoGenerate = true) val cid: Long = 0
)

@Dao
interface CategoryDao{
    @Query("select * from category") fun getAll(): List<Category>
    @Query("select * from category where cid = :cid") fun getById(cid: Long): Category
    @Insert(onConflict = OnConflictStrategy.REPLACE) fun insert(category: Category): Long
    @Delete fun delete(category: Category)
}
