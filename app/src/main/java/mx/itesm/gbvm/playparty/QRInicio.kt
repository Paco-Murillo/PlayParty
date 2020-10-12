package mx.itesm.gbvm.playparty

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.fragment_registrar.*
import kotlinx.android.synthetic.main.qr_inicio.*


class QRInicio : AppCompatActivity(), GPSListener {
    private var gps: GPS? = null
    private val CODIGO_PERMISO_GPS: Int = 200
    private var posicion: Location? = null

    public var idMusica = null
    public var BotonValido = false

    //Google Autenticador
    private val LOGIN_GOOGLE: Int = 500
    private lateinit var mGoogleSignInClient: String
    private lateinit var mAuth: FirebaseAuth

    private fun makeCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, fragment)
            commit()
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        configurarGPS()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qr_inicio)

        mAuth = FirebaseAuth.getInstance()

        val perfilFragment = FragmentoPerfil()
        val musicaFragment = FragmentoMusica()
        val mapaFragment = FragmentoMapa()
        val registroOIniciodesesion = Registro_o_InicioDeSesion()
        val registroFragment = FragmentoRegistro()
        val inicioSesion = FragmentoInicioSesion()
        val QRFragment = FragmentoQR()

        //Fragmento inicial
        makeCurrentFragment(Registro_o_InicioDeSesion())
        bottom_nav.setOnNavigationItemSelectedListener {
            when (it.itemId){
                R.id.nav_perfil -> makeCurrentFragment(registroOIniciodesesion)
                R.id.nav_Musica ->
                    if (BotonValido) {
                        makeCurrentFragment(musicaFragment)
                    } else {
                        makeCurrentFragment(QRFragment)
                    }
                R.id.nav_Mapa -> makeCurrentFragment(mapaFragment)
            }
            true
        }
    }

    //Botones
    fun validarID(v: View){

    }
    fun btnFragInicioSesion(v: View){
        makeCurrentFragment(FragmentoInicioSesion())
    }
    fun btnFragRegistro(v: View){
        makeCurrentFragment(FragmentoRegistro())
    }
    fun btnFragBackIniReg(v: View){
        makeCurrentFragment(Registro_o_InicioDeSesion())
    }

    //GPS
    private fun configurarGPS() {
        gps = GPS()
        gps?.gpsListener = this
        gps?.inicializar(this)
        if (verificarPermisos()) {
            iniciarActualizacionesPosicion()
        }else {
            pedirPermisos()
        }
    }

    private fun pedirPermisos() {
        val requiereJustificacion = ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (requiereJustificacion) {
            mostrarDialogo()
        }else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                CODIGO_PERMISO_GPS
            )
        }
    }

    private fun verificarPermisos(): Boolean {
        val estadoPermiso = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        return estadoPermiso == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CODIGO_PERMISO_GPS) {
            if (grantResults.isEmpty()) {
            } else if (grantResults.first() == PackageManager.PERMISSION_GRANTED) {
                gps?.iniciarActualizaciones()
            } else { // Permiso negado
                val dialogo = AlertDialog.Builder(this)
                dialogo.setMessage("Esta app requiere GPS, ¿Quieres configurar el permiso?")
                    .setPositiveButton("Aceptar", DialogInterface.OnClickListener { dialog, which ->
                        val intent = Intent()
                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        val uri = Uri.fromParts(
                            "package",
                            BuildConfig.APPLICATION_ID, null
                        )
                        intent.data = uri
                        //intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK startActivity(intent)
                    })
                    .setNeutralButton("Cancelar", DialogInterface.OnClickListener { dialog, which ->
                        println("No hay forma de usar gps, cerrar la actividad")
                        //Deshabilitar funcionalidad
                    })
                    .setCancelable(false)
                dialogo.show()
            }
        }
    }

    private fun iniciarActualizacionesPosicion() {
        gps?.iniciarActualizaciones()
    }

    private fun mostrarDialogo() {
        val dialogo = AlertDialog.Builder(this)
        dialogo.setMessage("Necesitas GPS para esta app.")
            .setPositiveButton("Aceptar") { dialog, which ->
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    CODIGO_PERMISO_GPS
                )
            }
            .setNeutralButton("Cancelar", null)
        dialogo.show()
    }

    override fun actualizarPosicion(posicion: Location) {
        this.posicion = posicion
    }

    //Google Autenticador
    override fun onStart() {
        super.onStart()
        val currentUser = mAuth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if(currentUser!= null) {
            println("LogIn exitoso")
            println("Usuario: ${currentUser?.displayName}")
        }else{
            println("No has hecho login")
        }
        //Actualizar con la sesión iniciada
    }
    fun btnRegistrarCuenta(v: View){
        val email = etEmail.text.toString()
        val password = etPassword.text.toString()
        registrarCuenta(email, password)
        makeCurrentFragment(FragmentoPerfil())
    }
    fun registrarCuenta(email: String, password:String){
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(
                this
            ) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    println("createUserWithEmail:success")
                    val user = mAuth.currentUser
                    Toast.makeText(
                        this@QRInicio, "Cuenta creada",
                        Toast.LENGTH_SHORT
                    ).show()
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    println("createUserWithEmail:failure${task.exception}")
                    Toast.makeText(
                        this@QRInicio, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                    updateUI(null)
                }

            }
    }
    fun btnIniciarSesion(v: View){
        val email = etEmail.text.toString()
        val password = etPassword.text.toString()
        iniciarSesion(email, password)
        makeCurrentFragment(FragmentoPerfil())
    }
    fun iniciarSesion(email: String, password: String){
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(
                this
            ) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    println("signInWithEmail:success")
                    Toast.makeText(
                        this@QRInicio, "Sesión Iniciada",
                        Toast.LENGTH_SHORT
                    ).show()
                    val user = mAuth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    println("signInWithEmail:failure ${task.exception}")
                    Toast.makeText(
                        this@QRInicio, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                    //Podemos apagar botones o lanzar a una segunda actividad
                    updateUI(null)
                }

            }
    }
    fun btnCerrarSesion(v:View){
        mAuth.signOut()
        println("logOut exitoso")
        Toast.makeText(
            this@QRInicio, "Sesión Cerrada",
            Toast.LENGTH_SHORT
        ).show()
        makeCurrentFragment(Registro_o_InicioDeSesion())
    }
}
