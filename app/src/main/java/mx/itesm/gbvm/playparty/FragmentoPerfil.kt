package mx.itesm.gbvm.playparty

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_perfil.*

class FragmentoPerfil : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_perfil, container, false)
    }

    fun btnRegresar(v: View){
        val intRegresar = Intent(activity, QRInicio::class.java)
        startActivity(intRegresar)

    }
    /*
    *   @Override
public void onBackPressed() {
    if (getFragmentManager().getBackStackEntryCount() > 0) {
        getFragmentManager().popBackStack();
    } else {
        super.onBackPressed();
    }
}
* requireActivity()
    .onBackPressedDispatcher
    .addCallback(this, object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            Log.d(TAG, "Fragment back pressed invoked")
            // Do custom work here

            // if you want onBackPressed() to be called as normal afterwards
            if (isEnabled) {
                isEnabled = false
                requireActivity().onBackPressed()
            }
        }
    }
)
fragmentManager.beginTransaction().replace(R.id.content_frame,fragment).addToBackStack("tag").commit();
    * */
}