package com.sishome.homeinventory

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sishome.homeinventory.fragments.CustomersFragment
import com.sishome.homeinventory.fragments.EditFragment
import com.sishome.homeinventory.fragments.ProductsFragment

class MainActivity : AppCompatActivity() {
    private lateinit var bnPrincipal: BottomNavigationView
    private lateinit var fcPrincipal: FragmentContainerView

    /*
    * Definimos un "atributo estatico"
    * que nos permita almacenar las llaves de las variables que se ponen en los Extras,
    * al lanzar una activity
    * */
    companion object{
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
        replaceFragment(ProductsFragment())

    }

    private fun initComponents() {
        bnPrincipal = findViewById(R.id.bnPrincipal)
        fcPrincipal = findViewById(R.id.fcPrincipal)
    }


    private fun initListeners() {
        bnPrincipal.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.vProductos -> replaceFragment(ProductsFragment())
                R.id.vClientes -> replaceFragment(CustomersFragment())
                R.id.vEditar -> replaceFragment(EditFragment())
                else -> {}
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fcPrincipal, fragment)
        fragmentTransaction.commit()
    }

}