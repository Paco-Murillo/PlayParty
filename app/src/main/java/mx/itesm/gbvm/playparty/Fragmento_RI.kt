package mx.itesm.gbvm.playparty

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_fragmento__r_i.*


class Fragmento_RI : Fragment() {
    lateinit var QRInicio: QRInicio

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragmento__r_i, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        btnSesion.setOnClickListener {
            QRInicio.cambiarPerfil(FragmentoInicioSesion2.newInstance(QRInicio))
        }
        btnReg.setOnClickListener {
            QRInicio.cambiarPerfil(FragmentoRegistro())
        }
    }
    companion object {
        fun newInstance(qrInicio: QRInicio) =
            Fragmento_RI().apply {
                arguments = Bundle().apply {
                    QRInicio = qrInicio
                }
            }
    }
}