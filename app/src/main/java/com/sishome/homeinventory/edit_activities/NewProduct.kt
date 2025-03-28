package com.sishome.homeinventory.edit_activities

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions

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
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream


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
    private lateinit var btnCamera: ImageButton
    private lateinit var ivProducto: ImageView
    private lateinit var llNew: LinearLayout
    private lateinit var llLoading: LinearLayout
    private lateinit var btnAddImageGalery: ImageButton
    private lateinit var btnCleanImage: ImageButton
    private lateinit var btnAddImageCamera: ImageButton

    //Servicio de retrofit
    private lateinit var retrofitService: RetrofitService

    //Camara scaner
    private var barcodeLauncher: ActivityResultLauncher<ScanOptions>? = null

    //Uri de la imagen seleccionada
    private var uriImageProduct: Uri? = null

    //Contract para escoger una imagen de galeria
    val contract = registerForActivityResult(ActivityResultContracts.GetContent()) {
        if (it != null) {
            uriImageProduct = it
            ivProducto.setImageURI(it)
        }
    }

    //Contract para tomar una foto
    private val takePicture =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { result ->
            if (result) {
                ivProducto.setImageURI(uriImageProduct)
            }
        }

    val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                takePicture.launch(uriImageProduct!!)
            } else {
                Toast.makeText(this, ":( necesito la camara porfa", Toast.LENGTH_SHORT).show()
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        //Listener para agregar la imagen de la galeria
        btnAddImageGalery.setOnClickListener {
            //Limpiamos el Uri de la imagen
            uriImageProduct = null
            //Lanzamos la actividad
            contract.launch("image/*")
        }

        btnAddImageCamera.setOnClickListener {
            //Creamos el Uri de destino
            uriImageProduct = createImageUri()
            //Verificamos si se cuenta con los permisos
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                //lanzar actividad de la camara
                takePicture.launch(uriImageProduct!!)
            } else {
                //Solicitar permisos
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }

        //Listener para eliminar la imagen
        btnCleanImage.setOnClickListener {
            ivProducto.setImageURI(null)
            uriImageProduct = null
        }

    }

    private fun createImageUri(): Uri {
        val image = File(filesDir, "camera_photos.png")
        return FileProvider.getUriForFile(this, "com.sishome.homeinventory.FileProvider", image)
    }

    private fun saveProduct() {
        //Habilitamos el estado de carga
        enableLoadingState(true)

        CoroutineScope(Dispatchers.IO).launch {
            //Crear el objeto con las propiedades
            val product = ProductosItem(
                etCodigoBarra.text.toString(),
                0,
                etNombre2.text.toString(),
                etOtros.text.toString(),
                etPrecioCompra.text.toString(),
                etPrecioVenta.text.toString(),
                etNombre.text.toString(),
            )

            try {
                val partImage = createPartImage()

                //Crear la imagen en el servidor
                if (partImage != null) {
                    val imageResponse: Response<ImageResponse> =
                        retrofitService.crearImagen(partImage)
                    if (imageResponse.isSuccessful)
                        product.image = imageResponse.body()?.image ?: ""
                }

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
                    throw Exception(response.message())
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this@NewProduct, e.message, Toast.LENGTH_SHORT).show()
                }
            } finally {
                //Deshabilitar estado carga
                runOnUiThread { enableLoadingState(false) }
            }

        }
    }

    private suspend fun createPartImage(): MultipartBody.Part? {
        if (uriImageProduct != null) {
            val filesDir = applicationContext.filesDir
            val file = File(filesDir, "image.png")
            val inputStream = contentResolver.openInputStream(uriImageProduct!!)
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

    private fun enableLoadingState(enable: Boolean) {
        //Ocultar el linearLayout
        llNew.isVisible = !enable
        //Ocultar el boton
        btnSave.isVisible = !enable
        llLoading.isVisible = enable

    }

    private fun reset() {
        etCodigoBarra.text.clear()
        etNombre2.text.clear()

        etOtros.text.clear()
        etPrecioCompra.setText("0")        //Precio de compra por defecto = 0
        etPrecioVenta.text.clear()
        etNombre.text.clear()
        etNombre.text.clear()

        //Limpiar la imagen
        ivProducto.setImageURI(null)
        uriImageProduct = null
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
        ivProducto = findViewById(R.id.ivProducto)
        llNew = findViewById(R.id.llNew)
        llLoading = findViewById(R.id.loading_state)
        btnAddImageGalery = findViewById(R.id.btnAddImageFromGalery)
        btnCleanImage = findViewById(R.id.btnCleanImage)
        btnAddImageCamera = findViewById(R.id.btnAddImageFromCamera)

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