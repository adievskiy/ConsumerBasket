package com.example.consumerbasket

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class ListAdapter(context: Context, productList: MutableList<Product>) :
    ArrayAdapter<Product>(context, R.layout.list_item, productList) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        val product = getItem(position)
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)
        }

        val productId = view?.findViewById<TextView>(R.id.productIdTV)
        val productNameTV = view?.findViewById<TextView>(R.id.productNameTV)
        val productPriceTV = view?.findViewById<TextView>(R.id.productPriceTV)
        val productWeightTV = view?.findViewById<TextView>(R.id.productWeightTV)
        val totalPriceTV = view?.findViewById<TextView>(R.id.totalPriceTV)

        productId?.text = product?.id.toString()
        productNameTV?.text = product?.productName
        productPriceTV?.text = product?.productPrice
        productWeightTV?.text = product?.productWeight
        totalPriceTV?.text = product?.totalPrice

        return view!!
    }
}