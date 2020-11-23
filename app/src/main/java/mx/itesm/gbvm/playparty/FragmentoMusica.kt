package mx.itesm.gbvm.playparty

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_qr.*

class FragmentoMusica : Fragment() {

    private lateinit var rvTarjetas: RecyclerView
    private val database = FirebaseDatabase.getInstance()
    private lateinit var referencia: DatabaseReference

    var idse = ""
    var Playlist = ""


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Playlist = arguments!!.getString("Playlist").toString()
        idse = arguments!!.getString("ID").toString()
        val vista = inflater.inflate(R.layout.fragment_musica, container, false)
        rvTarjetas = vista.findViewById(R.id.rvTarjetas)
        configurarRecyclerView()
        return vista
    }

    private fun configurarRecyclerView() {
        val admLayout = LinearLayoutManager(AppPlayParty.context)
        val arrTarjetas = crearArrTarjetas()

        val adaptador = Adaptador(arrTarjetas)
        if(rvTarjetas != null) {
            rvTarjetas.layoutManager = admLayout
            rvTarjetas.adapter = adaptador
        }
    }

    private fun crearArrTarjetas(): Array<Tarjeta> {
        var arreglo = arrayOf<Tarjeta>()
        /*
        referencia = database.getReference("/" + idse)
        referencia.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                var i = 0
                for (registro in snapshot.children) {
                    val tarjeta =
                        registro.getValue(mx.itesm.gbvm.playparty.Tarjeta::class.java)
                    if (tarjeta != null) {
                        arreglo.set(i, tarjeta)
                    }
                    i = i + 1
                    print(registro.toString())
                }
            }
        })*/
        arreglo.set(0, Tarjeta(10, "Artista", "Cancion", 0, "HOla"))
        arreglo.set(1, Tarjeta(13, "Artista", "Cancion", 0, "Hola"))
        arreglo.set(2, Tarjeta(4, "Artista", "Cancion", 0, "Hola"))
        println(arreglo.toString())
        arreglo.sort()
        println(arreglo.toString())
        return arreglo
    }

}
