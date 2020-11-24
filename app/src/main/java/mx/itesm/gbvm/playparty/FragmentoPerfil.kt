package mx.itesm.gbvm.playparty

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.ListFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FragmentoPerfil : ListFragment() {
    lateinit var arrUsuarios: MutableList<String>
    lateinit var tfNombre: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arrUsuarios = mutableListOf()
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val vista = super.onCreateView(inflater, container, savedInstanceState)
        //tfNombre = vista?.findViewById<TextView>(R.id.tfNombre)!!
        return  vista
    }

    override fun onStart() {
        super.onStart()
        leerDatosUsuario()
    }
    private fun leerDatosUsuario(){
        val baseDatos = FirebaseDatabase.getInstance()
        val referencia = baseDatos.getReference("/Usuarios/")
        //val usuarioActual = FirebaseAuth.getInstance().toString()
        //val currentUser = FirebaseUser?

        referencia.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                arrUsuarios.clear()
                for (registro in snapshot.children){
                    println(registro)
                    println(registro.value.toString())
                    val usuario = registro.getValue(Usuario::class.java)
                    /*
                    if(usuario == usuarioActual){
                        //tfNombre.text = ${usuario.nom}
                    }
                    //si el usuario es igual al current user lleno la información!!!!!!!!!!!!!!!!!!!!!!!!!

                     */
                    arrUsuarios.add("${usuario?.userID},${usuario?.email},${usuario?.password},${usuario?.nombreU},${usuario?.fechaN}")
                }
                val adaptador = ArrayAdapter<String>(context!!,
                android.R.layout.simple_list_item_1,arrUsuarios)
                listAdapter = adaptador
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context,"No se pueden leer los datos del Usuario",
                Toast.LENGTH_SHORT).show()
            }

        })
    }
}