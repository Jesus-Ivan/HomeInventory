package com.sishome.homeinventory.view_models


import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.sishome.homeinventory.fragments.ProductsFragment

class MainViewModel : ViewModel() {

    private var _currentFragment: Fragment = ProductsFragment()
    val currentFragment: Fragment
        get() = _currentFragment

    /**
     * Guarda el fragento actual en el modelo
     */
    fun saveCurrentFragment(fragment: Fragment) {
        _currentFragment = fragment
    }

}