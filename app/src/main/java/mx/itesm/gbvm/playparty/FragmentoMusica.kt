package mx.itesm.gbvm.playparty

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import kotlin.collections.ArrayList

class FragmentoMusica(var idMusica: String = "") : Fragment() {

    private lateinit var rvTarjetas: RecyclerView
    private val database = FirebaseDatabase.getInstance()
    private lateinit var referencia: DatabaseReference
    internal lateinit var callback: OnNewArrayListener
    var array: Array<Tarjeta> = arrayOf(Tarjeta())
    var adaptador: Adaptador = Adaptador(array, this@FragmentoMusica, idMusica)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val vista = inflater.inflate(R.layout.fragment_musica, container, false)
        rvTarjetas = vista.findViewById(R.id.rvTarjetas)

        println(idMusica)
        crearArrTarjetas()
        configurarRecyclerView()
        return vista
    }

    override fun onStart() {
        super.onStart()
        rvTarjetas.adapter = adaptador

    }

    private fun configurarRecyclerView() {
        val admLayout = LinearLayoutManager(AppPlayParty.context)
        rvTarjetas.layoutManager = admLayout
        rvTarjetas.adapter = adaptador
    }

    private fun crearArrTarjetas(){
        println("crearArrTarjetas")
        val miArreglo = ArrayList<Tarjeta>()
        referencia = database.getReference("/Usuarios/$idMusica/Playlist")
        println(idMusica)
        referencia.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach { registro ->
                    val tarjeta = registro.getValue(Tarjeta::class.java)!!
                    println(tarjeta)
                    miArreglo.add(tarjeta)
                }
                val arreglo1 = Array(miArreglo.size){Tarjeta()}
                array = miArreglo.toArray(arreglo1)

                //adaptador = Adaptador(array, this@FragmentoMusica, idMusica)
                //rvTarjetas.adapter = adaptador
                println("OnArrayChanged Fragmento Musica")
                println(snapshot)
                callback.onArrayChanged(array)
            }
        })
    }
    fun setOnNewArrayListener(callback: OnNewArrayListener){
        this.callback = callback
    }

    interface OnNewArrayListener{
        fun onArrayChanged(array :Array<Tarjeta>)
    }

}
