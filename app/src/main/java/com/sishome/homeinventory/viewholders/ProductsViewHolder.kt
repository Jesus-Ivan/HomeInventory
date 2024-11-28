package com.sishome.homeinventory.viewholders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sishome.homeinventory.R
import com.sishome.homeinventory.data.model.ProductosItem
import com.squareup.picasso.Picasso

class ProductsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    /**
     * Inicializar los componentes internos del item
     */
    private val tvNombre: TextView = view.findViewById(R.id.tvNombre)
    private val tvPrecio: TextView = view.findViewById(R.id.tvPrecio)
    private val tvObservaciones: TextView = view.findViewById(R.id.tvObservaciones)
    private val ivProducto: ImageView = view.findViewById(R.id.ivProducto)

    //Funcion de renderizado
    fun render(product: ProductosItem) {
        tvNombre.text = product.producto
        tvPrecio.text = product.precio_venta
        tvObservaciones.text = product.observaciones

        if(product.image == null){
            ivProducto.setImageResource(R.drawable.img_placeholder)
        }else if(product.image.isEmpty()){
            ivProducto.setImageResource(R.drawable.img_placeholder)
        }else{
            Picasso.get()
                .load(product.image)
                .error(R.drawable.img_error)
                .into(ivProducto)
        }
    }
}