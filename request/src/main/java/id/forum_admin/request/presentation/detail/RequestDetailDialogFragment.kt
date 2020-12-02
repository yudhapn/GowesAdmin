package id.forum_admin.request.presentation.detail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import id.forum_admin.request.databinding.FragmentDialogRequestDetailBinding

class RequestDetailDialogFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentDialogRequestDetailBinding
    private val args: RequestDetailDialogFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDialogRequestDetailBinding.inflate(inflater, container, false)
            .apply {
                community = args.community
            }
        Log.d("RequestDetailFragment", "RequestDetailDialogFragment run")
        return binding.root
    }
}