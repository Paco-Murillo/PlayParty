package mx.itesm.gbvm.playparty

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class Mapa : AppCompatActivity() {
    //Implementar el fragmento del mapa
    //Buscar todos los puntos en la base de datos y ponerlos
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mapa)
    }
    fun CambiarAMapa(v: View){
        val sign = Intent(this, Mapa::class.java)
        startActivityForResult(sign, 500)
    }
    fun CambiarACuenta(v: View){
        val sign = Intent(this, Registro::class.java)
        startActivityForResult(sign, 500)
    }
    fun CambiarAMusica(v: View) {
        var sign = Intent(this, QRInicio::class.java)
        if (true) {
            sign = Intent(this, Musica::class.java)
        }
        startActivityForResult(sign, 500)
    }
}