package com.example.consumerbasket

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private val db = DBHelper(this, null)
    private var product: MutableList<Product> = mutableListOf()
    private lateinit var toolbarMain: Toolbar
    private lateinit var productNameET: EditText
    private lateinit var productWeightET: EditText
    private lateinit var productPriceET: EditText
    private lateinit var addToBasketBTN: Button
    private lateinit var listViewLV: ListView


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        init()

        addToBasketBTN.setOnClickListener {
            if (productNameET.text.isEmpty() || productPriceET.text.isEmpty() || productWeightET.text.isEmpty()) {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_LONG).show()
            } else {
                if (isValidNumber(productPriceET.text.toString()) && isValidNumber(productWeightET.text.toString())) {
                    val productName = productNameET.text.toString()
                    val productWeight = productWeightET.text.toString()
                    val replaceProductWeight = productWeight.replace(",", ".")
                    val productPrice = productPriceET.text.toString()
                    val replaceProductPrice = productPrice.replace(",", ".")
                    val totalPrice =
                        (replaceProductWeight.toDouble() * replaceProductPrice.toDouble()).toString()
                    db.addProduct(
                        productName,
                        replaceProductPrice,
                        replaceProductWeight,
                        totalPrice
                    )
                    product = db.getProduct()
                    val listAdapter = ListAdapter(this@MainActivity, product)
                    listViewLV.adapter = listAdapter
                } else {
                    Toast.makeText(this, "Цена и вес - числа", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun init() {
        toolbarMain = findViewById(R.id.toolbarMain)
        setSupportActionBar(toolbarMain)
        title = "Потребительская корзина"
        productNameET = findViewById(R.id.productNameET)
        productWeightET = findViewById(R.id.productWeightET)
        productPriceET = findViewById(R.id.productPriceET)
        addToBasketBTN = findViewById(R.id.addToBasketBTN)
        listViewLV = findViewById(R.id.listViewLV)
    }

    private fun isValidNumber(number: String): Boolean {
        val regex = Regex("^[0-9.,]*$")
        return regex.matches(number)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_item, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.eraseDB -> {
                db.eraseDB()
                Toast.makeText(this, "БД очищена", Toast.LENGTH_LONG).show()
            }
            R.id.menuExit -> finishAffinity()
        }
        return super.onOptionsItemSelected(item)
    }
}