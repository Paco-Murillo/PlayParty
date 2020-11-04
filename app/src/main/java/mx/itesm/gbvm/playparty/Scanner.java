package mx.itesm.gbvm.playparty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

/**
 * Clase para leer códigos, utiliza @BarcodeDetector
 *
 */
public class Scanner extends AppCompatActivity
{
    private CameraSource camara;
    private SurfaceView vistaCamara;
    private final int PERMISO_CAMARA = 500;
    private final int PERMISO_VIBRAR = 800;

    private BarcodeDetector detector = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scanner);

        vistaCamara = findViewById(R.id.surfaceView);
        iniciarQR();
    }

    private void iniciarQR() {
        detector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats( Barcode.ALL_FORMATS)
                .build();

        int ancho = 800;
        int alto = 600;
        this.camara = new CameraSource.Builder(getApplicationContext(), detector)
                .setRequestedPreviewSize(ancho, alto)
                .setAutoFocusEnabled(true)
                .build();

        vistaCamara.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                pedirPermiso();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                camara.stop();
            }
        });
    }

    private void detectarCodigoBarras(BarcodeDetector detector) {

        detector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {

                final SparseArray<Barcode> codigos = detections.getDetectedItems();
                if (codigos != null && codigos.size() > 0) {

                    Scanner.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            camara.stop();
                            Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                            v.vibrate(300);
                            vistaCamara.setVisibility(View.GONE);
                            // Regresa
                            regresarCodigo(codigos.valueAt(0).displayValue);
                        }
                    });
                }
            }

            @Override
            public void release() {

            }
        });
    }

    private void regresarCodigo(String codigo) {
        Intent intRegresa = new Intent();
        System.out.println(codigo+ " Salida");
        intRegresa.setData(Uri.parse(codigo));
        setResult(RESULT_OK, intRegresa);
        finish();
    }

    private void pedirPermiso() {
        if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // No tiene permiso
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                    // Explicarle por qué y reintentar
                    explicarPermiso(Permiso.CAMARA);
                } else {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISO_CAMARA);
                }
            }
        }
        if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.VIBRATE) != PackageManager.PERMISSION_GRANTED){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.VIBRATE)) {
                    explicarPermiso(Permiso.VIBRAR);
                } else {
                    requestPermissions(new String[]{Manifest.permission.VIBRATE}, PERMISO_VIBRAR);
                }
            }
        }
        if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.VIBRATE) == PackageManager.PERMISSION_GRANTED &&
         ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CAMERA ) == PackageManager.PERMISSION_GRANTED){
            // Ya tiene permiso
            try {
                detectarCodigoBarras(detector);
                camara.start(vistaCamara.getHolder());
            } catch (IOException e) {

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        pedirPermiso();
    }

    private void explicarPermiso(Permiso tipo) {
        // Explicarle por qué y reintentar
        switch (tipo){
            case CAMARA:
                AlertDialog dialogoCamara = new AlertDialog.Builder(this)
                        .setMessage("Para capturar el código de barras")
                        .setTitle("Aviso")
                        .setPositiveButton("Conceder permiso", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                pedirPermiso();
                            }
                        }).setNegativeButton("No permitir", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).create();
                dialogoCamara.show();
                break;
            case VIBRAR:
                AlertDialog dialogoVibrar = new AlertDialog.Builder(this)
                        .setMessage("Para avisar cuando se captura el còdigo de barras")
                        .setTitle("Aviso")
                        .setPositiveButton("Conceder permiso", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                pedirPermiso();
                            }
                        }).setNegativeButton("No permitir", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).create();
                dialogoVibrar.show();
                break;
            default:
                break;

        }
    }

    public void finishOnButton(View v){
        camara.release();
        vistaCamara = null;
        camara = null;
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        camara.release();
        vistaCamara = null;
        camara = null;
    }

    private enum Permiso{
        CAMARA,
        VIBRAR
    }
}