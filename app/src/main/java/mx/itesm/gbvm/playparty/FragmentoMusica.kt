package mx.itesm.gbvm.playparty

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class FragmentoMusica : Fragment() {

    private lateinit var rvTarjetas: RecyclerView
    private val database = FirebaseDatabase.getInstance()
    private lateinit var referencia: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val vista = inflater.inflate(R.layout.fragment_musica, container, false)
        rvTarjetas = vista.findViewById(R.id.rvTarjetas)
        configurarRecyclerView()
        return vista
    }

    private fun configurarRecyclerView() {
        val admLayout = LinearLayoutManager(AppPlayParty.context)
        val arrTarjetas = crearArrTarjetas()

        val adaptador = Adaptador(arrTarjetas)
        rvTarjetas.layoutManager = admLayout
        rvTarjetas.adapter = adaptador
    }

    private fun crearArrTarjetas(): Array<Tarjeta> {
        var arreglo = arrayOf(
            Tarjeta(4, "No Money", "Galantis", "https://i.scdn.co/image/ab67616d0000b273119cb56f073efd326643dcfe", 189126, "540vIaP2JwjQb9dm3aArA4"),
            Tarjeta(4, "DJ Snake", "Middle", "https://i.scdn.co/image/ab67616d0000b273212d776c31027c511f0ee3bc", 220573,"0gb1J5UrTpzaU1s3nupgCd"),
            Tarjeta(0, "Cheap Thrills", "Sia", "https://i.scdn.co/image/ab67616d0000b27349e0134c686547c28b7c999f", 211666, "27SdWb2rFzO6GWiYDBTD9j")
        )

        val miArreglo = ArrayList<Tarjeta>()
        referencia = database.getReference("Establecimientos/MJQtwPWvTOlnMHj0VdF/Playlist")
        referencia.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                System.out.println("Hola")
                var i = 0
                for (registro in snapshot.children) {
                    System.out.println(registro)
                    i++
                    val tarjeta =
                        registro.getValue(mx.itesm.gbvm.playparty.Tarjeta::class.java)
                    if (tarjeta != null) {
                        arreglo.set(i,tarjeta)
                        miArreglo.add(tarjeta)
                    }
                }
            }
        })
        System.out.println(miArreglo.toString())
        return arreglo
    }

}
