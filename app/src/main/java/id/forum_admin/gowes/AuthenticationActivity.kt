package id.forum_admin.gowes

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import id.forum_admin.gowes.databinding.ActivityAuthenticationBinding
import id.forum_admin.gowes.util.contentView
import org.koin.androidx.viewmodel.ext.android.viewModel

class AuthenticationActivity : AppCompatActivity() {
    private val binding: ActivityAuthenticationBinding by contentView(R.layout.activity_authentication)
    private val viewModel: AuthenticationViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val text = binding.tvHead.text.toString()
        Log.d("AuthenticationActivity", "onCreate, text: $text")
//        observeCurrentUser()
    }

    private fun observeCurrentUser() {
        viewModel.userAccount.observe(this, { currentUser ->
            if (currentUser.data == null) {
                Log.d("AuthenticationActivity", "not logged in yet")
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                Log.d("AuthenticationActivity", "current user is: ${currentUser.data}")
            }
        })
    }

}