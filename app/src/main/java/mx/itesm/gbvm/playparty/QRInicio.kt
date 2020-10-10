package mx.itesm.gbvm.playparty

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.qr_inicio.*

class QRInicio : AppCompatActivity() {
    var Codigo = null //Obtener texto y enviar a cada actividad
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_owner)
    }
    fun BuscarPlaylist(v: View){
        //Codigo = ItBuscar //Obtener texto y checar en la base de datos
        //Busca la playlist en la base de datos si esta te mete a ella
        val sign = Intent(this, Musica::class.java)
        startActivityForResult(sign, 500)
    }
    fun CambiarAMapa(v: View){
        val sign = Intent(this, Mapa::class.java)
        startActivityForResult(sign, 500)
    }
    fun CambiarAQR(v: View){
        val sign = Intent(this, QRInicio::class.java)
        startActivityForResult(sign, 500)
    }
    fun CambiarACuenta(v: View){
        val sign = Intent(this, Registro::class.java)
        startActivityForResult(sign, 500)
    }
    fun CambiarAMusica(v: View){
        var sign = Intent(this, QRInicio::class.java)
        if (true) {
            sign = Intent(this, Musica::class.java)
        }
        startActivityForResult(sign, 500)
    }
}
