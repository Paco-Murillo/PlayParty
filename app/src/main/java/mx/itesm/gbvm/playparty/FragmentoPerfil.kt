package mx.itesm.gbvm.playparty

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.ListFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_perfil.*

class FragmentoPerfil(user: Usuario, buscar:Boolean) : ListFragment() {
    var usuario: Usuario = user
    val flagBuscar: Boolean = buscar

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        /*
        textfNombreU = view?.findViewById(R.id.tfNombreU) as TextView
        textfEmail = view?.findViewById(R.id.tfemail) as TextView
        textfPassword = view?.findViewById(R.id.tfpassword) as TextView
        textfFechaN = view?.findViewById(R.id.tfFechaN) as TextView
         */

    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val vista = super.onCreateView(inflater, container, savedInstanceState)
        return  vista
    }

    override fun onStart() {
        super.onStart()

        leerDatosUsuario()

        println(usuario.nombreU)
    /*
        if (usuario!=null) {
            mostrarUsuario()
        }

     */
    }
    private fun leerDatosUsuario(){
        if (flagBuscar){
            val baseDatos = FirebaseDatabase.getInstance()
            val referencia = baseDatos.getReference("/Usuarios/")

            referencia.addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    for (registro in snapshot.children){
                        println("Prueba con luismiiiiiiiiiiiiiiii")
                        println(registro)
                        println(registro.value.toString())
                        val usuarioBuscado = registro.getValue(Usuario::class.java)!!
                        if(usuario.email   == usuarioBuscado.email){

                            tfemail.setText(usuarioBuscado.email)
                            break
                        }
                        /*
                        if(usuario == usuarioActual){
                            //tfNombre.text = ${usuario.nom}
                        }
                        //si el usuario es igual al current user lleno la información!!!!!!!!!!!!!!!!!!!!!!!!!

                         */

                    }



                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context,"No se pueden leer los datos del Usuario",
                        Toast.LENGTH_SHORT).show()
                }

            })
        }


        /*
        val baseDatos = FirebaseDatabase.getInstance()
        val referencia = baseDatos.getReference("/Usuarios/")
        //val usuarioActual = FirebaseAuth.getInstance().toString()
        //val currentUser = FirebaseUser?

        referencia.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                for (registro in snapshot.children){
                    println(registro)
                    println(registro.value.toString())
                    val usuario = registro.getValue(Usuario::class.java)
                    if(usuario.nombreU == )
                    /*
                    if(usuario == usuarioActual){
                        //tfNombre.text = ${usuario.nom}
                    }
                    //si el usuario es igual al current user lleno la información!!!!!!!!!!!!!!!!!!!!!!!!!

                     */

                }



            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context,"No se pueden leer los datos del Usuario",
                Toast.LENGTH_SHORT).show()
            }

        })

         */
    }

    private fun mostrarUsuario() {

        /*val adaptador = ArrayAdapter<String>(context!!,
                android.R.layout.simple_list_item_1,arrUsuarios)
                listAdapter = adaptador*/

    }

}