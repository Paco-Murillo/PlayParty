package mx.itesm.gbvm.playparty


import android.Manifest
import android.annotation.SuppressLint
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
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import kotlinx.android.synthetic.main.fragment_qr.*
import kotlinx.android.synthetic.main.qr_inicio.*


class   QRInicio : AppCompatActivity(), GPSListener, Connector.ConnectionListener, FragmentoQR.OnHeadlineSelectedListener{
    private var gps: GPS? = null
    private val CODIGO_PERMISO_GPS: Int = 200
    private var posicion: Location? = null

    //Spotify


    // Validacion ID
    var idMusica:String = ""
    var BotonValido: Boolean = false
    private  val database = FirebaseDatabase.getInstance()
    private lateinit var referencia: DatabaseReference
    private var Perfil : Fragment = Fragmento_RI.newInstance(this)

    //qr
    val SCAN_QR = 6670

    fun cambiarPerfil(fragment: Fragment){
        Perfil = fragment
        makeCurrentFragment(Perfil)
    }

    fun makeCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(fragment.toString())
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        configurarGPS()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qr_inicio)


        var QRFragment = FragmentoQR(this)

        //QR
        QRFragment.setOnHeadlineSelectedListener(this)
        //Fragmento inicial
        makeCurrentFragment(QRFragment)
        val view: View = bottom_nav.findViewById(R.id.nav_Musica)
        view.performClick()
        bottom_nav.setOnNavigationItemSelectedListener {
            when (it.itemId){
                R.id.nav_perfil ->
                    makeCurrentFragment(Perfil)
                R.id.nav_Musica ->
                if (BotonValido) {
                    makeCurrentFragment(FragmentoMusica(idMusica))

                } else {
                    makeCurrentFragment(QRFragment)
                }
                R.id.nav_Mapa -> makeCurrentFragment(FragmentoMapa(posicion))
                }
            true
        }
    }

    fun valid(idMusica: String){
        this.idMusica = idMusica
        referencia = database.getReference("/Usuarios")
        referencia.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                for (registro in snapshot.children) {
                    val establecimiento =
                        registro.getValue(Establecimiento::class.java)
                    if (establecimiento != null) {
                        val id = establecimiento.userID
                        if (id == idMusica) {
                            makeCurrentFragment(FragmentoMusica(this@QRInicio.idMusica))
                            BotonValido = true
                        }

                    }
                    else{
                        val mySnackbar = Snackbar.make(findViewById(R.id.fragment_container), "ID Invalido!", Snackbar.LENGTH_LONG)
                        mySnackbar.show()
                    }
                }
            }
        })
    }

    fun validarID(v: View){
        idMusica = etSearch.text.toString()
        valid(idMusica)
    }

    fun btnFrag(v:View){
        makeCurrentFragment(FragmentoInicioSesion2.newInstance(this))
    }

    fun btnQR(v: View){
        BotonValido = false
        makeCurrentFragment(FragmentoQR(this))
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

    //Método de autenticación
    private fun updateUI(currentUser: FirebaseUser?) {
        if(currentUser!= null) {

            println("LogIn exitoso")
            println("Usuario: ${currentUser.email}")

        }else{
            println("No has hecho login")
            Toast.makeText(this, "No ha iniciado Sesión", Toast.LENGTH_SHORT).show()
        }

    }

    fun btnRegistrarCuenta(v: View){
        //val usuario = etNombreU.text.toString()
        //val email = etEmail.text.toString()
        //val password = etPassword.text.toString()
        //val nombreU = etNombreU.text.toString()
        //if(email != "" && password != "" && usuario != "" && nombreU != "") {
        //    registrarCuenta(email, password)
        //}

    }

    @SuppressLint("RestrictedApi")
    fun crearUsuarioBD(){
        //val email = etEmail.text.toString()
        //val password = etPassword.text.toString()
        //val nombreU = etNombreU.text.toString()
//
        //val lat = etLatitud.text.toString()
        //val long = etLongitud.text.toString()
        //val playListU = etPlayList.text.toString()
        ////Obtenemos la llave del usuario con la referencia del database
        //val referencia = database.getReference("/Usuarios").push()
        //val llave = referencia.key
        //println(llave)
        ////Creamos el objeto con los datos
        //val usuarioActual = Usuario(
        //    userID = llave!!,
        //    email = email,
        //    password = password,
        //    nombreU = nombreU,
        //    latitud = lat,
        //    longitud = long,
        //    playlist = playListU
        //)
        //referencia.setValue(usuarioActual)

    }


    fun registrarCuenta(email: String, password: String){

    }
    fun btnIniciarSesion(v: View){

    }
    fun btnCerrarSesion(v: View){
        //encontrado = false
        //Pablo = ""
        //mAuth.signOut()
        //email = ""
        //password = ""
        //println("logOut exitoso")
//
        //Toast.makeText(
        //    this@QRInicio, "Sesión Cerrada",
        //    Toast.LENGTH_SHORT
        //).show()
        //makeCurrentFragment(fragPerfil.fragmentoBack)
    }


    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
        // Aaand we will finish off here.
    }

    override fun onConnected(p0: SpotifyAppRemote?) {

    }

    override fun onFailure(p0: Throwable?) {

    }

    override fun onArticleSelected(string: String) {
        println(string)
        valid(string)
    }

}
