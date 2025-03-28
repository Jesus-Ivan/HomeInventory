package com.sishome.homeinventory.edit_activities

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import com.sishome.homeinventory.MainActivity.Companion.ID_PRODUCT_KEY
import com.sishome.homeinventory.R
import com.sishome.homeinventory.data.RetrofitService
import com.sishome.homeinventory.data.RetrofitServiceFactory
import com.sishome.homeinventory.data.model.ImageResponse
import com.sishome.homeinventory.data.model.ProductosItem
import com.squareup.picasso.Picasso
import id.zelory.compressor.Compressor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

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
    private lateinit var btnCamera: ImageButton
    private lateinit var ivProduct: ImageView            //Imageview del producto
    private lateinit var btnAddImageGalery: ImageButton //Boton para agregar imagen desde la galeria
    private lateinit var btnCleanImage: ImageButton     //Boton para limpiar la imagen
    private lateinit var btnAddImageCamera: ImageButton //Botono para agregar imagen desde la camara


    //Servicio de retrofit
    private lateinit var retrofitService: RetrofitService

    //Camara scaner
    private var barcodeLauncher: ActivityResultLauncher<ScanOptions>? = null

    //Uri's auxiliares para la actualizacion de la imagen
    private var urlOriginalImage: String = ""
    private var uriNewImage: Uri? = null

    //Contract para escoger una imagen de galeria
    val imageGalery = registerForActivityResult(ActivityResultContracts.GetContent()) {
        if (it != null) {
            uriNewImage = it
            Picasso.get().load(it).into(ivProduct)
        }
    }

    //Contract para tomar una foto
    private val takePicture =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { result ->
            if (result) {
                Picasso.get().load(uriNewImage).into(ivProduct)
            }
        }

    val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                takePicture.launch(uriNewImage!!)
            } else {
                Toast.makeText(this, ":( necesito la camara porfa", Toast.LENGTH_SHORT).show()
            }
        }

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
                    etNombre.text.toString(),
                    urlOriginalImage
                )
                //Crear la imagen nueva de forma local
                val partImage = createPartImage()

                try {
                    //Crear la imagen en el servidor
                    if (partImage != null) {
                        //Hacer peticion para crear la imagen
                        val imageResponseNew: Response<ImageResponse> =
                            retrofitService.crearImagen(partImage)
                        //Si hay respuesta exitosa
                        if (imageResponseNew.isSuccessful)
                            producto.image = imageResponseNew.body()?.image ?: ""
                        else
                            throw Exception(imageResponseNew.message())
                    }


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

                    }

                } catch (e: Exception) {
                    runOnUiThread {
                        Toast.makeText(this@EditProduct, e.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        //Listener del boton de camara
        btnCamera.setOnClickListener {
            barcodeLauncher!!.launch(ScanOptions().setOrientationLocked(false))
        }

        //Listener para eliminar la imagen del imageview
        btnCleanImage.setOnClickListener {
            urlOriginalImage = ""     //Limpiar la url original
            uriNewImage = null          //limpiar la uri nueva
            //Limpiar el imageview y establecerle la URI de la nueva imagen
            Picasso.get().load(uriNewImage).into(ivProduct)
        }

        //Listener del boton de galeria
        btnAddImageGalery.setOnClickListener {
            imageGalery.launch("image/*")
        }

        //Listener del boton de camara
        btnAddImageCamera.setOnClickListener {
            //Creamos el Uri de destino
            uriNewImage = createImageUri()
            //Verificamos si se cuenta con los permisos
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                //lanzar actividad de la camara
                takePicture.launch(uriNewImage!!)
            } else {
                //Solicitar permisos
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }

        }
    }

    private fun createImageUri(): Uri {
        val image = File(filesDir, "camera_photos.png")
        return FileProvider.getUriForFile(this, "com.sishome.homeinventory.FileProvider", image)
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

    private suspend fun createPartImage(): MultipartBody.Part? {
        if (uriNewImage != null) {
            val filesDir = applicationContext.filesDir
            val file = File(filesDir, "image.png")
            val inputStream = contentResolver.openInputStream(uriNewImage!!)
            val outputStream = FileOutputStream(file)
            inputStream!!.copyTo(outputStream)
            val compressedImageFile = Compressor.compress(this, file)

            val requestBody = RequestBody.create(MediaType.parse("image/*"), compressedImageFile)
            val part = MultipartBody.Part.createFormData(
                "imageProduct",
                compressedImageFile.name,
                requestBody
            )
            return part
        } else {
            return null
        }
    }


    private fun searchProduct() {
        /**
         * Lanzamos corrutina de busqueda en hilo secundario
         */
        CoroutineScope(Dispatchers.IO).launch {
            try {
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
                } else {
                    throw Exception(response.message())
                }
            } catch (e: Exception) {
                runOnUiThread {
                    pbLoading.isVisible = false
                    Toast.makeText(this@EditProduct, e.message, Toast.LENGTH_LONG).show()
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
        if (product.image.isNotEmpty() && product.image != null) {
            Picasso.get()
                .load(product.image)
                .error(R.drawable.img_error)
                .into(ivProduct)
            urlOriginalImage = product.image
        }

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
        btnCamera = findViewById(R.id.btnCamera)

        ivProduct = findViewById(R.id.ivProducto)
        btnAddImageGalery = findViewById(R.id.btnAddImageFromGalery)
        btnCleanImage = findViewById(R.id.btnCleanImage)
        btnAddImageCamera = findViewById(R.id.btnAddImageFromCamera)
        /**
         * Inicializar el scaner
         */
        barcodeLauncher = registerForActivityResult<ScanOptions, ScanIntentResult>(
            ScanContract()
        ) { result: ScanIntentResult ->
            if (result.contents == null) {
                Toast.makeText(this@EditProduct, "Cancelled", Toast.LENGTH_LONG).show()
            } else {
                //Llenar el edit text
                etCodigoBarra.setText(result.contents)
            }
        }
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