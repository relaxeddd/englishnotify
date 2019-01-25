package com.example.vchechin.testapp.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.GravityCompat
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.example.vchechin.testapp.R
import com.example.vchechin.testapp.common.ActivityBase
import com.example.vchechin.testapp.data.LiveDataEmail
import com.example.vchechin.testapp.databinding.HeaderNavigationMainBinding
import com.example.vchechin.testapp.databinding.MainActivityBinding
import com.example.vchechin.testapp.model.CacheStub
import kotlinx.android.synthetic.main.header_navigation_main.*
import kotlinx.android.synthetic.main.main_activity.*

class MainActivity : ActivityBase() {

    lateinit var viewModel: ViewModelMain
    lateinit var binding: MainActivityBinding
    lateinit var bindingHeader: HeaderNavigationMainBinding

    override fun configureBinding() {
        super.configureBinding()

        binding = DataBindingUtil.setContentView(this, R.layout.main_activity)
        viewModel = ViewModelProviders.of(this).get(ViewModelMain::class.java)
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

        button_test.setOnClickListener {
            CacheStub.email.set(CacheStub.email.get() + "s")
        }

        /*if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, FragmentDictionary.newInstance())
                .commitNow()
        }*/

        /*navigation_view_main.setNavigationItemSelectedListener {
            //it.isChecked = true
            drawer_layout_main.closeDrawers()
            true
        }*/
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)

        val navController: NavController = Navigation.findNavController(this, R.id.fragment_navigation_host)

        navController.navigate(R.id.fragmentDictionary)
        NavigationUI.setupWithNavController(navigation_view_main, navController)

        LiveDataEmail.observe(this, Observer {

        })
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                drawer_layout_main.openDrawer(GravityCompat.START)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}
