package mx.itesm.gbvm.playparty

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.BitmapRequestListener
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.renglon_musica.view.*
import java.net.URL


class Adaptador(var arrDatos: Array<Tarjeta>, var idMusica: String) : RecyclerView.Adapter<Adaptador.VistaRenglon>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VistaRenglon {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.renglon_musica, parent, false)
        return VistaRenglon(vista)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: VistaRenglon, position: Int) {
        val tarjeta = arrDatos[position]
        holder.vistaRenglon.buttonL.setOnClickListener {
            arrDatos[position].points++
            actualizarBD(tarjeta)
            val string = arrDatos[position].points.toString() + " Likes"
            holder.vistaRenglon.Count.text = string
            onArrayChanged()
            notifyDataSetChanged()

        }
        holder.set(tarjeta)
    }

    fun actualizarBD(tarjeta: Tarjeta){
        var referencia = FirebaseDatabase.getInstance().getReference("/Usuarios/$idMusica/Playlist/${tarjeta.idSong}/points")
        referencia.setValue(tarjeta.points)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun ordenarArray(){
        arrDatos.sortWith(Tarjeta.Comparator().reversed())
    }

    override fun getItemCount(): Int {
        return arrDatos.size
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun onArrayChanged(){
        ordenarArray()
        notifyDataSetChanged()
    }

    class VistaRenglon(val vistaRenglon: View) : RecyclerView.ViewHolder(vistaRenglon) {
        fun set(tarjeta: Tarjeta) {

            AndroidNetworking.get(tarjeta.idImagen).build().getAsBitmap(object : BitmapRequestListener {
                override fun onResponse(response: Bitmap?) {
                    vistaRenglon.imageAlbum.setImageBitmap(response!!)
                }

                override fun onError(anError: ANError?) {
                //Poner imagen generica
                }

            })

            vistaRenglon.Cancion.text = tarjeta.cancion
            vistaRenglon.Artista.text = tarjeta.artista
            val string = tarjeta.points.toString() + " Likes"
            vistaRenglon.Count.text = string

        }
    }


}