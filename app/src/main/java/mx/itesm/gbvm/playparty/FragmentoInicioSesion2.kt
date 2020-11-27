package mx.itesm.gbvm.playparty

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.NonNull
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_fragmento_inicio_sesion2.*


class FragmentoInicioSesion2 : Fragment() {
     lateinit var mAuth: FirebaseAuth
     lateinit var QRInicio: QRInicio

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    fun btnIniciarSesion(){
        val email = etIniEmail.text.toString()
        val password = etNombreU.text.toString()
        if(email != "" && password != "") {
            iniciarSesion(email, password)
        }else if(email == ""){
            Toast.makeText(
                context, "No ha ingresado correo ",
                Toast.LENGTH_SHORT
            ).show()

        }else if(password ==""){
            Toast.makeText(
                context, "No ha ingresado contraseña",
                Toast.LENGTH_SHORT
            ).show()
        }else{
            Toast.makeText(
                context, "No has ingresado correo y contraseña",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    fun iniciarSesion(email: String, password: String){
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(
                QRInicio
            ) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        QRInicio, "Sesión Iniciada",
                        Toast.LENGTH_SHORT
                    ).show()
                    val baseDatos = FirebaseDatabase.getInstance()
                    val referencia = baseDatos.getReference("/Usuarios/")

                    referencia.addListenerForSingleValueEvent(object: ValueEventListener {
                        override fun onDataChange(@NonNull snapshot: DataSnapshot) {

                            for (registro in snapshot.children) {
                                val usuarioBuscado = registro.getValue(Usuario::class.java)!!
                                if (snapshot.exists()) {
                                    if (email == usuarioBuscado.email) {
                                        QRInicio.cambiarPerfil(FragmentoPerfil2.newInstance(usuarioBuscado,mAuth,QRInicio))
                                        break
                                    }
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(
                                QRInicio, "No se pueden leer los datos del Usuario",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })

                } else {
                    Toast.makeText(
                        QRInicio, "Fallo de autenticación.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mAuth = FirebaseAuth.getInstance()
        return inflater.inflate(R.layout.fragment_fragmento_inicio_sesion2, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        btnRegistrarse.setOnClickListener {
            btnIniciarSesion()
        }
        btnAtras.setOnClickListener {
            QRInicio.cambiarPerfil(Fragmento_RI.newInstance(QRInicio))
        }
    }

    companion object {

        fun newInstance( qrInicio: QRInicio) =
            FragmentoInicioSesion2().apply {
                arguments = Bundle().apply {
                   QRInicio = qrInicio
                }
            }
    }
}