package com.sishome.homeinventory.view_models

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sishome.homeinventory.customers.NewCustomer
import com.sishome.homeinventory.data.RetrofitService
import com.sishome.homeinventory.data.RetrofitServiceFactory
import com.sishome.homeinventory.data.model.customers.Customer
import com.sishome.homeinventory.data.model.customers.NewCustomerResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CustomerViewModel : ViewModel() {
    // LiveData privado para evitar modificaciones externas
    private val _resultadoPost = MutableLiveData<NewCustomerResponse>()

    // LiveData público para que la Activity lo observe
    val resultadoPost: LiveData<NewCustomerResponse> get() = _resultadoPost

    private val _customer = MutableLiveData<Customer>()
    val customer: LiveData<Customer> get() = _customer

    //Servicio de retrofit
    private var retrofitService: RetrofitService

    init {
        //Creamos el servicio de retrofit
        retrofitService = RetrofitServiceFactory.makeRetrofitService();
        //Creamos de objeto Customer
        if (_customer.value == null) {
            _customer.value = Customer(
                nombre = "",
                apodo = "",
                contra = "",
                estado = "1"
            );
        }
    }

    fun updateNombre(nombre: String) {
        _customer.value?.nombre = nombre;
    }

    fun updateApodo(apodo: String) {
        _customer.value?.apodo = apodo;
    }

    fun updateContra(contra: String) {
        _customer.value?.contra = contra;
    }


    fun crearCliente() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val res = retrofitService.crearCliente(customer.value!!)
                withContext(Dispatchers.Main) {
                    if (res.isSuccessful) {
                        //Limpiar valores
                        updateNombre("");
                        updateApodo("");
                        updateContra("");
                    }
                    //Asignar el resultado de la consulta
                    _resultadoPost.value = res.body()
                }
            } catch (e: Exception) {
                var body =
                    NewCustomerResponse(_customer.value!!, e.message.toString(), false, 500);
                withContext(Dispatchers.Main) {
                    _resultadoPost.value = body;
                }
            }
        }
    }
}