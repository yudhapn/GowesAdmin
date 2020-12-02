package id.forum_admin.gowes

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import com.google.android.material.snackbar.Snackbar
import id.forum_admin.core.account.presentation.UserAccountViewModel
import id.forum_admin.gowes.data.Status.ERROR
import id.forum_admin.gowes.data.Status.SUCCESS
import id.forum_admin.gowes.databinding.ActivityMainBinding
import id.forum_admin.gowes.util.contentView
import id.forum_admin.gowes.util.setupWithNavController
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by contentView(R.layout.activity_main)
    private var currentNavController: LiveData<NavController>? = null
    private val viewModel: UserAccountViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Gowes_DayNight)
        super.onCreate(savedInstanceState)
        intent.extras?.let {
            val email = it.getString("email")
            val password = it.getString("password")
            viewModel.setUserInformationLogin(email ?: "", password ?: "")
        }
        if (savedInstanceState == null) {
            setupBottomNavigationBar()
        }
        showSnackBar()
        observeCurrentUser()
    }

    private fun observeCurrentUser() {
        viewModel.userAccount.observe(this, { currentUser ->
            Log.d("mainActivity", "userAccount has changed")
            binding.progressBar.visibility = when (currentUser.status) {
                SUCCESS, ERROR -> View.INVISIBLE
                else -> VISIBLE
            }
            if (currentUser.data == null) {
                binding.bottomNavView.visibility = GONE
                Log.d("mainActivity", "not logged in yet")
                startActivity(Intent(this, AuthenticationActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                })
            } else {
                binding.bottomNavView.visibility = VISIBLE
                Log.d("mainActivity", "current user is: ${currentUser.data}")
            }

        })
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        setupBottomNavigationBar()
    }

    private fun setupBottomNavigationBar() {
        binding.apply {
            val navGraphIds = listOf(
                R.navigation.request_navigation,
                R.navigation.profile_navigation
            )

            // Setup the bottom navigation view with a list of navigation graphs
            val controller = binding.bottomNavView.setupWithNavController(
                navGraphIds = navGraphIds,
                fragmentManager = supportFragmentManager,
                containerId = R.id.nav_host_container,
                intent = intent
            )

//            controller.observe(this@MainActivity, Observer { navController ->
//                setupActionBarWithNavController(navController)
//            })
            currentNavController = controller
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return currentNavController?.value?.navigateUp() ?: false
    }

    override fun onBackPressed() {
        if (currentNavController?.value?.popBackStack() != true) {
            super.onBackPressed()
        }
    }

    private fun showSnackBar() {
        viewModel.messageSnackBar.observe(this, { message ->
            if (message.isNotBlank()) {
                val snackBar =
                    Snackbar.make(binding.coordinatorLayout, message, Snackbar.LENGTH_LONG)
                val params = snackBar.view.layoutParams as CoordinatorLayout.LayoutParams
                params.apply {
                    anchorId = binding.bottomNavView.id
                    anchorGravity = Gravity.TOP
                    gravity = Gravity.TOP
                }
                snackBar.view.layoutParams = params
                snackBar.show()
            }
        })
    }
}