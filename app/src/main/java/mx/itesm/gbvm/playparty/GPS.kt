package mx.itesm.gbvm.playparty

import android.annotation.SuppressLint
import android.app.Activity
import android.content.IntentSender
import android.location.Location
import android.os.Looper
import android.widget.Toast
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import java.util.concurrent.TimeUnit

class GPS
{
    private var gps: FusedLocationProviderClient? = null
    private var locationRequest: LocationRequest? = null
    private var locationCallback: LocationCallback? = null
    private var posicionActual: Location? = null

    // Ajustes (Para verificar que el GPS está prendido)
    private var ajustes: SettingsClient? = null
    private var ajustesRequest: LocationSettingsRequest? = null

    var gpsListener: GPSListener? = null

    fun inicializar(contexto: QRInicio) {
        gps = LocationServices.getFusedLocationProviderClient(contexto)
        ajustes = LocationServices.getSettingsClient(contexto)

        crearLocationRequest()
        crearLocationCallback()
        crearAjustesRequest()
    }

    @SuppressLint("MissingPermission")
    fun iniciarActualizaciones() {
        ajustes!!.checkLocationSettings(ajustesRequest)
            .addOnSuccessListener(gpsListener as Activity, OnSuccessListener<LocationSettingsResponse> {
                if (gps != null) {
                    // Inicia actualizaciones
                    gps!!.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
                }
            })
            .addOnFailureListener(gpsListener as Activity, OnFailureListener { e ->
                // Está apagado el gps
                val codigo = (e as ApiException).statusCode
                when (codigo) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        try {
                            val rae = e as ResolvableApiException
                            rae.startResolutionForResult(gpsListener as Activity, CODIGO_PRENDE_GPS)
                        } catch (sie: IntentSender.SendIntentException) {
                        }
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                        Toast.makeText(gpsListener as Activity, "Activa el GPS en Ajustes", Toast.LENGTH_LONG).show()
                        gps = null
                    }
                }
            })
    }


    fun detenerActualizaciones() {
        if (gps != null) {
            gps!!.removeLocationUpdates(locationCallback)
        }
    }

    private fun crearAjustesRequest() {
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(locationRequest!!)
        ajustesRequest = builder.build()
    }

    private fun crearLocationCallback() {
        locationCallback = object: LocationCallback() {
            override fun onLocationResult(locations: LocationResult?) {
                super.onLocationResult(locations)

                posicionActual = locations?.lastLocation
                if (posicionActual != null && gpsListener != null) {
                    // Avisar al listener que hay una nueva posición
                    gpsListener?.actualizarPosicion(posicionActual!!)
                }
            }
        }
    }

    private fun crearLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest?.interval = TimeUnit.SECONDS.toMillis(15)
        locationRequest?.fastestInterval = TimeUnit.SECONDS.toMillis(10)
        locationRequest?.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    companion object {
        val CODIGO_PRENDE_GPS: Int = 220
    }
}

interface GPSListener
{
    fun actualizarPosicion(posicion: Location)

}
