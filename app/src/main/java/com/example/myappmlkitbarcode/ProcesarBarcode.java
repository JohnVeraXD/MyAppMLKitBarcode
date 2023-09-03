package com.example.myappmlkitbarcode;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.util.List;

public class ProcesarBarcode {
    private static final String TAG = "ProcesarBarcode";

    public interface BarcodeListener {
        void onProcesarBarcode(String result);
    }

    public static void processBarcode(Bitmap imageBitmap, BarcodeListener listener) {
        if (imageBitmap == null) {
            listener.onProcesarBarcode("No has seleccionado una imagen");
            return;
        }

        InputImage image = InputImage.fromBitmap(imageBitmap, 0);
        BarcodeScanner scanner = BarcodeScanning.getClient();

        scanner.process(image)
                .addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
                    @Override
                    public void onSuccess(List<Barcode> barcodes) {
                        if (barcodes.size() == 0) {
                            listener.onProcesarBarcode("No se encontró Código QR o Barra");
                        } else {
                            StringBuilder resultText = new StringBuilder();
                            for (Barcode barcode : barcodes) {
                                int valueType = barcode.getValueType();
                                switch (valueType) {
                                    case Barcode.TYPE_WIFI:
                                        // Procesar código WiFi
                                        String ssid = barcode.getWifi().getSsid();
                                        String password = barcode.getWifi().getPassword();
                                        int tipo = barcode.getWifi().getEncryptionType();
                                        resultText.append(" RED WIFI\n")
                                                .append(" Nombre: ").append(ssid).append("\n")
                                                .append(" Contraseña: ").append(password).append("\n")
                                                .append(" Tipo: ").append(tipo).append("\n");
                                        break;
                                    case Barcode.TYPE_URL:
                                        // Procesar URL
                                        String title = barcode.getUrl().getTitle();
                                        String url = barcode.getUrl().getUrl();
                                        resultText.append(" URL\n")
                                                .append(" Titulo: ").append(title).append("\n")
                                                .append(" Url: ").append(url).append("\n");
                                        break;
                                    default:
                                        //Procesar codigo de barra
                                        if (Barcode.FORMAT_CODABAR == 8) {
                                            String barra = barcode.getRawValue();
                                            resultText.append(" CODIGO DE BARRA\n" +
                                                    " Codigo: " + barra);
                                            break;
                                        }
                                        // Otros tipos de códigos
                                        resultText.append("Tipo de código no reconocido\n");
                                        break;
                                }
                            }
                            listener.onProcesarBarcode(resultText.toString());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.onProcesarBarcode("Error, intente de nuevo");
                    }
                });
    }
}
