package mx.itesm.gbvm.playparty

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.fragment.app.ListFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_perfil.*
import okhttp3.internal.cache.DiskLruCache

class FragmentoPerfil(user: Usuario, buscar:Boolean, fragmentoBack:Inicio_Registro) : Fragment() {
    var fragmentoBack = fragmentoBack
    private lateinit var callback: OnNewArrayListener
    var usuario: Usuario = user
    val flagBuscar: Boolean = buscar
    var flagEncontrado = false
    lateinit var textNombreU: TextView
    lateinit var textPassword: TextView
    lateinit var textEmail: TextView
    lateinit var txSpotify: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val vista = inflater.inflate(R.layout.fragment_perfil, container, false)

        textNombreU = vista.findViewById<TextView>(R.id.tfNombreU)
        textEmail = vista.findViewById<TextView>(R.id.tfemail)
        textPassword = vista.findViewById<TextView>(R.id.tfpassword)
        txSpotify = vista.findViewById(R.id.txSpotify)

        return  vista
    }

    override fun onStart() {
        super.onStart()

        leerDatosUsuario()
        if (flagEncontrado) {
            mostrarUsuario()
        }
        println("onstart")
        println(flagEncontrado)
        println(usuario)
    }

    override fun onResume() {
        super.onResume()
        if (flagEncontrado){
            mostrarUsuario()
        }
        println("onresume")
        println(flagEncontrado)
        println(usuario)
    }

    private fun leerDatosUsuario(){
        if (flagBuscar){
            val baseDatos = FirebaseDatabase.getInstance()
            val referencia = baseDatos.getReference("/Usuarios/")

            referencia.addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(@NonNull snapshot: DataSnapshot) {

                    for (registro in snapshot.children){
                        val usuarioBuscado = registro.getValue(Usuario::class.java)!!
                        if(snapshot.exists() ){
                            if(usuario.email   == usuarioBuscado.email){
                                println("Encontrado ---------------------------------------------------")
                                flagEncontrado = true
                                println(flagEncontrado)
                                usuario = usuarioBuscado
                                println(usuario)
                                mostrarUsuario()
                                println("Encontrado ---------------------------------------------------")
                                break
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context,"No se pueden leer los datos del Usuario",
                        Toast.LENGTH_SHORT).show()
                }

            })
        }
    }

    private fun mostrarUsuario() {
        println("Mostrar usuario")
        textEmail.text = (usuario.email)
        textNombreU.text = (usuario.nombreU)
        textPassword.text = (usuario.password)
        txSpotify.text = (usuario.playlist)
    }

    override fun onStop() {
        super.onStop()
        callback.onFragmentPerfilStopped(usuario)
    }

    fun setOnFragmentPerfilStoppedListener(callback: OnNewArrayListener){
        this.callback = callback
    }

    interface OnNewArrayListener{
        fun onFragmentPerfilStopped(usuario: Usuario)
    }
    companion object{
        fun newInstance(buscar: Boolean, fragmentoBack: Inicio_Registro, user: Usuario):
                FragmentoPerfil = FragmentoPerfil(user, buscar, fragmentoBack)
    }
}
