package io.eberlein.producteval.objects

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Category::class, Product::class], version = 1, exportSchema = false)
abstract class DB : RoomDatabase() {
    abstract fun category(): CategoryDao
    abstract fun product(): ProductDao
}
