package mx.itesm.gbvm.playparty

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.renglon_musica.view.*
//import com.squareup.picasso.Picasso

class Adaptador(
    var arrDatos: Array<Tarjeta>,
    fragmentoMusica: FragmentoMusica,
    var idMusica: String
) :
    RecyclerView.Adapter<Adaptador.VistaRenglon>(), FragmentoMusica.OnNewArrayListener {
    init {
        fragmentoMusica.setOnNewArrayListener(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VistaRenglon {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.renglon_musica, parent, false)
        return VistaRenglon(vista)
    }

    override fun onBindViewHolder(holder: VistaRenglon, position: Int) {
        val tarjeta = arrDatos[position]
        holder.vistaRenglon.buttonL.setOnClickListener {
            println("Oprimiste el boton $position")
            arrDatos[position].points++
            actualizarBD(tarjeta)
            val string = arrDatos[position].points.toString() + " Likes"
            holder.vistaRenglon.Count.text = string
            if(position != 0) {
                if (arrDatos[position].points > arrDatos[position - 1].points) {
                    var tar = arrDatos[position - 1].copy()
                    arrDatos[position - 1] = arrDatos[position]
                    arrDatos[position] = tar
                    notifyDataSetChanged()
                }
            }
        }
        holder.set(tarjeta)
    }
    fun actualizarBD(tarjeta: Tarjeta){
        var referencia = FirebaseDatabase.getInstance().getReference("/Usuarios/$idMusica/Playlist")
        referencia.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach { registro ->
                    val thisTarjeta = registro.getValue(Tarjeta::class.java)!!
                    if (tarjeta.idSong == thisTarjeta.idSong) {
                        actualizarBDKey(tarjeta, registro.key!!)
                    }
                }
            }
        })
    }

    fun ordenarArray(){
        arrDatos.sortWith(Tarjeta.Comparator().reversed())
    }

    fun actualizarBDKey(tarjeta: Tarjeta, string: String){
        var referencia = FirebaseDatabase.getInstance().getReference("/Usuarios/$idMusica/Playlist/$string/points")
        referencia.setValue(tarjeta.points) { error: DatabaseError?, _: DatabaseReference ->
            if (error == null) {
                println("Points Updated")
            }
        }
    }

    override fun getItemCount(): Int {
        return arrDatos.size
    }
    fun actualizarPuntos(){

    }

    override fun onArrayChanged(array: Array<Tarjeta>){
        for (i in arrDatos.indices){
            println(arrDatos[i])
        }
        arrDatos = array
        ordenarArray()
        notifyDataSetChanged()
    }

    class VistaRenglon(val vistaRenglon: View) : RecyclerView.ViewHolder(vistaRenglon) {
        fun set(tarjeta: Tarjeta) {
            vistaRenglon.Cancion.text = tarjeta.cancion
            vistaRenglon.Artista.text = tarjeta.artista
            val string = tarjeta.points.toString() + " Likes"
            vistaRenglon.Count.text = string
            vistaRenglon.buttonL.text = "Like"
            //Picasso.with(AppPlayParty.context).load(tarjeta.idImagen).into(vistaRenglon.imageAlbum)

        }
    }


}