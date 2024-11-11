package com.sishome.homeinventory.view_models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sishome.homeinventory.data.RetrofitService
import com.sishome.homeinventory.data.RetrofitServiceFactory
import com.sishome.homeinventory.data.model.ProductosItem

class EditProductViewModel : ViewModel() {
    //Id del producto
    private lateinit var _idProducto : String
    //Objeto editable
    private lateinit var _productoEditable: ProductosItem
    //Servicio de retrofit
    private var retrofitService: RetrofitService

    //objeto livedata
    val currentProduct :MutableLiveData<ProductosItem> by lazy {
        MutableLiveData<ProductosItem>()
    }


    val idProducto :String
        get() = _idProducto
    val productoEditable:ProductosItem
        get() = _productoEditable

    init {
        //Creamos el servicio de retrofit
        retrofitService = RetrofitServiceFactory.makeRetrofitService()
    }

}