package mx.itesm.gbvm.playparty

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.nfc.tech.Ndef.get
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.renglon_musica.view.*
import java.net.HttpURLConnection
import java.net.URL
import android.widget.Toast
import com.squareup.picasso.Picasso
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
            arrDatos[position].points++
            holder.vistaRenglon.Count.text = (arrDatos[position].points).toString() + " Likes"
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

    override fun getItemCount(): Int {
        return arrDatos.size
    }
    fun actualizarPuntos(){

    }

    class VistaRenglon(val vistaRenglon: View) : RecyclerView.ViewHolder(vistaRenglon) {
        fun set(tarjeta: Tarjeta) {
            vistaRenglon.Cancion.text = tarjeta.cancion
            vistaRenglon.Artista.text = tarjeta.artista
            vistaRenglon.Count.text = tarjeta.points.toString() + " Likes"
            Picasso.with(AppPlayParty.context).load(tarjeta.idImagen).into(vistaRenglon.imageAlbum)

        }
    }
}