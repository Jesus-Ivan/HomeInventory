package com.sishome.homeinventory.fragments

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.sishome.homeinventory.MainActivity.Companion.ID_CUSTOMER_KEY
import com.sishome.homeinventory.R

import com.sishome.homeinventory.adapters.customers.CustomerAdapter
import com.sishome.homeinventory.customers.ArchivedCustomers
import com.sishome.homeinventory.customers.EditCustomer
import com.sishome.homeinventory.customers.NewCustomer
import com.sishome.homeinventory.data.RetrofitService
import com.sishome.homeinventory.data.RetrofitServiceFactory

import com.sishome.homeinventory.data.model.customers.CustomersResponse
import com.sishome.homeinventory.data.model.customers.ItemCustomer
import com.sishome.homeinventory.databinding.FragmentCustomersBinding
import com.sishome.homeinventory.movimientos.Movimientos
import com.sishome.homeinventory.view_models.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CustomersFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CustomersFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentCustomersBinding? = null;
    private val binding get() = _binding!!;


    //Adaptador del recyclerview
    private lateinit var customerAdapter: CustomerAdapter

    //Servicio de retrofit
    private lateinit var retrofitService: RetrofitService

    // Importante que sea 'activityViewModels'
    private val viewModel: MainViewModel by activityViewModels()

    private var estado_cliente = "1";

    // 1. Registramos el contrato para crear/editar cliente
    private val customerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // 3. Este bloque se ejecuta cuando regresas de la Activity (callback)
        if (result.resultCode == Activity.RESULT_OK) {
            buscarCliente(
                "",
                estado_cliente
            )// Si el resultado fue OK, recargamos la lista de la API
        }
    }


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
        _binding = FragmentCustomersBinding.inflate(inflater, container, false);
        return binding.root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI();
        initComponents(view);
        initListeners()
        buscarCliente(binding.search.query.toString(), estado_cliente);
    }

    private fun initListeners() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            buscarCliente(binding.search.query.toString(), estado_cliente);
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun initUI() {
        //Utilizado para inectar los botones al ToolBar
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Inflamos el menú específico de este fragmento
                menuInflater.inflate(R.menu.menu_customers, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Manejamos los clics de los nuevos botones
                return when (menuItem.itemId) {
                    R.id.action_nuevo_cliente -> {
                        // 2. Lanzamos la Activity usando el launcher.
                        val intent = Intent(requireContext(), NewCustomer::class.java)
                        customerLauncher.launch(intent)
                        true
                    }

                    R.id.action_archivados_cliente -> {
                        when (estado_cliente) {
                            "1" -> {
                                estado_cliente = "0"
                                //Cambiar el título de la Toolbar
                                viewModel.updateTitle(getString(R.string.tittle_customer_archived))
                                buscarCliente("", estado_cliente)

                            }

                            "0" -> {
                                estado_cliente = "1"
                                //Cambiar el título de la Toolbar
                                viewModel.updateTitle(getString(R.string.customers))
                                buscarCliente("", estado_cliente)
                            }
                        }
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
        // ^ El estado RESUMED asegura que los botones solo se vean cuando el fragmento es visible
    }

    private fun initComponents(view: View) {
        /**
         * Recycler view
         */
        //Asignar los valores al adaptador
        customerAdapter = CustomerAdapter { item, action ->
            when (action) {
                "EDIT" -> {
                    // Navegar a pantalla de edición, mediante un launcher
                    val intent = Intent(requireContext(), EditCustomer::class.java).apply {
                        putExtra("EXTRA_CUSTOMER", item);
                    }
                    customerLauncher.launch(intent) //Lanzar activity, con un launcher. (contract)
                }

                "ARCHIVE" -> {
                    archivarCliente(item, "0")
                }

                "DES" -> {
                    archivarCliente(item, "1")
                }

                "MOV" -> {
                    val intent = Intent(requireContext(), Movimientos::class.java);
                    //Preparar el bundle de datos, para pasar al intent
                    val datos = Bundle();
                    val itemCustomer: ItemCustomer = item
                    //Agregar propiedades
                    datos.putString("id", itemCustomer.id)
                    datos.putString("apodo", itemCustomer.apodo);
                    datos.putString("saldo", itemCustomer.sumaTotalMovimientos);
                    //Agregar los datos al intent
                    intent.putExtras(datos)
                    customerLauncher.launch(intent) //Lanzar activity, con un launcher. (contract)
                }
            };
        }
        //definir el manejador del layouts del recyclerview
        binding.rvCustomers.layoutManager = LinearLayoutManager(this.context)
        //configurar el recyclerview, con su adaptador
        binding.rvCustomers.adapter = customerAdapter;
        /**
         * SearchView
         */
        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                buscarCliente(query.orEmpty(), estado_cliente)
                //Limpiar el campo de busqueda
                binding.search.setQuery("", false)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false;
            }

        })
    }

    private fun buscarCliente(query: String, estado: String) {
        //Activar las animaciones de carga
        enableLoadingState(true)
        /**
         * Lanzar una corrutina en un hilo secundario.
         * El alcance del hilo IO, es usado para procesos pesados o llamadas a BD
         */
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response: Response<CustomersResponse> =
                    retrofitService.obtenerClientes(estado = estado, nombre = query, apodo = query);

                if (response.isSuccessful) {
                    val body: CustomersResponse? = response.body()
                    if (body != null) {
                        //Actualizamos la UI, en el hilo main
                        withContext(Dispatchers.Main) {
                            //Desactivar animaciones de carga
                            enableLoadingState(false)

                            // Enviamos la nueva lista al adaptador
                            customerAdapter.submitList(body.list)

                            binding.rvCustomers.clearFocus()
                        }
                    }
                }
            } catch (e: Exception) {
                // Manejar la excepción, en el hilo principal
                withContext(Dispatchers.Main) {
                    //Mostrar toast de error
                    Toast.makeText(this@CustomersFragment.context, e.message, Toast.LENGTH_SHORT)
                        .show()
                    //Desactivar animaciones de carga
                    enableLoadingState(false)
                }
            }
        }
    }

    private fun enableLoadingState(status: Boolean) {
        binding.llConsultando.isVisible = status;
        binding.search.isVisible = !status
        binding.rvCustomers.isVisible = !status
    }

    private fun archivarCliente(itemCustomer: ItemCustomer, estado: String) {

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response =
                    retrofitService.archivarCliente(itemCustomer.id!!, EstadoRequest(estado))

                if (response.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        // 1. Obtenemos la lista actual del adaptador y creamos una copia mutable
                        val currentList = customerAdapter.currentList.toMutableList()
                        // 2. Removemos el objeto directamente (sin depender solo del índice)
                        currentList.remove(itemCustomer)
                        // 3. Enviamos la nueva lista. DiffUtil calculará la animación de borrado sola.
                        customerAdapter.submitList(currentList)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "No se pudo archivar", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    data class EstadoRequest(val estado: String)

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CustomersFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CustomersFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}

