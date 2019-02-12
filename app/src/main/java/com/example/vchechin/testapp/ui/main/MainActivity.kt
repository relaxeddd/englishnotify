package com.example.vchechin.testapp.ui.main

import android.os.Bundle
import androidx.core.view.GravityCompat
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.example.vchechin.testapp.R
import com.example.vchechin.testapp.common.*
import com.example.vchechin.testapp.databinding.HeaderNavigationMainBinding
import com.example.vchechin.testapp.databinding.MainActivityBinding
import com.example.vchechin.testapp.model.db.AppDatabase
import com.example.vchechin.testapp.model.db.ConverterListStr
import kotlinx.android.synthetic.main.main_activity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Arrays.asList

class MainActivity : ActivityBase<ViewModelMain, MainActivityBinding>() {

    private val uiScope = CoroutineScope(Dispatchers.Main)
    lateinit var bindingHeader: HeaderNavigationMainBinding
    lateinit var navController: NavController

    override fun getLayoutResId() = R.layout.main_activity
    override fun getViewModelFactory() = InjectorUtils.provideMainViewModelFactory(this)
    override fun getViewModelClass(): Class<ViewModelMain> = ViewModelMain::class.java

    override fun configureBinding() {
        super.configureBinding()

        binding.viewModel = viewModel

        /*val factory = InjectorUtils.provideMainViewModelFactory(this)

        binding = DataBindingUtil.setContentView(this, R.layout.main_activity)
        viewModel = ViewModelProviders.of(this, factory).get(ViewModelMain::class.java)
        binding.viewModel = viewModel
        binding.setLifecycleOwner(this)
        binding.executePendingBindings()*/

        /*bindingHeader = HeaderNavigationMainBinding.bind(binding.navigationViewMain.getHeaderView(0))
        bindingHeader.viewModel = viewModel
        bindingHeader.setLifecycleOwner(this)
        bindingHeader.executePendingBindings()*/
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        navController = Navigation.findNavController(this, R.id.fragment_navigation_host)

        navController.navigate(R.id.fragmentDictionary)
        NavigationUI.setupWithNavController(navigation_view_main, navController)

        /*button_test.setOnClickListener {
            uiScope.launch {
                testFunc()
            }
        }*/
    }

    /*override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                with(drawer_layout_main) {
                    if (isDrawerOpen(GravityCompat.START)) closeDrawers() else openDrawer(GravityCompat.START)
                }
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }*/

    override fun onNavigationEvent(eventId: Int) {
        when (eventId) {
            NAVIGATION_FRAGMENT_NOTIFICATIONS -> {
                if (navController.currentDestination?.label != getString(R.string.label_fragment_notifications)) {
                    navController.navigate(R.id.fragmentNotifications)
                }
            }
            NAVIGATION_FRAGMENT_SETTINGS -> {
                if (navController.currentDestination?.label != getString(R.string.label_fragment_settings)) {
                    navController.navigate(R.id.fragmentSettings)
                }
            }
            else -> super.onNavigationEvent(eventId)
        }
    }

    private suspend fun testFunc() {
        withContext(Dispatchers.IO) {
            AppDatabase.getInstance(this@MainActivity).wordDao().insertAll(Word("Test", "Тест", "test"))
        }
    }
}
