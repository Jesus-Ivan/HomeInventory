package com.sishome.homeinventory.edit_activities

import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
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
    private lateinit var etNombre: EditText
    private lateinit var etNombre2: EditText
    private lateinit var etPrecioCompra: EditText
    private lateinit var etPrecioVenta: EditText
    private lateinit var etCodigoBarra: EditText
    private lateinit var etOtros: EditText
    private lateinit var btnSave: Button
    private lateinit var btnCamera : ImageButton

    //Servicio de retrofit
    private lateinit var retrofitService: RetrofitService
    //Camara scaner
    private var barcodeLauncher : ActivityResultLauncher<ScanOptions>? = null

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
        //Listener del boton de camara
        btnCamera.setOnClickListener {
            barcodeLauncher!!.launch(ScanOptions().setOrientationLocked(false))
        }
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
            try {
                //llamar a retrofit
                val response: Response<ProductosItem> = retrofitService.crearProducto(product)

                if (response.isSuccessful) {
                    //Ejecutar acciones en la UI, en el hilo principal
                    runOnUiThread {
                        Toast.makeText(
                            this@NewProduct,
                            "Exito :" + response.body()?.id.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                        reset()     //Limpiar imputs
                    }
                } else {
                    throw Exception("Algo ha ido mal :C")
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this@NewProduct, e.message, Toast.LENGTH_SHORT).show()
                }
            }
            finally {
                //Ocultar el dialogo de carga
                runOnUiThread { dialog.hide() }
            }

        }
    }

    private fun reset() {
        etCodigoBarra.text.clear()
        etNombre2.text.clear()
        etOtros.text.clear()
        etPrecioCompra.setText("0")        //Precio de compra por defecto = 0
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
        etPrecioCompra.setText("0")        //Precio de compra por defecto = 0
        etPrecioVenta = findViewById(R.id.etPrecioVenta_new)
        etCodigoBarra = findViewById(R.id.etCodigoBarra_new)
        etOtros = findViewById(R.id.etOtros_new)
        btnSave = findViewById(R.id.btnSave_new)

        btnCamera = findViewById(R.id.btnCamera)
        /**
         * Inicializar el scaner
         */
        barcodeLauncher = registerForActivityResult<ScanOptions, ScanIntentResult>(
            ScanContract()
        ) { result: ScanIntentResult ->
            if (result.contents == null) {
                Toast.makeText(this@NewProduct, "Cancelled", Toast.LENGTH_LONG).show()
            } else {
                //Llenar el edit text
                etCodigoBarra.setText(result.contents)
            }
        }
    }
}