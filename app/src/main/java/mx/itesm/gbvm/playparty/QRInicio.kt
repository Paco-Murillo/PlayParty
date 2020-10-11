package mx.itesm.gbvm.playparty

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.qr_inicio.*


class QRInicio : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qr_inicio)

        val perfilFragment = FragmentoPerfil()
        val musicaFragment = FragmentoMusica()
        val mapaFragment = FragmentoMapa()
        val registroOIniciodesesion = Registro_o_InicioDeSesion()

        makeCurrentFragment(perfilFragment)

        bottom_nav.setOnNavigationItemSelectedListener {
            when (it.itemId){
                //R.id.nav_perfil -> makeCurrentFragment(perfilFragment)
                R.id.nav_Musica -> makeCurrentFragment(musicaFragment)
                R.id.nav_Mapa -> makeCurrentFragment(mapaFragment)
                R.id.nav_perfil-> makeCurrentFragment(registroOIniciodesesion)
            }
            true
        }
    }

    private fun makeCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container,fragment)
            commit()
        }

}
