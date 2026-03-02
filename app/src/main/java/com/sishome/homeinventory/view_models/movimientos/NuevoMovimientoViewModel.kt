package com.sishome.homeinventory.view_models.movimientos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sishome.homeinventory.data.RetrofitService
import com.sishome.homeinventory.data.RetrofitServiceFactory

class MovimientoViewModel: ViewModel() {

    //Servicio de retrofit
    private var retrofitService: RetrofitService

    private var _fecha = MutableLiveData<String>()
    val fecha: LiveData<String> get() = _fecha;

    private var _concepto = MutableLiveData<String>()
    val concepto: LiveData<String> get() = _concepto;

    private var _monto = MutableLiveData<String>()
    val monto: LiveData<String> get() = _monto;

    private var _uriImage = MutableLiveData<String>()
    val uriImage: LiveData<String> get() = _uriImage;

    init {
        //Creamos el servicio de retrofit
        retrofitService = RetrofitServiceFactory.makeRetrofitService();
    }

    fun crearMovimiento(fecha: String, concepto: String, monto: String, tipoMovimiento: String){

    }

}