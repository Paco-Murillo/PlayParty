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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_qr.*
import kotlinx.android.synthetic.main.fragment_registro.*
import kotlinx.android.synthetic.main.qr_inicio.*

class   QRInicio : AppCompatActivity(), GPSListener {
    private var gps: GPS? = null
    private val CODIGO_PERMISO_GPS: Int = 200
    private var posicion: Location? = null

    //private val bar:BarcodeGra

    // Validacion ID
    var idMusica:String = ""
    var BotonValido: Boolean = false
    private  val database = FirebaseDatabase.getInstance()
    private lateinit var referencia: DatabaseReference
    private var Pablo = ""


    //Google Autenticador
    private val LOGIN_GOOGLE: Int = 500
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var mAuth: FirebaseAuth
    private lateinit var listaDatabase: DatabaseReference

    //qr
    private val RC_BARCODE_CAPTURE = 6669
    private val SCAN_QR = 6670


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

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        val account  = GoogleSignIn.getLastSignedInAccount(this)
        updateUIGoogle(account)



        val musicaFragment = FragmentoMusica()
        val mapaFragment = FragmentoMapa()
        val registroOIniciodesesion = Registro_o_InicioDeSesion()
        val QRFragment = FragmentoQR()
        val fragInicio = FragmentoInicioSesion()
        val fragRegistro = FragmentoRegistro()
        val fragPerfil = FragmentoPerfil()

        //Fragmento inicial
        makeCurrentFragment(QRFragment)
        bottom_nav.setOnNavigationItemSelectedListener {
            when (it.itemId){
                R.id.nav_perfil ->
                    if(Pablo == "Inicio"){
                        makeCurrentFragment(fragInicio)
                    }else if(Pablo == "Registro"){
                        makeCurrentFragment(fragRegistro)
                    }else if(Pablo == "Perfil"){
                        makeCurrentFragment(fragPerfil)
                    }else{
                        makeCurrentFragment(registroOIniciodesesion)
                    }
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

    private fun instanciarLista(string: String){
        listaDatabase = database.getReference("https://playparty-a9dd9.firebaseio.com/Lists/active/${string}")
    }

    //Botones
   /*  fun scanQRCode(v: View){
        val detector = BarcodeDetector.Builder(applicationContext)
            .setBarcodeFormats(Barcode.DATA_MATRIX or Barcode.QR_CODE)
            .build()
        if (!detector.isOperational) {
            mostrarDialogo("No se pudo configurar el detector")
            return
        }
        val frame: Frame = Frame.Builder().setBitmap(BitmapFactory.decodeFile("")).build()
        val barcodes = detector.detect(frame)
        val thisCode = barcodes.valueAt(0)
        instanciarLista(thisCode.rawValue)
    }*/

    fun validarID(v: View){
        idMusica = etSearch.text.toString()
        referencia = database.getReference("/Establecimientos")
        referencia.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                for (registro in snapshot.children) {
                    val establecimiento =
                        registro.getValue(mx.itesm.gbvm.playparty.Establecimiento::class.java)
                    if (establecimiento != null) {
                        val id = establecimiento.ID
                        if (id == idMusica) {
                            makeCurrentFragment(FragmentoMusica())
                            BotonValido = true
                        }
                    }
                }
            }
        })
    }

    fun btnFragInicioSesion(v: View){
        Pablo = "Inicio"
        makeCurrentFragment(FragmentoInicioSesion())
    }
    fun btnFragRegistro(v: View){
        Pablo = "Registro"
        makeCurrentFragment(FragmentoRegistro())
    }
    fun btnFragBackIniReg(v: View){
        Pablo = ""
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
            mostrarDialogo("Necesitas GPS para esta app.")
        }else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                CODIGO_PERMISO_GPS
            )
        }
        val cameraJustificacion = ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            Manifest.permission.CAMERA
        )
        if (cameraJustificacion){
            mostrarDialogo("Necesitas acceso a la camara para poder escanear QR codes.")
        }else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.CAMERA),
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

    private fun mostrarDialogo(string: String) {
        val dialogo = AlertDialog.Builder(this)
        dialogo.setMessage(string)
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
        if(email != "" && password != "") {
            registrarCuenta(email, password)
        }
    }
    fun registrarCuenta(email: String, password: String){
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
                    Pablo = "Perfil"
                    makeCurrentFragment(FragmentoPerfil())
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
        if(email != "" && password != "") {
            iniciarSesion(email, password)
        }
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
                    Pablo = "Perfil"
                    makeCurrentFragment(FragmentoPerfil())
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
    fun btnCerrarSesion(v: View){
        mAuth.signOut()
        println("logOut exitoso")
        Toast.makeText(
            this@QRInicio, "Sesión Cerrada",
            Toast.LENGTH_SHORT
        ).show()
        makeCurrentFragment(Registro_o_InicioDeSesion())
    }
    //Goggle methods
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
        if (requestCode == LOGIN_GOOGLE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        } else if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == SCAN_QR) {
                if (data != null) {
                    val datos = data.data.toString()
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
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
