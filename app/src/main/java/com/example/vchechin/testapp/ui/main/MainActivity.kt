package com.example.vchechin.testapp.ui.main

import android.os.Bundle
import androidx.core.view.GravityCompat
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.example.vchechin.testapp.R
import com.example.vchechin.testapp.common.ActivityBase
import com.example.vchechin.testapp.common.InjectorUtils
import com.example.vchechin.testapp.common.Word
import com.example.vchechin.testapp.databinding.HeaderNavigationMainBinding
import com.example.vchechin.testapp.databinding.MainActivityBinding
import com.example.vchechin.testapp.model.db.AppDatabase
import kotlinx.android.synthetic.main.main_activity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ActivityBase() {

    private val uiScope = CoroutineScope(Dispatchers.Main)
    lateinit var viewModel: ViewModelMain
    lateinit var binding: MainActivityBinding
    lateinit var bindingHeader: HeaderNavigationMainBinding

    override fun configureBinding() {
        super.configureBinding()

        val factory = InjectorUtils.provideMainViewModelFactory(this)

        binding = DataBindingUtil.setContentView(this, R.layout.main_activity)
        viewModel = ViewModelProviders.of(this, factory).get(ViewModelMain::class.java)
        binding.viewModel = viewModel
        binding.setLifecycleOwner(this)
        binding.executePendingBindings()

        bindingHeader = HeaderNavigationMainBinding.bind(binding.navigationViewMain.getHeaderView(0))
        bindingHeader.viewModel = viewModel
        bindingHeader.setLifecycleOwner(this)
        bindingHeader.executePendingBindings()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)

        val navController: NavController = Navigation.findNavController(this, R.id.fragment_navigation_host)

        navController.navigate(R.id.fragmentDictionary)
        NavigationUI.setupWithNavController(navigation_view_main, navController)

        button_test.setOnClickListener {
            uiScope.launch {
                testFunc()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                with(drawer_layout_main) {
                    if (isDrawerOpen(GravityCompat.START)) closeDrawers() else openDrawer(GravityCompat.START)
                }
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private suspend fun testFunc() {
        withContext(Dispatchers.IO) {
            AppDatabase.getInstance(this@MainActivity).wordDao().insertAll(Word("Test", "Тест", "test"))
        }
    }
}
