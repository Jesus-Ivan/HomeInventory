package com.sishome.homeinventory.edit_activities

import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.sishome.homeinventory.R
import com.sishome.homeinventory.data.RetrofitService
import com.sishome.homeinventory.data.RetrofitServiceFactory
import com.sishome.homeinventory.data.model.ProductosItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class NewProduct : AppCompatActivity() {
    /**
     * Variables correspondientes a los componentes
     */
    private lateinit var etNombre :EditText
    private lateinit var etNombre2 :EditText
    private lateinit var etPrecioCompra :EditText
    private lateinit var etPrecioVenta :EditText
    private lateinit var etCodigoBarra :EditText
    private lateinit var etOtros :EditText
    private lateinit var btnSave :Button

    //Servicio de retrofit
    private lateinit var retrofitService: RetrofitService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_new_product)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initComponents()
        initListeners()
        //Creamos el servicio de retrofit
        retrofitService = RetrofitServiceFactory.makeRetrofitService()
    }

    private fun initListeners() {
        btnSave.setOnClickListener { saveProduct() }
    }

    private fun saveProduct() {
        //Creamos un nuevo dialog
        val dialog = createDialog()
        //mostramos el dialogo
        dialog.show()

        CoroutineScope(Dispatchers.IO).launch {
            //Crear el objeto con las propiedades
            val product = ProductosItem(
                etCodigoBarra.text.toString(),
                0,
                etNombre2.text.toString(),
                etOtros.text.toString(),
                etPrecioCompra.text.toString(),
                etPrecioVenta.text.toString(),
                etNombre.text.toString()
            )
            //llamar a retrofit
            val response: Response<ProductosItem> = retrofitService.crearProducto(product)

            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    Toast.makeText(this@NewProduct, "Exito :"+ response.body()?.id.toString(), Toast.LENGTH_SHORT).show()
                    reset()
                } else {
                    Toast.makeText(this@NewProduct, "Error :(", Toast.LENGTH_SHORT).show()
                }
                dialog.hide()
            }
        }
    }

    private fun reset() {
        etCodigoBarra.text.clear()
        etNombre2.text.clear()
        etOtros.text.clear()
        etPrecioCompra.text.clear()
        etPrecioVenta.text.clear()
        etNombre.text.clear()
        etNombre.text.clear()
    }


    private fun createDialog(): Dialog {
        //Crear el dialogo
        val dialog: Dialog = Dialog(this)
        //Enganchar la vista al dialogo
        dialog.setContentView(R.layout.loading_dialog)
        /**
         * Como este dialogo solo es inidicador de carga, no es necesario hacer nada mas.
         */
        return dialog
    }

    private fun initComponents() {
        etNombre = findViewById(R.id.etNombre_new)
        etNombre2 = findViewById(R.id.etNombre2_new)
        etPrecioCompra = findViewById(R.id.etPrecioCompra_new)
        etPrecioVenta = findViewById(R.id.etPrecioVenta_new)
        etCodigoBarra = findViewById(R.id.etCodigoBarra_new)
        etOtros = findViewById(R.id.etOtros_new)
        btnSave = findViewById(R.id.btnSave_new)
    }
}