package com.saefulrdevs.dicodingevent.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.saefulrdevs.dicodingevent.R
import com.saefulrdevs.dicodingevent.data.local.model.FavoriteEvent
import com.saefulrdevs.dicodingevent.databinding.FragmentDetailBinding
import com.saefulrdevs.dicodingevent.viewmodel.MainViewModel
import com.saefulrdevs.dicodingevent.viewmodel.ViewModelFactory

class DetailFragment : Fragment() {
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding
    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)

        val eventId = arguments?.getInt("eventId")

        if (eventId != null) {
            mainViewModel.getDetailEvent(eventId)
        }



        mainViewModel.detailEvent.observe(viewLifecycleOwner) { event ->
            binding?.apply {
                tvEventName.text = event.name
                tvEventName.text = event.name
                tvOwnerName.text = event.ownerName
                tvEventTime.text = getString(R.string.event_time, event.beginTime)
                val remainingQuota = event.quota?.minus(event.registrants ?: 0) ?: 0
                tvQuota.text = getString(R.string.quota_remaining, remainingQuota)

                tvDescription.text = event.description?.let {
                    HtmlCompat.fromHtml(
                        it,
                        HtmlCompat.FROM_HTML_MODE_LEGACY
                    )
                }
                btnEventLink.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(event.link))
                    startActivity(intent)
                }
            }
            binding?.ivMediaCover?.let {
                Glide.with(requireContext())
                    .load(event.mediaCover)
                    .into(it)
            }



        }

        mainViewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }

        mainViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                binding?.apply {
                    handlingLayout.visibility = View.VISIBLE
                    tvErrorMessage.text = errorMessage
                    btnRefresh.visibility = View.VISIBLE
                    btnRefresh.setOnClickListener {
                        if (eventId != null) {
                            mainViewModel.getDetailEvent(eventId)
                        }
                    }
                }

            } else {
                binding?.handlingLayout?.visibility = View.GONE
            }
        }
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        return requireNotNull(binding?.root) { "Binding is null!" }
    }

    private fun showLoading(isLoading: Boolean) {
        binding?.progressBar?.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

