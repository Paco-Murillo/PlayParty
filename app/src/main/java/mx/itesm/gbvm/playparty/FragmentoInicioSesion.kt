package mx.itesm.gbvm.playparty

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class FragmentoInicioSesion : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_inicio_sesion, container, false)
    }
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
        } else {
            println("No has hecho login")
        }

    }
    fun crearCuenta(v: View){
       /* val correo = etCorreo.text.toString()
        val password = etPassword.text.toString()
        createAccount(correo, password) */

    }
    fun hacerLogout(v: View){
        mAuth.signOut()
    }

    fun createAccount(correo: String, password: String) {
        mAuth.createUserWithEmailAndPassword(correo, password)
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
        /*val correo = etCorreo.text.toString()
        val password = etPassword.text.toString()
        signIn(correo, password)*/

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
}*/

