package mx.itesm.gbvm.playparty

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_fragmento_musica2.*


class FragmentoMusica2 : Fragment() {
    lateinit var QRInicio: QRInicio
    lateinit var idMusica: String
    lateinit var array: Array<Tarjeta>

    lateinit var rvTarjeta: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_fragmento_musica2, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        btnSalir.setOnClickListener {
            QRInicio.cambiarMusica(FragmentoQR2.newInstance(QRInicio))
        }
        rvTarjeta = rvTarjetas
        val admLayout = LinearLayoutManager(QRInicio)
        rvTarjetas.layoutManager = admLayout
        crearArrTarjetas()
    }

    private fun crearArrTarjetas(){
        val miArreglo = ArrayList<Tarjeta>()
        val baseDatos = FirebaseDatabase.getInstance()
        val referencia = baseDatos.getReference("/Usuarios/$idMusica/Playlist")
        referencia.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach { registro ->
                    val tarjeta = registro.getValue(Tarjeta::class.java)!!
                    miArreglo.add(tarjeta)
                }
                val arreglo1 = Array(miArreglo.size){Tarjeta()}
                array = miArreglo.toArray(arreglo1)
                array.sortWith(Tarjeta.Comparator().reversed())
                rvTarjetas.adapter = Adaptador(array, idMusica)

            }
        })
    }

    companion object {
        @JvmStatic
        fun newInstance(qrInicio: QRInicio, param2: String) =
            FragmentoMusica2().apply {
                arguments = Bundle().apply {
                    QRInicio = qrInicio
                    idMusica = param2
                }
            }
    }
}