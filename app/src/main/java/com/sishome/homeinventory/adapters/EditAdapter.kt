package com.sishome.homeinventory.adapters

import android.app.Dialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.sishome.homeinventory.MainActivity.Companion.ID_PRODUCT_KEY
import com.sishome.homeinventory.R
import com.sishome.homeinventory.data.model.ProductosItem
import com.sishome.homeinventory.edit_activities.EditProduct
import com.sishome.homeinventory.viewholders.EditViewHolder

class EditAdapter(private var products: List<ProductosItem>, val deleteProduct: (Int) -> Unit) :
    RecyclerView.Adapter<EditViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EditViewHolder {
        //Creamos la vista que le vamos a pasar al viewHolder
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_edit, parent, false)
        //devolvemos la vista (cada vez que se crea un item)
        return EditViewHolder(view)
    }

    override fun getItemCount() = products.size

    override fun onBindViewHolder(holder: EditViewHolder, position: Int) {
        holder.render(products[position]);

        //Añadir el listener de click
        holder.itemView.setOnClickListener {
            //Creamos intent
            val intent = Intent(holder.itemView.context, EditProduct::class.java)
            //Agregamos el id del producto al intent
            intent.putExtra(ID_PRODUCT_KEY, products[position].id)
            //Iniciamos la activity
            startActivity(holder.itemView.context, intent, null)
        }

        //el segundo listener para eliminar
        holder.itemView.setOnLongClickListener {
            deleteProduct(position)
            false
        }
    }
}