package com.example.consumerbasket

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.w3c.dom.Text
import java.util.zip.Inflater

class MainActivity : AppCompatActivity() {

    private val db = DBHelper(this, null)
    private var products: MutableList<Product> = mutableListOf()
    private lateinit var toolbarMain: Toolbar
    private lateinit var productNameET: EditText
    private lateinit var productWeightET: EditText
    private lateinit var productPriceET: EditText
    private lateinit var addToBasketBTN: Button
    private lateinit var listViewLV: ListView
    private lateinit var updateBTN: Button
    private lateinit var deleteBTN: Button

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
            addProduct()
        }

        updateBTN.setOnClickListener {
            updateProduct()
        }

        deleteBTN.setOnClickListener {
            deleteProduct()
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
        updateBTN = findViewById(R.id.updateBTN)
        deleteBTN = findViewById(R.id.deleteBTN)
    }

    private fun addProduct() {
        if (productNameET.text.isEmpty() || productPriceET.text.isEmpty() || productWeightET.text.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_LONG).show()
        } else {
            if (isValidNumber(productPriceET.text.toString()) && isValidNumber(productWeightET.text.toString())) {
                val id = IdGenerator(db).addId()
                val productName = productNameET.text.toString()
                val productWeight = productWeightET.text.toString()
                val replaceProductWeight = productWeight.replace(",", ".")
                val productPrice = productPriceET.text.toString()
                val replaceProductPrice = productPrice.replace(",", ".")
                val totalPrice =
                    (replaceProductWeight.toDouble() * replaceProductPrice.toDouble()).toString()
                val product =
                    Product(Integer.parseInt(id), productName, productPrice, productWeight, totalPrice)
                db.addProduct(product)
                productNameET.text.clear()
                productWeightET.text.clear()
                productPriceET.text.clear()
                reloadView()
            } else {
                Toast.makeText(this, "Цена и вес - числа", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun updateProduct() {
        val updateDialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val updateDialogView = inflater.inflate(R.layout.update_dialog, null)
        updateDialogBuilder.setView(updateDialogView)

        val editId = updateDialogView.findViewById<EditText>(R.id.editIdET)
        val editName = updateDialogView.findViewById<EditText>(R.id.editNameET)
        val editPrice = updateDialogView.findViewById<EditText>(R.id.editPriceET)
        val editWeight = updateDialogView.findViewById<EditText>(R.id.editWeightET)

        updateDialogBuilder.setTitle("Обновить запись")
        updateDialogBuilder.setMessage("Заполните поля:")
        updateDialogBuilder.setPositiveButton("Обновить") { _, _ ->
            val id = editId.text.toString()
            val updatedName = editName.text.toString()
            val updatedPrice = editPrice.text.toString()
            val updatedWeight = editWeight.text.toString()
            val updatedTotalPrice =
                (updatedPrice.toDouble() * updatedWeight.toDouble()).toString()
            if (id.trim() != "" && updatedName.trim() != "" && updatedPrice.trim() != "" && updatedWeight.trim() != "") {
                val product =
                    Product(id.toInt(), updatedName, updatedPrice, updatedWeight, updatedTotalPrice)
                db.updateProduct(product)
                reloadView()
            }
        }
        updateDialogBuilder.setNegativeButton("Отмена") { _, _ ->
        }
        updateDialogBuilder.create().show()
    }

    private fun deleteProduct() {
        val deleteDialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.delete_dialog, null)
        deleteDialogBuilder.setView(dialogView)

        val chooseDeleteId = dialogView.findViewById<EditText>(R.id.deleteET)
        deleteDialogBuilder.setTitle("Удалить запись?")
        deleteDialogBuilder.setMessage("Введите идентификатор:")
        deleteDialogBuilder.setPositiveButton("Обновить") { _, _ ->
            val id = chooseDeleteId.text.toString()
            if (id.trim() != "") {
                val product =
                    Product(id.toInt(), "", "", "", "")
                db.deleteProduct(product)
                reloadView()
            }
        }
        deleteDialogBuilder.setNegativeButton("Отмена") { _, _ ->
        }
        deleteDialogBuilder.create().show()
    }

    private fun reloadView() {
        products = db.getProduct()
        val listAdapter = ListAdapter(this@MainActivity, products)
        listViewLV.adapter = listAdapter
        listAdapter.notifyDataSetChanged()
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
                reloadView()
                Toast.makeText(this, "БД очищена", Toast.LENGTH_LONG).show()
            }

            R.id.menuExit -> finishAffinity()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        reloadView()
        super.onResume()
    }
}