package com.makelick.anytime.view

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.makelick.anytime.R
import com.makelick.anytime.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configBottomNav()
    }

    override fun onStart() {
        super.onStart()
        addNavigationListener()
    }

    private fun configBottomNav() {
        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.focus -> {
                    navigate(R.id.focusFragment)
                }

                R.id.tasks -> {
                    navigate(R.id.tasksFragment)
                }

                R.id.calendar -> {
                    navigate(R.id.calendarFragment)
                }

                R.id.profile -> {
                    navigate(R.id.profileFragment)
                }

                else -> false
            }
        }
    }

    private fun navigate(destinationId: Int): Boolean {
        return try {
            findNavController(R.id.fragment_content_main).apply {
                popBackStack()
                navigate(destinationId)
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun addNavigationListener() {
        findNavController(R.id.fragment_content_main).addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNavigationView.visibility =
            if (destination.id in arrayOf(
                    R.id.focusFragment,
                    R.id.tasksFragment,
                    R.id.calendarFragment,
                    R.id.profileFragment
                )) {
                View.VISIBLE
            }
            else View.GONE
        }
    }

    fun changeBottomNavSelectedId(destinationId: Int) {
        binding.bottomNavigationView.selectedItemId = destinationId
    }
}