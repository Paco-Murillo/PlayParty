package mx.itesm.gbvm.playparty

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_fragmento_perfil2.*


class FragmentoPerfil2 : Fragment() {
    lateinit var mAuth: FirebaseAuth
    lateinit var usuario: Usuario
    lateinit var QRInicio: QRInicio

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_fragmento_perfil2, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        tfNombreU.text = usuario.nombreU
        btnCerrar.setOnClickListener {
            mAuth.signOut()
            QRInicio.makeCurrentFragment(FragmentoInicioSesion2.newInstance(QRInicio))
        }
    }

    companion object {

        @JvmStatic fun newInstance( user: Usuario,aut: FirebaseAuth, qrInicio: QRInicio) =
                FragmentoPerfil2().apply {
                    arguments = Bundle().apply {
                        usuario = user
                        mAuth = aut
                        QRInicio = qrInicio
                    }
                }
    }
}