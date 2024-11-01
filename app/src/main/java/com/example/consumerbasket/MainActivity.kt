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

class MainActivity : AppCompatActivity() {

    private val db = DBHelper(this, null)
    private var products: MutableList<Product> = mutableListOf()
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
            addProduct()
        }

        listViewLV.setOnItemClickListener { parent, _, position, _ ->
            val selectedProduct = parent.getItemAtPosition(position) as Product
            editDelDialog(selectedProduct)
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

    private fun editDelDialog(selectedProduct: Product) {
        val editDelDialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val editDelDialogView = inflater.inflate(R.layout.edit_del_dialog, null)
        editDelDialogBuilder.setView(editDelDialogView)
        val editBTN = editDelDialogView.findViewById<Button>(R.id.editBTN)
        val newDeleteBTN = editDelDialogView.findViewById<Button>(R.id.newDeleteBTN)
        val cancelBTN = editDelDialogView.findViewById<Button>(R.id.cancelBTN)
        val dialog = editDelDialogBuilder.create()

        editBTN.setOnClickListener {
            updateProductDialog(selectedProduct)
            dialog.dismiss()
        }

        newDeleteBTN.setOnClickListener {
            deleteProduct(selectedProduct)
            dialog.dismiss()
        }

        cancelBTN.setOnClickListener {
            dialog.dismiss()
        }


        dialog.show()
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
                    Product(
                        Integer.parseInt(id),
                        productName,
                        replaceProductPrice,
                        replaceProductWeight,
                        totalPrice
                    )
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

    private fun updateProductDialog(selectedProduct: Product) {
        val updateDialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val updateDialogView = inflater.inflate(R.layout.update_dialog, null)
        updateDialogBuilder.setView(updateDialogView)

        val editName = updateDialogView.findViewById<EditText>(R.id.editNameET)
        val editPrice = updateDialogView.findViewById<EditText>(R.id.editPriceET)
        val editWeight = updateDialogView.findViewById<EditText>(R.id.editWeightET)
        val cancelUpdBTN = updateDialogView.findViewById<Button>(R.id.cancelUpdBTN)
        val updateUpdBTN = updateDialogView.findViewById<Button>(R.id.updateUpdBTN)
        val dialog = updateDialogBuilder.create()

        updateUpdBTN.setOnClickListener {
            checkUpdateProduct(selectedProduct, editName, editPrice, editWeight, dialog)
        }

        cancelUpdBTN.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun checkUpdateProduct(
        selectedProduct: Product,
        editName: EditText,
        editPrice: EditText,
        editWeight: EditText,
        dialog: AlertDialog,
    ) {
        val id = selectedProduct.id
        var updatedName = editName.text.toString()
        var updatedPrice = editPrice.text.toString()
        var updatedWeight = editWeight.text.toString()
        if (editName.text.isEmpty()) {
            updatedName = selectedProduct.productName
            if (editPrice.text.isEmpty()) {
                updatedPrice = selectedProduct.productPrice
                if (editWeight.text.isEmpty()) {
                        Toast.makeText(this, "Заполните хотя бы одно поле", Toast.LENGTH_LONG).show()
                } else {
                    if (isValidNumber(editWeight.text.toString())) {
                        updatedWeight = selectedProduct.productWeight
                        updateProduct(updatedPrice, updatedWeight, updatedName, id, dialog)
                    } else Toast.makeText(this, "Вес - число", Toast.LENGTH_LONG).show()
                }
            } else {
                if (editWeight.text.isEmpty()) {
                    if (isValidNumber(editPrice.text.toString())) {
                        updatedWeight = selectedProduct.productWeight
                        updateProduct(updatedPrice, updatedWeight, updatedName, id, dialog)
                    } else Toast.makeText(this, "Цена - число", Toast.LENGTH_LONG).show()
                } else {
                    if (isValidNumber(editPrice.text.toString()) &&
                            isValidNumber(editWeight.text.toString())
                    ) {
                        updateProduct(updatedPrice, updatedWeight, updatedName, id, dialog)
                    } else Toast.makeText(this, "Цена и вес - число", Toast.LENGTH_LONG).show()
                }
            }
        } else if (editPrice.text.isEmpty()) {
            updatedPrice = selectedProduct.productPrice
            if (editWeight.text.isEmpty()) {
                updatedWeight = selectedProduct.productWeight
                updateProduct(updatedPrice, updatedWeight, updatedName, id, dialog)
            } else {
                if (isValidNumber(editWeight.text.toString())) {
                    updateProduct(updatedPrice, updatedWeight, updatedName, id, dialog)
                } else Toast.makeText(this, "Вес - число", Toast.LENGTH_LONG).show()
            }
        } else if (editWeight.text.isEmpty()) {
            updatedWeight = selectedProduct.productWeight
            if (isValidNumber(editPrice.text.toString())) {
                updateProduct(updatedPrice, updatedWeight, updatedName, id, dialog)
            } else Toast.makeText(this, "Цена - число", Toast.LENGTH_LONG).show()
        } else {
            if (isValidNumber(editPrice.text.toString()) &&
                isValidNumber(editWeight.text.toString())
            ) {
                updateProduct(updatedPrice, updatedWeight, updatedName, id, dialog)
            } else {
                Toast.makeText(this, "Цена и вес - числа", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun updateProduct(
        updatedPrice: String,
        updatedWeight: String,
        updatedName: String,
        id: Int,
        dialog: AlertDialog,
    ) {
        val replacedUpdPrice = updatedPrice.replace(",", ".")
        val replacedUpdWeight = updatedWeight.replace(",", ".")
        val updatedTotalPrice =
            (replacedUpdPrice.toDouble() * replacedUpdWeight.toDouble())
        if (updatedName.trim() != "" && updatedPrice.trim() != "" && updatedWeight.trim() != "") {
            val product =
                Product(
                    id,
                    updatedName,
                    replacedUpdPrice,
                    replacedUpdWeight,
                    updatedTotalPrice.toString()
                )
            db.updateProduct(product)
            reloadView()
            dialog.dismiss()
        }
    }

    private fun deleteProduct(selectedProduct: Product) {
        val deleteDialogBuilder = AlertDialog.Builder(this)


        deleteDialogBuilder.setTitle("Внимание!")
        deleteDialogBuilder.setMessage("Удалить запись?")
        deleteDialogBuilder.setPositiveButton("Удалить") { _, _ ->
            val id = selectedProduct.id
            val product =
                Product(id, "", "", "", "")
            db.deleteProduct(product)
            reloadView()
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