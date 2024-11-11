package com.sishome.homeinventory.fragments

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.sishome.homeinventory.R
import com.sishome.homeinventory.adapters.EditAdapter
import com.sishome.homeinventory.data.RetrofitService
import com.sishome.homeinventory.data.RetrofitServiceFactory
import com.sishome.homeinventory.data.model.ProductosItem
import com.sishome.homeinventory.data.model.ProductosResponse
import com.sishome.homeinventory.edit_activities.NewProduct

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class EditFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


    //Variables referentes al UI
    private lateinit var search: SearchView;
    private lateinit var rvProductsGrid: RecyclerView
    private lateinit var fabAddProducto: FloatingActionButton
    private lateinit var pbProducts: ProgressBar

    //Componentes del dialog
    private lateinit var dConfirmDelete: Dialog
    private lateinit var tvProducto: TextView
    private lateinit var btnConfirmDelete: Button
    private lateinit var btnCancelDelete: Button
    private lateinit var tvDeleteMain: TextView
    private lateinit var llDeleteProcesing: LinearLayout

    //Servicio de retrofit
    private lateinit var retrofitService: RetrofitService

    //lista de productos
    private var products: MutableList<ProductosItem> = mutableListOf()
    private lateinit var productsAdapter: EditAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Creamos el servicio de retrofit, antes devolver la vista para el fragment
        retrofitService = RetrofitServiceFactory.makeRetrofitService()
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initComponents(view)
        initListeners(view)
    }

    private fun initComponents(view: View) {
        //Barra de busqueda
        search = view.findViewById(R.id.search)
        //Boton de añadir producto
        fabAddProducto = view.findViewById(R.id.fabAddProducto)
        //Progress bar
        pbProducts = view.findViewById(R.id.pbProducts)
        /**
         * Dialog de eliminacion
         */
        //Dialogo de eliminacion
        dConfirmDelete = Dialog(view.context)
        //Enganchar la vista al dialog
        dConfirmDelete.setContentView(R.layout.delete_dialog)
        //componentes internos del dialog
        tvProducto = dConfirmDelete.findViewById(R.id.tvConfirmDialog)
        btnConfirmDelete = dConfirmDelete.findViewById(R.id.btnConfirmDelete)
        btnCancelDelete = dConfirmDelete.findViewById(R.id.btnCancelDelete)
        //Titulo principal
        tvDeleteMain = dConfirmDelete.findViewById(R.id.tvDeleteMain)
        //Titulo secundario de carga
        llDeleteProcesing = dConfirmDelete.findViewById(R.id.llDeleteProcesing)


        /**
         * Recycler view
         */
        //Asignar los valores al adaptador
        productsAdapter = EditAdapter(products) { deleteProduct(it) }
        //  Iniciar el recyclerview
        rvProductsGrid = view.findViewById(R.id.rvProductsGrid)
        //definir el manejador del layouts del recyclerview
        rvProductsGrid.layoutManager = GridLayoutManager(view.context, 2)

        //configurar el recyclerview, con su adaptador
        rvProductsGrid.adapter = productsAdapter;

    }

    private fun initListeners(view: View) {
        //Boton de añadir nuevo producto
        fabAddProducto.setOnClickListener {
            val intent = Intent(view.context, NewProduct::class.java)
            startActivity(intent)
        }

        //Agregar listener de cancelar
        btnCancelDelete.setOnClickListener { dConfirmDelete.hide() }

        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    buscarProductos(newText);
                }
                return false
            }
        })

    }

    private fun buscarProductos(newText: String) {
        //Habilitar la progress bar
        pbProducts.isVisible = true
        /**
         * Lanzar una corrutina en un hilo secundario.
         * El alcance del hilo IO, es usado para procesos pesados o llamadas a BD
         */
        CoroutineScope(Dispatchers.IO).launch {
            try {
                //Llamada a la API
                val response: Response<ProductosResponse> =
                    retrofitService.obtenerProductos(newText)

                if (response.isSuccessful) {
                    //Estraer el cuerpo de la respuesta
                    val body: ProductosResponse? = response.body()
                    if (body != null) {
                        //Actualizamos la UI, en el hilo main
                        withContext(Dispatchers.Main) {
                            //Ocultar la progress bar
                            pbProducts.isVisible = false

                            // Actualizar la lista de usuarios
                            products.clear()
                            products.addAll(body.products)

                            // Notificar al adaptador
                            productsAdapter.notifyDataSetChanged()
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    //Ocultar la progress bar
                    pbProducts.isVisible = false
                    Toast.makeText(this@EditFragment.context, e.message, Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    private fun deleteProduct(position: Int) {
        //Ajustar el nombre
        tvProducto.text = products[position].producto
        //mostrar el dialogo
        dConfirmDelete.show()
        //Agregar listener de borrar
        btnConfirmDelete.setOnClickListener { confirmDelete(position) }
    }

    private fun confirmDelete(position: Int) {
        enableItemsDialog(false)        //Deshabilitar botones
        /**
         * Lanzar en corrutina
         */
        CoroutineScope(Dispatchers.IO).launch {
            try {
                //Ejecutar llamada a API
                val response: Response<Any> =
                    retrofitService.eliminarProducto(products[position].id)
                //Cuando se recibe la respuesta
                if (response.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        //Eliminar de la lista el item
                        products.removeAt(position)
                        //Eliminar del adapter
                        productsAdapter.notifyItemRemoved(position)
                    }
                } else {
                    throw Exception("Algo ha ido mal al borrar :C")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@EditFragment.context, e.message, Toast.LENGTH_SHORT).show()
                }
            }finally {
                withContext(Dispatchers.Main){
                    dConfirmDelete.hide()       //ocultar el cuadro de dialogo
                    enableItemsDialog(true) //Habilitar denuevo los botones
                }
            }
        }


    }

    /**
     * Habilita o deshabilita los componentes internos de dialog de eliminacion
     */
    private fun enableItemsDialog(enabled: Boolean) {
        //Ocultamos partes del dialog.
        tvDeleteMain.isVisible = enabled
        btnConfirmDelete.isVisible = enabled
        btnCancelDelete.isVisible = enabled

        //Habilitamos el menu de carga
        llDeleteProcesing.isVisible = !enabled
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment EditFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EditFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}