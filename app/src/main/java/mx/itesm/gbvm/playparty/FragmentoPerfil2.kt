package mx.itesm.gbvm.playparty

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
import androidx.core.text.set
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.types.PlayerState
import com.spotify.protocol.types.Track
import kotlinx.android.synthetic.main.fragment_fragmento_perfil2.*
import kotlinx.android.synthetic.main.fragment_fragmento_perfil2.tfNombreU


class FragmentoPerfil2 : Fragment() {
    lateinit var mAuth: FirebaseAuth
    lateinit var usuario: Usuario
    lateinit var QRInicio: QRInicio

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
        tfID.text = usuario.userID
        PlayList.text = SpannableStringBuilder(usuario.playlist)
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
            if(PlayList.text.toString() != usuario.userID){
                cambiarPlaylist(PlayList.text.toString())
            }
        }
    }

    private fun cambiarPlaylist(Playlist: String) {
        //COnectar a FB y cambiar Playlist
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
                play("0ct6r3EGTcMLPtrXHDvVjc")
                contador(185)
            }

        }
        timer.start()
    }

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