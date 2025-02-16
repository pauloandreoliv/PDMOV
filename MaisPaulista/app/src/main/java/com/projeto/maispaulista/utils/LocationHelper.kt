package com.projeto.maispaulista.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import java.io.IOException
import java.util.Locale

class LocationHelper(private val activity: Activity, private val addressEditText: EditText) {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    var isManualEdit = false

    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 1
        const val LOCATION_SETTINGS_REQUEST_CODE = 2
    }

    init {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    if (!isManualEdit) {
                        val latitude = location.latitude
                        val longitude = location.longitude
                        val address = getAddressFromLocation(latitude, longitude)
                        addressEditText.setText(address)
                        Log.d("EditTextUpdate", "Endereço definido no EditText: $address")
                    }
                }
            }
        }

        addressEditText.addTextChangedListener(object : android.text.TextWatcher {
            override fun afterTextChanged(s: android.text.Editable) {
                isManualEdit = true
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Não é necessário fazer nada aqui
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // Não é necessário fazer nada aqui
            }
        })
    }

    fun checkLocationAndEnableGPS() {
        if (checkLocationPermission()) {
            if (isLocationEnabled()) {
                startLocationUpdates()
            } else {
                enableGPS()
            }
        } else {
            requestLocationPermission()
        }
    }

    private fun checkLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    fun isLocationEnabled(): Boolean {
        val locationManager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isEnabled =
            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
            )
        Log.d("LocationEnabled", "GPS ativado: $isEnabled")
        return isEnabled
    }

    fun enableGPS() {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val client: SettingsClient = LocationServices.getSettingsClient(activity)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            startLocationUpdates()
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    exception.startResolutionForResult(activity, LOCATION_SETTINGS_REQUEST_CODE)
                } catch (sendEx: IntentSender.SendIntentException) {
                    Log.e("enableGPS", "Erro ao abrir configurações de localização", sendEx)
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {
        val locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    private fun getAddressFromLocation(latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(activity, Locale.getDefault())
        return try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                Log.d("GeocoderResult", "Endereço encontrado: ${address.getAddressLine(0)}")
                address.getAddressLine(0)
            } else {
                Log.d("GeocoderResult", "Nenhum endereço encontrado")
                "Endereço não encontrado"
            }
        } catch (e: IOException) {
            Log.e("GeocoderError", "Erro no Geocoder: ${e.message}")
            "Erro ao buscar endereço"
        }
    }

    fun checkLocationPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }
}
