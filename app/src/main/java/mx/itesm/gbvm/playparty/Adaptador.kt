package mx.itesm.gbvm.playparty

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.renglon_musica.view.*
import java.net.HttpURLConnection
import java.net.URL
import android.widget.Toast
import java.io.IOException

class Adaptador(private val arrDatos: Array<Tarjeta>) :
    RecyclerView.Adapter<Adaptador.VistaRenglon>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VistaRenglon {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.renglon_musica, parent, false)
        return VistaRenglon(vista)
    }

    override fun onBindViewHolder(holder: VistaRenglon, position: Int) {
        val tarjeta = arrDatos[position]
        holder.vistaRenglon.buttonL.setOnClickListener {
            println("Oprimiste el boton $position")
            holder.vistaRenglon.Count.text = (arrDatos[position].points + 1).toString() + "Likes"
            if(arrDatos[position].points > arrDatos[position - 1].points){
                var tar = arrDatos[position - 1].copy()
                arrDatos[position - 1] = arrDatos[position]
                arrDatos[position] = arrDatos[position - 1]


            }
        }
        holder.set(tarjeta)
    }

    override fun getItemCount(): Int {
        return arrDatos.size
    }
    fun actualizarPuntos(){

    }

    class VistaRenglon(val vistaRenglon: View) : RecyclerView.ViewHolder(vistaRenglon) {
        private var loadedImage: Bitmap? = null
        fun set(tarjeta: Tarjeta) {
            vistaRenglon.Cancion.text = tarjeta.cancion
            vistaRenglon.Artista.text = tarjeta.artista
            vistaRenglon.Count.text = tarjeta.points.toString() + " Likes"
            vistaRenglon.imageAlbum.setImageResource(tarjeta.idImagen)

        }
    }
}