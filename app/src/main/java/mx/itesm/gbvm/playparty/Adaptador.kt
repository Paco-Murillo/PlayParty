package mx.itesm.gbvm.playparty

import android.graphics.BitmapFactory
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.renglon_musica.view.*
import java.net.URL


class Adaptador(var arrDatos: Array<Tarjeta>, var idMusica: String) : RecyclerView.Adapter<Adaptador.VistaRenglon>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VistaRenglon {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.renglon_musica, parent, false)
        return VistaRenglon(vista)
    }

    override fun onBindViewHolder(holder: VistaRenglon, position: Int) {
        val tarjeta = arrDatos[position]
        holder.vistaRenglon.buttonL.setOnClickListener {
            arrDatos[position].points++
            actualizarBD(tarjeta)
            val string = arrDatos[position].points.toString() + " Likes"
            holder.vistaRenglon.Count.text = string
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
                        var referencia = FirebaseDatabase.getInstance().getReference("/Usuarios/$idMusica/Playlist/${registro.key!!}/points")
                        referencia.setValue(tarjeta.points)
                    }
                }
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun ordenarArray(){
        arrDatos.sortWith(Tarjeta.Comparator().reversed())
    }

    override fun getItemCount(): Int {
        return arrDatos.size
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun onArrayChanged(array: Array<Tarjeta>){
        arrDatos = array
        ordenarArray()
        notifyDataSetChanged()
    }

    class VistaRenglon(val vistaRenglon: View) : RecyclerView.ViewHolder(vistaRenglon) {
        fun set(tarjeta: Tarjeta) {
            val url = URL("http://image10.bizrate-images.com/resize?sq=60&uid=2216744464")
            val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
            vistaRenglon.imageAlbum.setImageBitmap(bmp)
            vistaRenglon.Cancion.text = tarjeta.cancion
            vistaRenglon.Artista.text = tarjeta.artista
            val string = tarjeta.points.toString() + " Likes"
            vistaRenglon.Count.text = string

        }
    }


}