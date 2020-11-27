package mx.itesm.gbvm.playparty

import android.annotation.SuppressLint
import android.location.Location
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.CameraUpdateFactory

import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.*

class FragmentoMapa(posicion: Location?) : Fragment(){

    private  val database = FirebaseDatabase.getInstance()
    private lateinit var referencia: DatabaseReference

    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { googleMap ->
        googleMap.isMyLocationEnabled = true
        referencia = database.getReference("/Usuarios")
        referencia.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                for (registro in snapshot.children) {
                    //leer coordenadas de gps (combinar practica de gps)
                    val establecimiento = registro.getValue(mx.itesm.gbvm.playparty.Usuario::class.java)
                    if (establecimiento != null) {
                        val nombre = establecimiento.nombreU
                        val latLng = LatLng(establecimiento.latitud.toDouble(), establecimiento.longitud.toDouble())
                        googleMap.addMarker(MarkerOptions().position(latLng).title(nombre))
                        if (posicion != null) {
                            googleMap.moveCamera(
                                CameraUpdateFactory.newLatLng(
                                    LatLng(
                                        posicion.latitude,
                                        posicion.longitude
                                    )
                                )
                            )
                            val camara = CameraPosition.Builder()
                                .target(LatLng(posicion.latitude, posicion.longitude)).zoom(18f)
                                .build()
                            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(camara))
                        }
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