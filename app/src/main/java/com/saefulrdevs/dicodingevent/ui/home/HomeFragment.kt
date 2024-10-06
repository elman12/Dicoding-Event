package com.saefulrdevs.dicodingevent.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.saefulrdevs.dicodingevent.R
import com.saefulrdevs.dicodingevent.data.remote.response.ListEventsItem
import com.saefulrdevs.dicodingevent.databinding.FragmentHomeBinding
import com.saefulrdevs.dicodingevent.utils.UiHandler.handleError
import com.saefulrdevs.dicodingevent.utils.UiHandler.showLoading
import com.saefulrdevs.dicodingevent.viewmodel.AdapterVerticalEvent // Import adapter vertical
import com.saefulrdevs.dicodingevent.viewmodel.MainViewModel
import com.saefulrdevs.dicodingevent.viewmodel.ViewModelFactory

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding

    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(requireActivity())
    }
    private lateinit var adapterVertical: AdapterVerticalEvent // Ubah menjadi adapter vertikal

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        setupRecyclerViews()
        setupAdapters()
        observeViewModel()

        return requireNotNull(binding?.root) { "Binding is null!" }
    }

    private fun setupRecyclerViews() {
        binding?.apply {
            val verticalLayout = LinearLayoutManager(requireContext())
            rvUpcomingEvent.layoutManager = verticalLayout // Ganti layout manager menjadi vertikal
            rvUpcomingEvent.addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    verticalLayout.orientation
                )
            )
        }
    }

    private fun setupAdapters() {
        adapterVertical = AdapterVerticalEvent { eventId -> // Ganti menjadi adapter vertikal
            val bundle = Bundle().apply {
                if (eventId != null) {
                    putInt("eventId", eventId)
                }
            }
            findNavController().navigate(R.id.navigation_detail, bundle)
        }

        binding?.rvUpcomingEvent?.adapter = adapterVertical // Set adapter untuk RecyclerView
    }

    private fun observeViewModel() {
        binding?.apply {
            mainViewModel.upcomingEvent.observe(viewLifecycleOwner) { listItems ->
                setUpcomingEvent(listItems)
                mainViewModel.clearErrorMessage()
            }

            mainViewModel.isLoading.observe(viewLifecycleOwner) {
                showLoading(it, progressBar)
            }

            mainViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
                handleError(
                    isError = errorMessage != null,
                    message = errorMessage,
                    errorTextView = tvErrorMessage,
                    refreshButton = btnRefresh,
                    recyclerView = rvUpcomingEvent
                ) {
                    mainViewModel.getUpcomingEvent()
                }
            }
        }
    }

    private fun setUpcomingEvent(listUpcomingEvent: List<ListEventsItem>) {
        val limitedList = if (listUpcomingEvent.size > 5) listUpcomingEvent.take(5) else listUpcomingEvent
        adapterVertical.submitList(limitedList) // Kirim data ke adapter vertikal
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
