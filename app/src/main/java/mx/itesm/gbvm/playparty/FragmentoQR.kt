package mx.itesm.gbvm.playparty

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_qr.view.*

class FragmentoQR : Fragment() {

    private val SCAN_QR = 6669

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val viewOut = inflater.inflate(R.layout.fragment_qr, container, false)

        viewOut.scanQR.setOnClickListener{
            val scanQrIntent = Intent(AppPlayParty.context,BarcodeCaptureActivity::class.java)
            startActivityForResult(scanQrIntent, SCAN_QR)
        }

        return viewOut
    }
}