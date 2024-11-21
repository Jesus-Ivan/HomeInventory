package com.sishome.homeinventory

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentContainerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.sishome.homeinventory.fragments.CustomersFragment
import com.sishome.homeinventory.fragments.EditFragment
import com.sishome.homeinventory.fragments.ProductsFragment
import com.sishome.homeinventory.view_models.MainViewModel

class MainActivity : AppCompatActivity() {

    //Variables referentes a los componentes de UI
    private lateinit var fcPrincipal: FragmentContainerView

    private lateinit var toggle : ActionBarDrawerToggle
    private lateinit var drawerLayout : DrawerLayout
    private lateinit var navView : NavigationView


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
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.dlMain)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //Habilita el hamburger menu
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        initComponents()
        initListeners()
        updateFragment()
    }

    private fun initComponents() {
        drawerLayout = findViewById(R.id.dlMain)        //Layout principal de la actividad
        navView = findViewById(R.id.nvMain)             //Menu lateral
        fcPrincipal = findViewById(R.id.fcPrincipal)    //Contenedor de fragment
        /**
         * Boton del appbar para abrir el drawer
         */
        toggle = ActionBarDrawerToggle(this, drawerLayout,R.string.open, R.string.close)
        toggle.syncState()
    }


    private fun initListeners() {
        //Agregar el boton hamburgesa al drawerLayout
        drawerLayout.addDrawerListener(toggle)

        //listener a los items dentro del navView
        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.vProductos -> viewModel.saveCurrentFragment(ProductsFragment())
                R.id.vClientes -> viewModel.saveCurrentFragment(CustomersFragment())
                R.id.vEditar -> viewModel.saveCurrentFragment(EditFragment())
                else -> {}
            }
            drawerLayout.close()
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}