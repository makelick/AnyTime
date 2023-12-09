package com.makelick.anytime

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
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
            findNavController(R.id.fragment_content_main).navigate(destinationId)
            true
        } catch (e: Exception) {
            false
        }
    }
}