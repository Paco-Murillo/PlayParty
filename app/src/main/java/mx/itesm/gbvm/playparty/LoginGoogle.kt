package mx.itesm.gbvm.playparty

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import kotlinx.android.synthetic.main.qr_inicio.*

class LoginGoogle : AppCompatActivity() {

    private val LOGIN_GOOGLE: Int = 500
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var mAuth: FirebaseAuth
    private lateinit var listaDatabase: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_login_google)

        mAuth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        val account  = GoogleSignIn.getLastSignedInAccount(this)
        updateUIGoogle(account)

        val intGoogle = mGoogleSignInClient.signInIntent
        startActivityForResult(intGoogle, LOGIN_GOOGLE)

    }

    private fun updateUIGoogle(account: GoogleSignInAccount?) {
        if(account != null){
            println("Login Google exitoso")
            println("Nombre: ${account.displayName}")
            println("Correo: ${account.email}")
            println("ID: ${account.id}")
        }else{
            println("No a hecho login en Google")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == LOGIN_GOOGLE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(task: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount = task.getResult(ApiException::class.java)!!

            // Signed in successfully, show authenticated UI.
            updateUIGoogle(account)
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            println("signInResult:failed code= ${e.statusCode}")
            updateUIGoogle(null)
        }
    }

}