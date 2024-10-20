package com.sishome.homeinventory.viewholders

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sishome.homeinventory.R
import com.sishome.homeinventory.data.model.ProductosItem

class ProductsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    /**
     * Inicializar los componentes internos del item
     */
    private val tvNombre:TextView = view.findViewById(R.id.tvNombre)
    private val tvPrecio:TextView = view.findViewById(R.id.tvPrecio)
    private val tvObservaciones:TextView = view.findViewById(R.id.tvObservaciones)

    //Funcion de renderizado
    fun render(product: ProductosItem) {
        tvNombre.text = product.producto
        tvPrecio.text = product.precio_venta
        tvObservaciones.text = product.observaciones
    }
}