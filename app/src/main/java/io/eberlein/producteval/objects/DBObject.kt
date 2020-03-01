package io.eberlein.producteval.objects

import io.paperdb.Book
import io.paperdb.Paper
import java.util.*
import kotlin.collections.ArrayList


open class DBObject<T>(@Transient val BOOK: String = "default") {
    var id: String = UUID.randomUUID().toString()

    fun getBook(): Book {
        return Paper.book(BOOK)
    }

    fun save(){
        getBook().write(id, this)
    }

    fun delete(){
        getBook().delete(id)
    }

    fun get(id: String): T {
        return getBook().read<T>(id)
    }

    fun getAll(): MutableList<T> {
        val r: MutableList<T> = ArrayList()
        val b = getBook()
        for (i:String in b.allKeys) r.add(b.read(i))
        return r
    }

    fun getCount(): Int {
        return getBook().allKeys.size
    }
}