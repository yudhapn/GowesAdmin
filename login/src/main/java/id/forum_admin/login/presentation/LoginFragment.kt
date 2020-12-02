package id.forum_admin.login.presentation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import id.forum_admin.core.account.presentation.UserAccountViewModel
import id.forum_admin.gowes.MainActivity
import id.forum_admin.gowes.R
import id.forum_admin.gowes.data.Status.ERROR
import id.forum_admin.gowes.data.Status.SUCCESS
import id.forum_admin.gowes.util.*
import id.forum_admin.login.databinding.FragmentLoginBinding
import id.forum_admin.login.injectFeature
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

@ExperimentalCoroutinesApi
class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private val viewModel: LoginViewModel by viewModel()
    private val accountViewModel: UserAccountViewModel by sharedViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectFeature()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        Log.d("LoginFragment", "attached")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewListener()
    }

    private fun setupViewListener() {
        binding.apply {
            etPassword.setOnEditorActionListener { _, actionId, _ ->
                var handled = false
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    handleLogin()
                    handled = true
                }
                handled
            }
            btnLogin.button.setOnClickListener { handleLogin() }
            etEmail.setErrorStateTextInput(tilEmail)
            etPassword.setErrorStateTextInput(tilPassword)
        }
    }

    private fun handleLogin() {
        binding.apply {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            if (validateInput(email, password)) {
                viewModel.loginByEmail(email, password).observe(viewLifecycleOwner, Observer {
                    Log.d("LoginFragment", "handleLogin")
                    responseType = it
                    when (it.status) {
                        SUCCESS -> {
                            startActivity(Intent(requireContext(), MainActivity::class.java).apply {
                                val bundle = Bundle().apply {
                                    putString("email", email)
                                    putString("password", password)
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                }
                                putExtras(bundle)
                            })
                        }
                        ERROR -> accountViewModel.showSnackBar(it.message.toString())
                        else -> Unit
                    }
                })
            }
        }
        requireContext().hideSoftKeyboard(requireActivity().currentFocus?.windowToken)
    }

    private fun validateInput(email: String, password: String): Boolean {
        binding.apply {
            val emailValid = email.isValidEmail()
                .also { valid ->
                    tilEmail.setErrorMessage(if (!valid) getString(R.string.error_email) else null)
                }
            val passwordValid = password.isTextValid()
                .also { valid ->
                    tilPassword.setErrorMessage(if (!valid) getString(R.string.error_password) else null)
                }
            return emailValid && passwordValid
        }
    }
}
