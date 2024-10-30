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

    fun addProduct(name: String, price: String, weight: String, totalPrice: String) {
        val values = ContentValues()
        values.put(KEY_NAME, name)
        values.put(KEY_PRICE, price)
        values.put(KEY_WEIGHT, weight)
        values.put(KEY_TOTAL, totalPrice)
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
        var productName: String
        var productPrice: String
        var productWeight: String
        var totalPrice: String
        if (cursor.moveToFirst()) {
            do {
                productName = cursor.getString(cursor.getColumnIndex("name"))
                productWeight = cursor.getString(cursor.getColumnIndex("weight"))
                productPrice = cursor.getString(cursor.getColumnIndex("price"))
                totalPrice = cursor.getString(cursor.getColumnIndex("total"))
                val product = Product(productName, productPrice, productWeight, totalPrice)
                productList.add(product)
            } while (cursor.moveToNext())
        }
        return productList
    }

    fun eraseDB() {
        val db = this.writableDatabase
        db.delete(TABLE_NAME, null, null)
    }
}