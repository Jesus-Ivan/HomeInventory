package com.sishome.homeinventory.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
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
import com.sishome.homeinventory.adapters.ProductsAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/*
    * Definimos un "atributo estatico"
    * que nos permita almacenar las llaves de las variables que se ponen en los Extras,
    * al lanzar una activity
    * */


class EditFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


    //Variables referentes al UI
    private lateinit var search: SearchView;
    private lateinit var rvProductsGrid: RecyclerView
    private lateinit var fabAddProducto :FloatingActionButton
    private lateinit var pbProducts : ProgressBar
    //Servicio de retrofit
    private lateinit var retrofitService: RetrofitService

    //lista de productos
    private var products : MutableList<ProductosItem> = mutableListOf()
    private lateinit var productsAdapter : EditAdapter



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

    private fun initComponents(view: View){
        //Barra de busqueda
        search = view.findViewById(R.id.search)
        //Boton de añadir producto
        fabAddProducto = view.findViewById(R.id.fabAddProducto)
        //Progress bar
        pbProducts = view.findViewById(R.id.pbProducts)

        /**
         * Recycler view
         */
        //Asignar los valores al adaptador
        productsAdapter = EditAdapter(products)
        //  Iniciar el recyclerview
        rvProductsGrid  = view.findViewById(R.id.rvProductsGrid)
        //definir el manejador del layouts del recyclerview
        rvProductsGrid.layoutManager = GridLayoutManager(view.context,2)

        //configurar el recyclerview, con su adaptador
        rvProductsGrid.adapter = productsAdapter;

    }
    private fun initListeners(view: View) {
        //Boton de añadir nuevo producto
        fabAddProducto.setOnClickListener {
            val intent = Intent(view.context, NewProduct::class.java)
            startActivity(intent)
        }

        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText !=null){
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
            val response: Response<ProductosResponse> = retrofitService.obtenerProductos(newText)
            //println(response)
            if (response.isSuccessful) {
                val body: ProductosResponse? = response.body()
                if (body != null) {
                    //Actualizamos la UI, en el hilo main
                    withContext(Dispatchers.Main) {
                        //Ocultar la progress bar
                        pbProducts.isVisible =false

                        // Actualizar la lista de usuarios
                        products.clear()
                        products.addAll(body.products)

                        // Notificar al adaptador
                        productsAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    private fun changedItem(position: Int, info:ProductosItem){

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