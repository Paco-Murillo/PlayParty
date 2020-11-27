package mx.itesm.gbvm.playparty

import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_fragmento_registro2.*


class Fragmento_registro2 : Fragment() {
    lateinit var QRInicio: QRInicio
    lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mAuth = FirebaseAuth.getInstance()
        return inflater.inflate(R.layout.fragment_fragmento_registro2, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        btnRegistrarse.setOnClickListener {
            val usuario = etNombreU.text.toString()
            val email = etEmail.text.toString()
            val password = etIniEmail.text.toString()
            val latitud = etLatitud.text.toString()
            val longitud = etLongitud.text.toString()
            val playlist = etPlayList.text.toString()
            if(email != "" && password != "" && usuario != "" && latitud != "" && longitud != "" && playlist != "") {
                registrarCuenta(email, password)
            }
            else
            {
                Toast.makeText(
                    context, "llene todos los campos",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun registrarCuenta(email: String, password: String) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(
                QRInicio
            ) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        QRInicio, "Cuenta creada",
                        Toast.LENGTH_SHORT
                    ).show()
                    val usuario = etNombreU.text.toString()
                    val latitud = etLatitud.text.toString()
                    val longitud = etLongitud.text.toString()
                    val playlist = etPlayList.text.toString()

                    val baseDatos = FirebaseDatabase.getInstance()
                    val referencia = baseDatos.getReference("/Usuarios").push()
                    val llave = referencia.key
                    if(llave != null) {
                        val user =Usuario(llave, email, password, usuario, latitud, longitud, playlist)
                        referencia.setValue(user)
                        QRInicio.cambiarPerfil(FragmentoPerfil2.newInstance(user, mAuth, QRInicio))
                    }
                    else{
                        Toast.makeText(
                            QRInicio, "Error al crear la Base de datos.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                } else {
                    Toast.makeText(
                        QRInicio, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    companion object {
        @JvmStatic
        fun newInstance(qrInicio: QRInicio) =
            Fragmento_registro2().apply {
                arguments = Bundle().apply {
                    QRInicio = qrInicio
                }
            }
    }
}