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

class FragmentoMusica(idMusica: String = "-MN6tS4qVNtOYELe3nrp") : Fragment() {

    var idMusica = idMusica
    private lateinit var rvTarjetas: RecyclerView
    private val database = FirebaseDatabase.getInstance()
    private lateinit var referencia: DatabaseReference
    internal lateinit var callback: OnNewArrayListener
    lateinit var array: Array<Tarjeta>
    lateinit var adaptador: Adaptador

    var flagAdaptador = true

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

    override fun onStart() {
        super.onStart()

        if(this::adaptador.isInitialized) {
            rvTarjetas.adapter = adaptador
        }
    }

    private fun configurarRecyclerView() {
        val admLayout = LinearLayoutManager(AppPlayParty.context)
        crearArrTarjetas()
        rvTarjetas.layoutManager = admLayout
    }

    private fun crearArrTarjetas(){

        val miArreglo = ArrayList<Tarjeta>()
        referencia = database.getReference("/Usuarios/$idMusica/Playlist")
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
                if(flagAdaptador){
                    adaptador = Adaptador(array, this@FragmentoMusica)
                    rvTarjetas.adapter = adaptador
                }else{
                    callback.onArrayChanged(array)
                }
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
