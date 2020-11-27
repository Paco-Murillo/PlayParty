package mx.itesm.gbvm.playparty

import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.text.SpannableStringBuilder
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.text.set
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PlayGamesAuthCredential
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.types.PlayerState
import com.spotify.protocol.types.Track
import kotlinx.android.synthetic.main.fragment_fragmento_musica2.*
import kotlinx.android.synthetic.main.fragment_fragmento_perfil2.*
import kotlinx.android.synthetic.main.fragment_fragmento_perfil2.tfNombreU


class FragmentoPerfil2 : Fragment() {
    lateinit var mAuth: FirebaseAuth
    lateinit var usuario: Usuario
    lateinit var QRInicio: QRInicio


    lateinit var array: Array<Tarjeta>

    private val CLIENT_ID: String? = "571410421d934710ba3a3f201b170b50"
    private val REDIRECT_URI = "https://mx.itesm.gbvm.playparty/callback"
    private var mSpotifyAppRemote: SpotifyAppRemote? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_fragmento_perfil2, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        tfNombreU.text = usuario.nombreU
        tfemail.text = usuario.email
        tfID.text = usuario.playlist
        btnCerrar.setOnClickListener {
            mAuth.signOut()
            QRInicio.cambiarPerfil(FragmentoInicioSesion2.newInstance(QRInicio))
        }
        btnPlay.setOnClickListener {
            contador(0)
        }
        btnSpotify.setOnClickListener {
            conectSpotify()
        }
        btnActuPlay.setOnClickListener{
            if(PlayList.text.toString() != usuario.playlist){
                cambiarPlaylist(PlayList.text.toString())
            }
        }
    }

    private fun cambiarPlaylist(Playlist: String) {
        //COnectar a FB y cambiar Playlist
        FirebaseDatabase.getInstance().getReference("/Usuarios/${usuario.userID}/playlist").setValue(Playlist)
        usuario.playlist = PlayList.text.toString()
        tfID.text = usuario.playlist
    }

    private fun conectSpotify() {
        val connectionParams = ConnectionParams.Builder(CLIENT_ID)
            .setRedirectUri(REDIRECT_URI)
            .showAuthView(true)
            .build()

        SpotifyAppRemote.connect(QRInicio, connectionParams,
            object : Connector.ConnectionListener {
                override fun onConnected(spotifyAppRemote: SpotifyAppRemote) {
                    mSpotifyAppRemote = spotifyAppRemote
                    Toast.makeText(
                        QRInicio, "Conectao!",
                        Toast.LENGTH_SHORT
                    ).show()
                    mSpotifyAppRemote!!.playerApi
                        .subscribeToPlayerState()
                        .setEventCallback { playerState: PlayerState ->
                            val track: Track? = playerState.track
                        }
                }

                override fun onFailure(throwable: Throwable) {
                    Toast.makeText(
                        QRInicio, "Fallo de conección.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    fun contador(tiempo:Int){
        val timer = object: CountDownTimer(tiempo.toLong() * 1000,1000){
            override fun onTick(millisSec: Long) {
                println(millisSec)
            }

            override fun onFinish() {
                val miArreglo = ArrayList<Tarjeta>()
                val baseDatos = FirebaseDatabase.getInstance()
                val referencia = baseDatos.getReference("/Usuarios/${usuario.userID}/Playlist")
                referencia.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                    }

                    @RequiresApi(Build.VERSION_CODES.N)
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.children.forEach { registro ->
                            val tarjeta = registro.getValue(Tarjeta::class.java)!!
                            miArreglo.add(tarjeta)
                        }
                        val arreglo1 = Array(miArreglo.size) { Tarjeta() }
                        array = miArreglo.toArray(arreglo1)
                        array.sortWith(Tarjeta.Comparator().reversed())
                        play(array[0].idSong)
                        contador(array[0].tiempo)
                        baseDatos.getReference("/Usuarios/${usuario.userID}/Playlist/${array[0].idSong}/points")
                            .setValue(0)
                    }
                })
            }
        }
        timer.start()
    }
    /*
    fun mostrarQR(){
        try {

            var bm = encodeAsBitMap(usuario.userID, BarcodeFormat.QR_CODE,150,150)
        }
    }

     */
    private fun play(cancion: String){
        var track = "spotify:track:"+ cancion
        mSpotifyAppRemote?.getPlayerApi()?.play(track)
    }

    companion object {

        @JvmStatic fun newInstance( user: Usuario,aut: FirebaseAuth, qrInicio: QRInicio) =
                FragmentoPerfil2().apply {
                    arguments = Bundle().apply {
                        usuario = user
                        mAuth = aut
                        QRInicio = qrInicio
                    }
                }
    }
}