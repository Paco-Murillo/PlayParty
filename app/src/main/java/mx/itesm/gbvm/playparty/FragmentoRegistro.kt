package mx.itesm.gbvm.playparty

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class FragmentoRegistro : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_registro, container, false)
    }

/*
    fun onCreate(savedInstanceState: Bundle?) {
        private lateinit var mAuth: FirebaseAuth
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.fragment_inicio_sesion)

        mAuth = FirebaseAuth.getInstance()




    fun onStart() {
        super.onStart()
        val currentUser = mAuth.currentUser
        // updateUI(currentUser)
    }

    fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            println("LOGIN exitoso")
            println("Usuario: ${currentUser?.displayName}")
            println("")
        } else {
            println("No has hecho login")
        }

    }
    fun crearCuenta(v: View){
        /* val correo = etCorreo.text.toString()
        val usuario = etUsuario.text.toString()
         val password = etPassword.text.toString()
         createAccount(correo, usuario, password) */

    }
    fun hacerLogout(v: View){
        mAuth.signOut()
    }

    fun createAccount(correo: String, usuario: String, password: String) {
        mAuth.createUserWithEmailAndPassword(correo,usuario, password)
            .addOnCompleteListener(
                this
            ) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    println("createUserWithEmail:success")
                    val user = mAuth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    println("createUserWithEmail:failure${task.exception}")
                    /* Toast.makeText(
                         this@MainActivity, "Authentication failed.",
                         Toast.LENGTH_SHORT
                     ).show()*/
                    updateUI(null)
                }

            }
    }
    fun hacerLogin(v: View){
        /*if(val correo == "${email}"){
        correo = etCorreo.text.toString()
        } elif(val usuario == "${usuario}") {
        usuario = etCorreo.text.toString()}
        val password = etPassword.text.toString()
        if(etCorreo.getValue($"{usuario}") ==$"{usuario}"){
        signIn(usuario, password)
        } elif(etCorreo.getValue($"{email})"  == $"{email})
        signIn(correo, password)}
        */


    }
    fun signIn(correo: String, password: String){
        mAuth.signInWithEmailAndPassword(correo, password)
            .addOnCompleteListener(
                this
            ) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    println("signInWithEmail:success")
                    val user = mAuth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    println("signInWithEmail:failure ${task.exception}")
                    /*Toast.makeText(
                        this@FragmentoInicioSesion, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()*/
                    updateUI(null)
                }

            }
    }
        fun OthersignIn(usuario: String, password: String){
            mAuth.signInWithEmailAndPassword(usuario, password)
                .addOnCompleteListener(
                    this
                ) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        println("signInWithEmail:success")
                        val user = mAuth.currentUser
                        updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        println("signInWithEmail:failure ${task.exception}")
                        /*Toast.makeText(
                            this@FragmentoInicioSesion, "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()*/
                        updateUI(null)
                    }

                }
        }
}*/
}

