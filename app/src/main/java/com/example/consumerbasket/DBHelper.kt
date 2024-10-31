package com.example.consumerbasket

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "PRODUCT_DATABASE"
        private const val DATABASE_VERSION = 1
        const val TABLE_NAME = "product_table"
        const val KEY_ID = "id"
        const val KEY_NAME = "name"
        const val KEY_PRICE = "price"
        const val KEY_WEIGHT = "weight"
        const val KEY_TOTAL = "total"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val query = ("CREATE TABLE " + TABLE_NAME + " (" +
                KEY_ID + " INTEGER PRIMARY KEY, " +
                KEY_NAME + " TEXT," +
                KEY_PRICE + " TEXT," +
                KEY_WEIGHT + " TEXT," +
                KEY_TOTAL + " TEXT" + ")")
        db.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
    }

    fun addProduct(product: Product) {
        val values = ContentValues()
        values.put(KEY_ID, product.id)
        values.put(KEY_NAME, product.productName)
        values.put(KEY_PRICE, product.productPrice)
        values.put(KEY_WEIGHT, product.productWeight)
        values.put(KEY_TOTAL, product.totalPrice)
        val db = this.writableDatabase
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    @SuppressLint("Range")
    fun getProduct(): MutableList<Product> {
        val productList: MutableList<Product> = mutableListOf()
        val selectQuery = "SELECT * FROM $TABLE_NAME"
        val db = this.readableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return productList
        }
        var id: Int
        var productName: String
        var productPrice: String
        var productWeight: String
        var totalPrice: String
        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndex("id"))
                productName = cursor.getString(cursor.getColumnIndex("name"))
                productWeight = cursor.getString(cursor.getColumnIndex("weight"))
                productPrice = cursor.getString(cursor.getColumnIndex("price"))
                totalPrice = cursor.getString(cursor.getColumnIndex("total"))
                val product = Product(id, productName, productPrice, productWeight, totalPrice)
                productList.add(product)
            } while (cursor.moveToNext())
        }
        return productList
    }

    fun eraseDB() {
        val db = this.writableDatabase
        db.delete(TABLE_NAME, null, null)

    }

    fun updateProduct(product: Product) {
        val db = writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_ID, product.id)
        contentValues.put(KEY_NAME, product.productName)
        contentValues.put(KEY_PRICE, product.productPrice)
        contentValues.put(KEY_WEIGHT, product.productWeight)
        contentValues.put(KEY_TOTAL, product.totalPrice)
        db.update(TABLE_NAME, contentValues, "id=" + product.id, null)
        db.close()
    }

    fun isIdExists(id: Int): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM $TABLE_NAME WHERE $KEY_ID = ?", arrayOf(id.toString()))
        cursor.use {
            if (it.moveToFirst()) {
                return it.getInt(0) > 0
            }
        }
        return false
    }

    fun deleteProduct(product: Product) {
        val db = writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_ID, product.id)
        db.delete(TABLE_NAME, "id=" + product.id, null)
        db.close()
    }
}