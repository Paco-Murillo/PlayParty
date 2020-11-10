package mx.itesm.gbvm.playparty

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_musica.*
import kotlinx.android.synthetic.main.qr_inicio.*

class FragmentoMusica : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_musica, container, false)
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
        return arrayOf(
            Tarjeta("Song", "Artist", R.drawable.bts, 0),
            Tarjeta("Song", "Artist", R.drawable.bts, 0)
        )
    }

}
