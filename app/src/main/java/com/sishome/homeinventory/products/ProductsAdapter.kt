package com.sishome.homeinventory.products

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sishome.homeinventory.R
import com.sishome.homeinventory.data.model.Productos
import com.sishome.homeinventory.data.model.ProductosItem

class ProductsAdapter(var products:List<ProductosItem>) : RecyclerView.Adapter<ProductsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder {
        //Creamos la vista que le vamos a pasar al viewHolder
        val view =LayoutInflater.from(parent.context).inflate(R.layout.item_product,parent,false)
        //devolvemos la vista (cada vez que se crea un item)
        return ProductsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return products.size
    }

    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int) {
        holder.render(products[position]);
    }
}