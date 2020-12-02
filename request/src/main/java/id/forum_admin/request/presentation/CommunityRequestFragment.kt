package id.forum_admin.request.presentation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import id.forum_admin.core.account.presentation.UserAccountViewModel
import id.forum_admin.gowes.data.Status.ERROR
import id.forum_admin.gowes.data.Status.SUCCESS
import id.forum_admin.gowes.request.domain.model.CommunityRequest
import id.forum_admin.request.R
import id.forum_admin.request.databinding.FragmentCommunityRequestBinding
import id.forum_admin.request.injectFeature
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class CommunityRequestFragment : Fragment() {
    private lateinit var binding: FragmentCommunityRequestBinding
    private val accountViewModel: UserAccountViewModel by sharedViewModel()
    private val viewModel: CommunityRequestViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectFeature()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCommunityRequestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        binding.apply {
            val adapter = RequestAdapter(
                RequestListener(
                    clickListener = {
                        val action = CommunityRequestFragmentDirections.actionToDetail(it)
                        findNavController().navigate(action)
                    },
                    approveListener = { request ->
                        showAlertDialog(
                            getString(R.string.approve_request_message),
                            isApproved = true,
                            request
                        )
                    },
                    rejectListener = { request ->
                        showAlertDialog(
                            getString(R.string.reject_request_message),
                            isApproved = false,
                            request
                        )
                    }

                )
            )
            rvRequest.adapter = adapter
            val itemTouchHelper = ItemTouchHelper(ReboundingSwipeActionCallback())
            itemTouchHelper.attachToRecyclerView(rvRequest)

            refreshLayout.setOnRefreshListener { viewModel.refreshRequest() }
            accountViewModel.userAccount.observe(requireActivity(), {
                Log.d("RequestFragment", "user account has changed")
                it.data?.let { user ->
                    Log.d("RequestFragment", "current user is: $user")
                    if (user.id.isNotBlank()) {
                        if (viewModel.currentUser.id.isBlank()) {
                            viewModel.getCommunityRequest()
                        }
                    }
                }
            })


            viewModel.communityRequest.observe(viewLifecycleOwner, {
                when (it.status) {
                    SUCCESS -> {
                        adapter.submitList(it.data)
                        refreshLayout.isRefreshing = false
                    }
//                    ERROR -> accountViewModel.showSnackBar("Sorry, something went wrong, Try again.")
                    ERROR -> {
                        accountViewModel.showSnackBar(it.message.toString())
                        refreshLayout.isRefreshing = false
                    }
                    else -> Unit
                }
            })
        }
    }

    private fun showAlertDialog(message: String, isApproved: Boolean, request: CommunityRequest) {
        Log.d("alert dialog showed", "message: $message")
        val admin = accountViewModel.getCurrentUser()
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(message)
            .setNegativeButton(resources.getString(R.string.cancel_button)) { _, _ ->

            }
            .setPositiveButton(resources.getString(R.string.confirm_button)) { _, _ ->
                admin?.let {
                    if (isApproved) {
                        viewModel.acceptRequest(request, admin)
                    } else {
                        viewModel.rejectRequest(request, admin)
                    }
                }
            }
            .show()
    }
}