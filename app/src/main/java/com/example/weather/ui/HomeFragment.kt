package com.example.weather.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.weather.R
import com.example.weather.adapters.ForecastAdapter
import com.example.weather.databinding.FragmentHomeBinding
import com.example.weather.domain.WeatherApiStatus
import com.example.weather.viewmodels.WeatherViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import timber.log.Timber

class HomeFragment : Fragment() {
    private val viewModel: WeatherViewModel by lazy {
        val application = requireNotNull(this.activity).application
        ViewModelProvider(this, WeatherViewModel.Factory(application)).get(WeatherViewModel::class.java)
    }
    private lateinit var adapter: ForecastAdapter
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding: FragmentHomeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        adapter = ForecastAdapter()

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            homeViewModel = viewModel
            recyclerView.adapter = adapter
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.forecasts.observe(viewLifecycleOwner, {
            adapter.submitList(it)
            if(!it.isNullOrEmpty()) {
                viewModel.setStatus(WeatherApiStatus.DONE)
            }
        })

        viewModel.status.observe(viewLifecycleOwner, {
            if(it.equals(WeatherApiStatus.ERROR))
                showErrorUI()
        })

        checkForPermission()
    }

    private fun checkForPermission() {
        val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                if (isGranted) {
                    Timber.d("Permission granted by user")
                    getCurrentLocation()
                } else {
                    Timber.d("Permission denied by user")
                    showInContextUI()
                }
            }

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            Timber.d("checkSelfPermission granted")
            getCurrentLocation()
        }
        else {
            Timber.d("checkSelfPermission denied")
            requestPermissionLauncher.launch(
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
    }

    private fun showInContextUI() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Permission Disabled")
            .setMessage("Please enable the permission in \nSettings>Weather>Permission \nand check 'location' permission")
            .setPositiveButton("Go to settings") { _, _ -> startActivity(Intent(Settings.ACTION_SETTINGS)) }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
            .show()
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        fusedLocationClient.lastLocation
            .addOnCompleteListener {
                val location: Location? = it.result

                if (location != null) {
                    viewModel.refreshDataFromRepository(latitude = location.latitude, longitude = location.longitude)
                }
                else {
                    viewModel.refreshDataFromRepository(city = "Soweto")
                    showErrorUI()
                }
            }
    }

    private fun showErrorUI() {
        Toast.makeText(requireContext(), "Network error: Location not found.", Toast.LENGTH_LONG).show()
    }

}