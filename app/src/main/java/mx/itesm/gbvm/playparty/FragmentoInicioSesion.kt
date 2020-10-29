package mx.itesm.gbvm.playparty

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.provider.FontsContractCompat.FontRequestCallback.RESULT_OK
import androidx.media.MediaBrowserServiceCompat.RESULT_OK
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.fragment_inicio_sesion.*
import kotlinx.android.synthetic.main.qr_inicio.*
import kotlin.math.sign


class FragmentoInicioSesion() : Fragment() {
    //var listener: ListenerGoogle? = null

    public final var LOGIN_GOOGLE: Int = 500
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    var btnGoogleSignIn: GoogleSignIn? =null
    private var mSignInClicked: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_inicio_sesion, container, false)

    }
    /*
/*
    fun configurarBtnGoogle() {
        sign_in_button.setOnClickListener{
            val intGoogle = mGoogleSignInClient.signInIntent
            startActivityForResult(intGoogle, LOGIN_GOOGLE)
        }
    }
    */

    override fun onStart() {
        super.onStart()
        onClickedSignInGoogle(main = View(context))
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode === LOGIN_GOOGLE) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
            /*if(resultCode != QRInicio.RESULT_OK) {
                mSignInClicked=false
            }*/
        }
    }
    private fun onClickedSignInGoogle(main: View) {
        if(main.id ==R.id.sign_in_button){
            mSignInClicked = true
            //configurarBtnGoogle()
        }
    }    private fun handleSignInResult(task: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount = task.getResult(ApiException::class.java)!!

            // Signed in successfully, show authenticated UI.

            //cambio de fragmento es la actividad
            //Intent para cambiar a una nueva actividad
            val intBtnGoogle = Intent(activity, QRInicio::class.java)
            startActivity(intBtnGoogle)
            //Avisar a la actividad a través de un listener que el login fue exitoso
            //Avisar hacer un cambio de fragmento
            //itemClicked()

        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            println( "signInResult:failed code= ${e.statusCode}")
            updateUIGoogle(null)
        }
    }
    private fun updateUIGoogle(account: GoogleSignInAccount?) {

        if(account != null){
            println("LOGIN con Google")
            println("Nombre: ${account.displayName}")
            println("Correo: ${account.email}")
            println("ID: ${account.id}")
        }else{
            println("No ha hecho login con Google")
        }
    }

    fun signOutGoogle(v: View){
        mGoogleSignInClient.signOut()
    }

*/
}
/*
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private val LOGIN_GOOGLE: Int = 500
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()
        //Autenticar con cuenta de google

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        val account = GoogleSignIn.getLastSignedInAccount(this)
        updateUIGoogle(account)

        configurarBtnGoogle()

        sign_in_button.setSize(SignInButton.SIZE_WIDE)
    }

    private fun configurarBtnGoogle() {
        sign_in_button.setOnClickListener{
            val intGoogle = mGoogleSignInClient.signInIntent
            startActivityForResult(intGoogle, LOGIN_GOOGLE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode === LOGIN_GOOGLE) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(task: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount = task.getResult(ApiException::class.java)!!

            // Signed in successfully, show authenticated UI.
            updateUIGoogle(account)
            //cambio de fragmento es la actividad
            //Avisar a la actividad a través de un listener

        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            println( "signInResult:failed code= ${e.statusCode}")
            updateUIGoogle(null)
        }
    }
    fun signOutGoogle(v: View){
        mGoogleSignInClient.signOut()
    }

    private fun updateUIGoogle(account: GoogleSignInAccount?) {
        if(account != null){
            println("LOGIN con Google")
            println("Nombre: ${account.displayName}")
            println("Correo: ${account.email}")
            println("ID: ${account.id}")
        }else{
            println("No ha hecho login con Google")
        }
    }


    override fun onStart() {
        super.onStart()
        val currentUser = mAuth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            println("LOGIN exitoso")
            println("Usuario: ${currentUser?.displayName}")
        } else {
            println("No has hecho login")
        }

    }
    fun crearCuenta(v: View){
        val correo = etCorreo.text.toString()
        val password = etPassword.text.toString()
        createAccount(correo, password)

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
                    Toast.makeText(
                        this@MainActivity, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                    updateUI(null)
                }

            }
    }
    fun hacerLogin(v: View){
        val correo = etCorreo.text.toString()
        val password = etPassword.text.toString()
        signIn(correo, password)

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
                    Toast.makeText(
                        this@MainActivity, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                    updateUI(null)
                }

            }
    }
} */