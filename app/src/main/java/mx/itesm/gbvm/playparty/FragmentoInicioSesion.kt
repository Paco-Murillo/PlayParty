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
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import kotlinx.android.synthetic.main.fragment_inicio_sesion.*
import kotlinx.android.synthetic.main.fragment_inicio_sesion.view.*
import kotlinx.android.synthetic.main.qr_inicio.*
import kotlin.math.sign


class FragmentoInicioSesion : Inicio_Registro() {
    lateinit var mView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.fragment_inicio_sesion, container, false)

        return mView

    }
    companion object{
        fun newInstance(): Inicio_Registro = FragmentoInicioSesion()
    }
}

