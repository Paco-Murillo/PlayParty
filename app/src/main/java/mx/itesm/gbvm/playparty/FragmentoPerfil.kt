package mx.itesm.gbvm.playparty

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_perfil.*

class FragmentoPerfil : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_perfil, container, false)
    }

    fun btnRegresar(v: View){
        val intRegresar = Intent(activity, QRInicio::class.java)
        startActivity(intRegresar)

    }
}