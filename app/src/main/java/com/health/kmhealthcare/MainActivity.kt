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
import com.health.kmhealthcare.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
//    private var binding: ActivityMainBinding? = null
    private lateinit var binding:ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var fragmentManager: FragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)




        setSupportActionBar(binding.toolbar)
        val toggle = ActionBarDrawerToggle(this,binding.drawerLayout, binding.toolbar,
            R.string.nav_open,
            R.string.nav_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        binding.navigationDrawer.setNavigationItemSelectedListener(this)
        binding.bottomNavigation.background = null
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when(item.itemId){
                R.id.bottom_home -> openFragment(HomeFragment())
//                R.id.bottom_profile -> openFragment(ProfileFragment())
                R.id.bottom_feedback -> openFragment(PaHomeFragment())
                R.id.bottom_menu -> openFragment(ViewPatBookingFragment())
            }
            true
        }

        fragmentManager = supportFragmentManager
        openFragment(ViewPatBookingFragment())

        binding.fab.setOnClickListener {
            Toast.makeText(this, "Hello KM", Toast.LENGTH_SHORT).show()
        }
//        auth = Firebase.auth
//        binding?.btnSignOut?.setOnClickListener {
//            if (auth.currentUser!= null){
//                auth.signOut()
//                startActivity(Intent(this, activity_get_started::class.java))
//                finish()
//            }
//
//        }
    }



    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_home -> openFragment(ViewPatBookingFragment())
            R.id.nav_profile -> openFragment(ProfileFragment())
            R.id.nav_Feedback -> openFragment(PaHomeFragment())
            R.id.nav_doctorAvailability -> openFragment(DoctorAvailabilityFragment())
            R.id.nav_prescription -> openFragment(PrescriptionFragment())
            R.id.nav_payment -> openFragment(ViewPaymentFragment())



            R.id.nav_logout -> {
                    startActivity(Intent(this, activity_get_started::class.java))
                    finish()
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed(){
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun openFragment(fragment: Fragment){
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.commit();

    }


}