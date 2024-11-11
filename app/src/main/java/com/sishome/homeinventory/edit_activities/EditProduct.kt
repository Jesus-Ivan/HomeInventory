package com.sishome.homeinventory.edit_activities

import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.sishome.homeinventory.MainActivity.Companion.ID_PRODUCT_KEY
import com.sishome.homeinventory.R
import com.sishome.homeinventory.data.RetrofitService
import com.sishome.homeinventory.data.RetrofitServiceFactory
import com.sishome.homeinventory.data.model.ProductosItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class EditProduct : AppCompatActivity() {
    //Id del producto
    private var idProducto: Int = 0

    //Componentes de las vistas
    private lateinit var tvIdProducto: TextView
    private lateinit var etNombre: EditText
    private lateinit var edNombre2: EditText
    private lateinit var etPrecioCompra: EditText
    private lateinit var etPrecioVenta: EditText
    private lateinit var etCodigoBarra: EditText
    private lateinit var etOtros: EditText
    private lateinit var pbLoading: ProgressBar
    private lateinit var llEdit: LinearLayout
    private lateinit var btnSave: Button

    //Servicio de retrofit
    private lateinit var retrofitService: RetrofitService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_product)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Creamos el servicio de retrofit, antes devolver la vista para el fragment
        retrofitService = RetrofitServiceFactory.makeRetrofitService()

        initComponents()
        initUI()
        searchProduct()
        initListeners()
    }

    private fun initListeners() {
        btnSave.setOnClickListener {
            //Creamos un nuevo dialog
            val dialog = createDialog()
            //mostramos el dialogo
            dialog.show()

            //Lanzamos en una corrutina el proceso completo para actualizar
            CoroutineScope(Dispatchers.IO).launch {
                //Creamos el objeto a actualizar
                val producto: ProductosItem = ProductosItem(
                    etCodigoBarra.text.toString(),
                    idProducto,
                    edNombre2.text.toString(),
                    etOtros.text.toString(),
                    etPrecioCompra.text.toString(),
                    etPrecioVenta.text.toString(),
                    etNombre.text.toString()
                )
                //Hacer llamada a la api
                val response: Response<ProductosItem> =
                    retrofitService.actualizarProducto(idProducto, producto)
                //Mostramos en el hilo principal, el toast
                runOnUiThread {
                    if (response.isSuccessful) {
                        Toast.makeText(this@EditProduct, "Exito :D!", Toast.LENGTH_SHORT).show()
                        onBackPressed()
                    } else {
                        Toast.makeText(this@EditProduct, "Error :(", Toast.LENGTH_SHORT).show()
                    }
                    dialog.hide()
                }
            }
        }
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

    private fun searchProduct() {
        /**
         * Lanzamos corrutina de busqueda en hilo secundario
         */
        CoroutineScope(Dispatchers.IO).launch {
            //hacemos la llamada para obtener la informacion
            val response: Response<ProductosItem> = retrofitService.obtenerProducto(idProducto)
            if (response.isSuccessful) {
                //extraemos el cuerpo de la respuesta devuelta por la llamada a retrofit
                val product: ProductosItem? = response.body()
                if (product != null) {
                    //Actualizamos la UI, en el hilo main
                    runOnUiThread {
                        //Ocultar la progress bar
                        pbLoading.isVisible = false
                        //Mostrar la UI
                        llEdit.isVisible = true
                        btnSave.isVisible = true
                        //Settear los valores de los inputs
                        setValuesProduct(product)
                    }

                }
            }
        }
    }

    private fun setValuesProduct(product: ProductosItem) {
        etNombre.setText(product.producto)
        edNombre2.setText(product.nombre2)
        etPrecioCompra.setText(product.precio_compra)
        etPrecioVenta.setText(product.precio_venta)
        etCodigoBarra.setText(product.codigo_barra)
        etOtros.setText(product.observaciones)
    }

    private fun initComponents() {
        tvIdProducto = findViewById(R.id.tvIdProducto)
        etNombre = findViewById(R.id.etNombre)
        edNombre2 = findViewById(R.id.edNombre2)
        etPrecioCompra = findViewById(R.id.etPrecioCompra)
        etPrecioVenta = findViewById(R.id.etPrecioVenta)
        etCodigoBarra = findViewById(R.id.etCodigoBarra)
        etOtros = findViewById(R.id.etOtros)
        pbLoading = findViewById(R.id.pbLoading)
        llEdit = findViewById(R.id.llEdit)
        btnSave = findViewById(R.id.btnSave)
    }

    private fun initUI() {
        //Seteamos el titulo
        tvIdProducto.text = intent.extras?.getInt(ID_PRODUCT_KEY).toString()
        /**
         * guardamos el id provieniente del intent, una variable de la clase.
         * No es necesario,pero puede utilizarse directamente desde "intent.extras.getInt(ID_PRODUCT_KEY)"
         */
        idProducto = intent.extras?.getInt(ID_PRODUCT_KEY) ?: 0

    }
}