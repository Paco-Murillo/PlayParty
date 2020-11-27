package mx.itesm.gbvm.playparty

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList

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

        val miArreglo = ArrayList<Tarjeta>()
        referencia = database.getReference("/Establecimientos/-MJQtwPWvTOlnMHj0VdF/Playlist/")
        referencia.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                println("Hola")
                println(snapshot)
                snapshot.children.forEach { registro ->
                    val tarjeta = registro.getValue(Tarjeta::class.java)!!
                    println(tarjeta)
                    miArreglo.add(tarjeta)
                }
                println("Adios")
            }
        })
        val arreglo1 = Array(miArreglo.size){Tarjeta()}

        println(Arrays.toString(miArreglo.toArray(arreglo1)))
        return miArreglo.toArray(arreglo1)
    }

}
