package mx.itesm.gbvm.playparty

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_fragmento_q_r2.*

class FragmentoQR2 : Fragment() {
    lateinit var QRInicio: QRInicio

    var idMusica = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 6670){
            if(resultCode == Activity.RESULT_OK){
                if (data != null) {
                    valid(data.data.toString())
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        btnQR.setOnClickListener {
            val scanQrIntent = Intent(QRInicio,Scanner::class.java)
            startActivityForResult(scanQrIntent, 6670)
        }
        btnBuscar.setOnClickListener {
            idMusica = etBuscarId.toString()
            valid(idMusica)
        }
    }
    fun valid(idMusica: String){
        this.idMusica = idMusica
        var boolean = false
        val baseDatos = FirebaseDatabase.getInstance()
        val referencia = baseDatos.getReference("/Usuarios")
        referencia.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                for (registro in snapshot.children) {
                    val usuario =
                        registro.getValue(Usuario::class.java)
                    if (usuario != null) {
                        val id = usuario.userID
                        if (id == idMusica) {
                            QRInicio.cambiarMusica(FragmentoMusica2.newInstance(QRInicio,idMusica))
                            boolean = true
                        }

                    }
                }
                if(!boolean){
                    Toast.makeText(
                        QRInicio, "ID Invalido!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_fragmento_q_r2, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance(qrInicio: QRInicio) =
            FragmentoQR2().apply {
                arguments = Bundle().apply {
                    QRInicio = qrInicio
                }
            }
    }
}