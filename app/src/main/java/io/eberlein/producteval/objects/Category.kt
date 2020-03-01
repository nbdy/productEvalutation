package io.eberlein.producteval.objects

class Category(val name: String, private val products: MutableList<String>): DBObject<Category>("category") {
    constructor(): this("", ArrayList())
    constructor(name: String): this(name, ArrayList())

    fun getProductObjects(): MutableList<Product> {
        val r: MutableList<Product> = ArrayList()
        val b = getBook()
        for(p:String in products) r.add(b.read(p))
        return r
    }

    fun addProduct(product: Product) {
        if(!products.contains(product.id)) products.add(product.id); save()
    }

    fun removeProduct(product: Product) {
        products.remove(product.id)
        save()
    }
}