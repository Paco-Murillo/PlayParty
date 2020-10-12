package mx.itesm.gbvm.playparty

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.*

class FragmentoMapa : Fragment(){

    private  val database = FirebaseDatabase.getInstance()
    private lateinit var referencia: DatabaseReference

    fun cargarDatos(v: View){
        val establecimiento = Establecimiento(123.12,123.42,"LuismiBar", "EsteEsUnID")
        referencia.push().setValue(establecimiento)
    }

    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { googleMap ->
        googleMap.isMyLocationEnabled = true
        referencia = database.getReference("/Establecimientos")
        referencia.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                for (registro in snapshot.children) {
                    val location = registro.getValue(mx.itesm.gbvm.playparty.Establecimiento::class.java)
                    if (location != null) {
                        val nombre = location.nombre
                        val latLng = LatLng(location.latitud, location.longitud)
                        googleMap.addMarker(MarkerOptions().position(latLng).title(nombre))
                    }
                }
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_fragmento_mapa, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }


}