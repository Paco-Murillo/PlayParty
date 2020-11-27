package mx.itesm.gbvm.playparty

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_qr.view.*

class FragmentoQR(qrInicio: QRInicio) : Fragment() {

    private val SCAN_QR = qrInicio.SCAN_QR
    private lateinit var callback: OnHeadlineSelectedListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val viewOut = inflater.inflate(R.layout.fragment_qr, container, false)

        viewOut.scanQR.setOnClickListener{
            val scanQrIntent = Intent(AppPlayParty.context,Scanner::class.java)
            startActivityForResult(scanQrIntent, SCAN_QR)
        }

        return viewOut
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == SCAN_QR){
            if(resultCode == Activity.RESULT_OK){
                if (data != null) {
                    println(data.data.toString() + " Leida Fragmento")
                    callback.onArticleSelected(data.data.toString())
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    fun setOnHeadlineSelectedListener(callback: OnHeadlineSelectedListener){
        this.callback = callback
    }

    interface OnHeadlineSelectedListener{
        fun onArticleSelected(string: String)
    }

}