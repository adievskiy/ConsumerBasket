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
                        productPrice,
                        productWeight,
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
            updateProduct(selectedProduct, editName, editPrice, editWeight, dialog)
        }

        cancelUpdBTN.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun updateProduct(
        selectedProduct: Product,
        editName: EditText,
        editPrice: EditText,
        editWeight: EditText,
        dialog: AlertDialog
    ) {
        if (editName.text.isEmpty() || editPrice.text.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_LONG).show()
        } else {
            if (isValidNumber(editPrice.text.toString()) &&
                isValidNumber(editWeight.text.toString())
            ) {
                val id = selectedProduct.id
                val updatedName = editName.text.toString()
                val updatedPrice = editPrice.text.toString()
                val replacedUpdPrice = updatedPrice.replace(",", ".")
                val updatedWeight = editWeight.text.toString()
                val replacedUpdWeight = updatedWeight.replace(",", ".")
                val updatedTotalPrice =
                    (replacedUpdPrice.toDouble() * replacedUpdWeight.toDouble())
                if (updatedName.trim() != "" && updatedPrice.trim() != "" && updatedWeight.trim() != "") {
                    val product =
                        Product(
                            id,
                            updatedName,
                            updatedPrice,
                            updatedWeight,
                            updatedTotalPrice.toString()
                        )
                    db.updateProduct(product)
                    reloadView()
                    dialog.dismiss()
                }
            } else {
                Toast.makeText(this, "id, вес и цена - числа", Toast.LENGTH_LONG).show()
            }
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