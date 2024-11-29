package com.desafiolatam.weatherlatam.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.desafiolatam.weatherlatam.R
import com.desafiolatam.weatherlatam.WeatherApplication
import com.desafiolatam.weatherlatam.data.CELSIUS
import com.desafiolatam.weatherlatam.data.ITEM_ID
import com.desafiolatam.weatherlatam.databinding.FragmentHomeBinding
import com.desafiolatam.weatherlatam.extension.hide
import com.desafiolatam.weatherlatam.extension.show
import com.desafiolatam.weatherlatam.model.WeatherDto
import com.desafiolatam.weatherlatam.view.adapter.WeatherAdapter
import com.desafiolatam.weatherlatam.view.viewmodel.UIState
import com.desafiolatam.weatherlatam.view.viewmodel.WeatherViewModel
import com.desafiolatam.weatherlatam.view.viewmodel.WeatherViewModelFactory
import kotlinx.coroutines.flow.collectLatest

class HomeFragment : Fragment() {

    private lateinit var adapter: WeatherAdapter
    private lateinit var tempUnit: String

    private val viewModel: WeatherViewModel by viewModels {
        WeatherViewModelFactory((activity?.application as WeatherApplication).repository)
    }

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        sharedPref.getString(getString(R.string.settings_temperature_unit), CELSIUS)?.let {
            tempUnit = it
        }

        adapter = WeatherAdapter(
            weatherList = emptyList(),
            inCelsius = tempUnit == CELSIUS
        )
        getRemoteWeatherData()
        getWeatherData()
        initRecyclerView()
        navigateToDetails()
        navigateToSettings()
        refresh()
    }

    private fun getRemoteWeatherData() {
        lifecycleScope.launchWhenCreated {
            viewModel.getRemoteWeatherData().collectLatest { state ->

                when (state) {
                    is UIState.Loading -> binding.rvWeather.show()
                    is UIState.Success -> {
                        binding.rvWeather.show()
                        val list = listOf(state.data as WeatherDto)
                        populateRecyclerView(list)
                    }

                    is UIState.Error -> {
                        binding.rvWeather.hide()
                        binding.cityName.hide()
                    }
                }
            }
        }
    }

    private fun getWeatherData() {
        lifecycleScope.launchWhenCreated {
            viewModel.getWeather().collectLatest { list ->
                list?.let {
                    populateRecyclerView(it)
                }
            }
        }
    }

    private fun initRecyclerView() {
        binding.rvWeather.layoutManager = LinearLayoutManager(context)
        binding.rvWeather.adapter = adapter
    }

    private fun populateRecyclerView(list: List<WeatherDto>) {
        if (list.isNotEmpty()) {
            binding.cityName.text = list.last().cityName
            adapter.weatherList = list
            adapter.inCelsius = tempUnit == CELSIUS
        }
        adapter.notifyDataSetChanged()
    }

    private fun refresh(){
        binding.refresh.setOnRefreshListener {
            getRemoteWeatherData()
            binding.refresh.isRefreshing = false
        }
    }

    private fun navigateToDetails() {
        adapter.onClick = {
            val bundle = bundleOf(ITEM_ID to it)
            findNavController().navigate(R.id.action_homeFragment_to_detailsFragment, bundle)
        }
    }

    private fun navigateToSettings() {
        binding.btnSettings.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_settingsFragment)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}