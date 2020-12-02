package id.forum_admin.report.presentation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import id.forum_admin.core.account.presentation.UserAccountViewModel
import id.forum_admin.gowes.R
import id.forum_admin.gowes.data.Status
import id.forum_admin.gowes.data.Status.ERROR
import id.forum_admin.gowes.data.Status.SUCCESS
import id.forum_admin.report.databinding.FragmentReportBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ReportFragment : Fragment() {
    private lateinit var binding: FragmentReportBinding
    private val accountViewModel: UserAccountViewModel by sharedViewModel()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReportBinding.inflate(inflater, container, false).apply {
            toolbar.inflateMenu(R.menu.top_app_bar_profile_menu)
            toolbar.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_logout -> handleLogout()
                }
                true
            }
        }

        return binding.root
    }

    private fun handleLogout() {
        accountViewModel.logout().observe(viewLifecycleOwner, {
            accountViewModel.setCurrentState(it.status)
            when (it.status) {
                SUCCESS -> Unit
                ERROR -> accountViewModel.showSnackBar(it.message.toString())
                else -> Log.e("AccountViewModel", "failed logout: ${it.message.toString()}")
            }
        })
    }
}