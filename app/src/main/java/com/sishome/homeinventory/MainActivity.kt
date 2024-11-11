package com.sishome.homeinventory

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentContainerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sishome.homeinventory.fragments.CustomersFragment
import com.sishome.homeinventory.fragments.EditFragment
import com.sishome.homeinventory.fragments.ProductsFragment
import com.sishome.homeinventory.view_models.MainViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var bnPrincipal: BottomNavigationView
    private lateinit var fcPrincipal: FragmentContainerView
    private val viewModel: MainViewModel by viewModels()

    /*
    * Definimos un "atributo estatico"
    * que nos permita almacenar las llaves de las variables que se ponen en los Extras,
    * al lanzar una activity
    * */
    companion object {
        const val ID_PRODUCT_KEY = "id_product"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initComponents()
        initListeners()
        updateFragment()
    }

    private fun initComponents() {
        bnPrincipal = findViewById(R.id.bnPrincipal)
        fcPrincipal = findViewById(R.id.fcPrincipal)
    }


    private fun initListeners() {
        bnPrincipal.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.vProductos -> viewModel.saveCurrentFragment(ProductsFragment())
                R.id.vClientes -> viewModel.saveCurrentFragment(CustomersFragment())
                R.id.vEditar -> viewModel.saveCurrentFragment(EditFragment())
                else -> {}
            }
            updateFragment()
            true
        }
    }

    private fun updateFragment() {
        //Modificar la vista
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fcPrincipal, viewModel.currentFragment)
        fragmentTransaction.commit()
    }

}