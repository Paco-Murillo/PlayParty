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
import android.os.CountDownTimer
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.types.PlayerState
import com.spotify.protocol.types.Track
import kotlinx.android.synthetic.main.fragment_inicio_sesion.*
import kotlinx.android.synthetic.main.fragment_qr.*
import kotlinx.android.synthetic.main.fragment_registro.*
import kotlinx.android.synthetic.main.fragment_registro.etEmail
import kotlinx.android.synthetic.main.fragment_registro.etPassword
import kotlinx.android.synthetic.main.fragment_registro_inicio_sesion.*
import kotlinx.android.synthetic.main.qr_inicio.*


class   QRInicio : AppCompatActivity(), GPSListener, Connector.ConnectionListener, FragmentoQR.OnHeadlineSelectedListener, FragmentoPerfil.OnNewArrayListener {
    private var gps: GPS? = null
    private val CODIGO_PERMISO_GPS: Int = 200
    private var posicion: Location? = null

    //Spotify
    private val CLIENT_ID: String? = "571410421d934710ba3a3f201b170b50"
    private val REDIRECT_URI = "https://mx.itesm.gbvm.playparty/callback"
    private var mSpotifyAppRemote: SpotifyAppRemote? = null

    // Validacion ID
    var idMusica:String = ""
    var BotonValido: Boolean = false
    private  val database = FirebaseDatabase.getInstance()
    private lateinit var referencia: DatabaseReference
    private var Pablo = ""


    //Google Autenticador
    private lateinit var mAuth: FirebaseAuth
    private var usuarioActual: Usuario = Usuario()
    var email = ""
    var password = ""

    //qr
    val SCAN_QR = 6670

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

        mAuth = FirebaseAuth.getInstance()


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
                    if (Pablo == "Inicio") {
                        makeCurrentFragment(FragmentoInicioSesion2.newInstance(this))
                    } else {
                        makeCurrentFragment(Registro_o_InicioDeSesion())
                    }
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

    fun btnIniciarSpotify(v: View){
        val connectionParams = ConnectionParams.Builder(CLIENT_ID)
            .setRedirectUri(REDIRECT_URI)
            .showAuthView(true)
            .build()

        SpotifyAppRemote.connect(this, connectionParams,
            object : Connector.ConnectionListener {
                override fun onConnected(spotifyAppRemote: SpotifyAppRemote) {
                    mSpotifyAppRemote = spotifyAppRemote
                    val mySnackbar = Snackbar.make(findViewById(R.id.fragment_container), "Conectado!", Snackbar.LENGTH_SHORT)
                    mySnackbar.show()
                    mSpotifyAppRemote!!.playerApi
                        .subscribeToPlayerState()
                        .setEventCallback { playerState: PlayerState ->
                            val track: Track? = playerState.track
                            if (track != null) {
                                Log.d("MainActivity", track.name.toString() + " by " + track.artist.name)
                            }
                        }
                    connected()
                }

                override fun onFailure(throwable: Throwable) {
                    val mySnackbar = Snackbar.make(findViewById(R.id.fragment_container), "Fallo!", Snackbar.LENGTH_SHORT)
                    mySnackbar.show()
                }
            })
    }
    fun btnFragInicioSesion(v: View){
        Pablo = "Inicio"
        makeCurrentFragment(FragmentoInicioSesion2.newInstance(this))
    }

    fun btnFragRegistro(v: View){
        Pablo = "Registro"
    }

    fun btnFragBackIniReg(v: View){
        Pablo = ""
        makeCurrentFragment(Registro_o_InicioDeSesion())
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
            println("Usuario: ${currentUser?.email}")

        }else{
            println("No has hecho login")
            Toast.makeText(this, "No ha iniciado Sesión", Toast.LENGTH_SHORT).show()
        }

    }

    fun btnRegistrarCuenta(v: View){
        val usuario = etNombreU.text.toString()
        val email = etEmail.text.toString()
        val password = etPassword.text.toString()
        val nombreU = etNombreU.text.toString()
        if(email != "" && password != "" && usuario != "" && nombreU != "") {
            registrarCuenta(email, password)
        }

    }

    @SuppressLint("RestrictedApi")
    fun crearUsuarioBD(){
        val email = etEmail.text.toString()
        val password = etPassword.text.toString()
        val nombreU = etNombreU.text.toString()

        val lat = etLatitud.text.toString()
        val long = etLongitud.text.toString()
        val playListU = etPlayList.text.toString()
        //Obtenemos la llave del usuario con la referencia del database
        val referencia = database.getReference("/Usuarios").push()
        val llave = referencia.key
        println(llave)
        //Creamos el objeto con los datos
        usuarioActual = Usuario(
            userID = llave!!,
            email = email,
            password = password,
            nombreU = nombreU,
            latitud = lat,
            longitud = long,
            playlist = playListU
        )
        referencia.setValue(usuarioActual)

    }


    fun registrarCuenta(email: String, password: String){

    }
    fun btnIniciarSesion(v: View){

    }
    fun iniciarSesion(email: String, password: String){

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

    private fun connected() {
        val mySnackbar = Snackbar.make(findViewById(R.id.fragment_container), "Conectado!", Snackbar.LENGTH_SHORT)
        mySnackbar.show()
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
    fun contador(tiempo:Int){
        val timer = object: CountDownTimer(tiempo.toLong() * 1000,1000){
            override fun onTick(millisSec: Long) {
                println(millisSec)
            }

            override fun onFinish() {
                play("0ct6r3EGTcMLPtrXHDvVjc")
                contador(185)
            }

        }
        timer.start()
    }
    fun instanciarLista(v: View){
        play("040SAk4tIYfcqHXWF6yfpG")
        contador(185)
    }

    private fun play(cancion: String){
        var track = "spotify:track:"+ cancion
        mSpotifyAppRemote?.getPlayerApi()?.play(track)
    }

    override fun onFragmentPerfilStopped(usuario: Usuario) {
        TODO("Not yet implemented")
    }
}
