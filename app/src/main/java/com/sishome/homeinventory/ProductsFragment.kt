package com.sishome.homeinventory

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sishome.homeinventory.products.Product
import com.sishome.homeinventory.products.ProductsAdapter

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProductsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProductsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    //Variables del fragment
    private lateinit var rvProducts :RecyclerView
    private lateinit var productsAdapter: ProductsAdapter
    private lateinit var etInputSearch : EditText

    private val products = mutableListOf(
        Product(1,"AGUA","",0f,0.5f),
        Product(2,"AGUA CIEL","",0f,5f),
        Product(3,"AGUA MINERAL","",0f,9.5f),
        Product(4,"AGUA PEÑAFIEL","",0f,8.5f),
        Product(5,"AGUA MAGICA","",0f,5.9f),
        Product(6,"CHEETOS","",0f,25f),
        Product(7,"SABRITAS","",0f,15f),
    )

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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_products, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Creamos los componentes
        initComponents(view);
        initListeners()
    }
    
    private fun initComponents(view: View) {
        /**
         * Recycler view
         */
        //Asignar los valores al adaptador
        productsAdapter = ProductsAdapter(products)
        //  Iniciar el recyclerview
        rvProducts = view.findViewById(R.id.rvProducts)
        //definir el manejador del layouts del recyclerview
        rvProducts.layoutManager = LinearLayoutManager(rvProducts.context)
        //configurar el recyclerview, con su adaptador
        rvProducts.adapter = productsAdapter;

        /**
         * Campo de busqueda 
         */
        etInputSearch = view.findViewById(R.id.etInputSearch)
    }
    private fun initListeners() {

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProductsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProductsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}