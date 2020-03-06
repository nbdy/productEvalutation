package io.eberlein.producteval.objects

import androidx.room.Database
import androidx.room.RoomDatabase
import java.security.MessageDigest


fun hash(input: ByteArray, algorithm: String): String {
    return MessageDigest.getInstance(algorithm).digest(input).fold("", {
            str, it -> str + "%02x".format(it)
    })
}

fun ByteArray.sha256(): String {
    return hash(this, "SHA-256")
}

@Database(entities = [Category::class, Product::class], version = 1, exportSchema = false)
abstract class DB : RoomDatabase() {
    abstract fun category(): CategoryDao
    abstract fun product(): ProductDao
}
