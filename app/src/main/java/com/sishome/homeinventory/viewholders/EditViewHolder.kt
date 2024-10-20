package com.sishome.homeinventory.viewholders

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sishome.homeinventory.R
import com.sishome.homeinventory.data.model.ProductosItem

class EditViewHolder (view: View) : RecyclerView.ViewHolder(view) {

    private val tvNombreProducto :TextView = view.findViewById(R.id.tvNombreProducto)
    private val tvCodigoBarra :TextView = view.findViewById(R.id.tvCodigoBarra)
    private val tvPrecioCompra :TextView = view.findViewById(R.id.tvPrecioCompra)
    private val tvPrecioVenta :TextView = view.findViewById(R.id.tvPrecioVenta)
    private val tvObservaciones :TextView = view.findViewById(R.id.tvObservaciones)

    fun render (product: ProductosItem){
        tvNombreProducto.text = product.producto
        tvCodigoBarra.text = product.codigo_barra
        tvPrecioCompra.text = product.precio_compra
        tvPrecioVenta.text = product.precio_venta
        tvObservaciones.text = product.observaciones
    }
}