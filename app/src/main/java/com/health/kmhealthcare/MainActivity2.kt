package com.health.kmhealthcare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.health.kmhealthcare.R
import com.health.kmhealthcare.databinding.ActivityMain2Binding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth


class MainActivity2 : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMain2Binding
    private lateinit var auth: FirebaseAuth
    private lateinit var fragmentManager: FragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding?.root)




        setSupportActionBar(binding.toolbar)
        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.nav_open,
            R.string.nav_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        binding.navigationDrawer.setNavigationItemSelectedListener(this)
        binding.bottomNavigation.background = null
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottom_home -> openFragment(HomeFragment())
                R.id.bottom_statues -> openFragment(ViewBookingStatuesFragment())
                R.id.bottom_feedback -> openFragment(PaFeedbackFragment())
                R.id.bottom_menu -> openFragment(PaymentFragment())
            }
            true
        }

        fragmentManager = supportFragmentManager
        openFragment(HomeFragment())
//            binding.bottomNavigation.selectedItemId = R.id.bottom_home

//        binding.fab.setOnClickListener {
//            Toast.makeText(this, "Hello KM", Toast.LENGTH_SHORT).show()
//        }

    }
    ///////////////////////////////////////////////////////
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                openFragment(HomeFragment())
                binding.bottomNavigation.selectedItemId = R.id.bottom_home
            }
            R.id.nav_profile -> {
                openFragment(PaProfileFragment())
                clearBottomNavigationSelection()
                // Update bottom navigation selection if applicable
            }
            R.id.nav_booking -> {
                openFragment(ViewBookingStatuesFragment())
                binding.bottomNavigation.selectedItemId = R.id.bottom_statues
            }
            R.id.nav_prescription -> {
                openFragment(ViewPrescriptionFragment())
            }
            R.id.nav_Feedback -> {
                openFragment(PaFeedbackFragment())
                binding.bottomNavigation.selectedItemId = R.id.bottom_feedback
            }
            R.id.nav_payment -> {
                openFragment(PaymentFragment())
                binding.bottomNavigation.selectedItemId = R.id.bottom_menu
            }
//                R.id.nav_home -> openFragment(HomeFragment())
//                R.id.nav_profile -> openFragment(PaProfileFragment())
//                R.id.nav_booking -> openFragment(ViewBookingStatuesFragment())
//                R.id.nav_prescription -> openFragment(ViewBookingStatuesFragment())
//                R.id.nav_Feedback -> openFragment(PaFeedbackFragment())
//                R.id.nav_payment -> openFragment(PaymentFragment())


            R.id.nav_logout -> {
                startActivity(Intent(this, activity_get_started::class.java))
                finish()
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun openFragment(fragment: Fragment) {
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.commit();

    }

    private fun clearBottomNavigationSelection() {
        val menu = binding.bottomNavigation.menu
        menu.setGroupCheckable(0, false, true)
        // Clearing selection
        for (i in 0 until menu.size()) {
            menu.getItem(i).isChecked = false
        }
        menu.setGroupCheckable(0, true, true)
    }
}
