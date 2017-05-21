package com.translator.tester.visiontranslator;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.translator.tester.visiontranslator.camera.CameraScreen;
import com.translator.tester.visiontranslator.camera.CameraSource;
import com.translator.tester.visiontranslator.camera.GraphicOverlay;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public final class OCR_Activity extends AppCompatActivity {

    private static final int GMS_RequestCode = 1;
    private static final int CAMERA_PERMISSION_RC = 2;
    private static boolean flashLight = false;
    private static boolean autoFocus = true;

    @BindView(R.id.preview) CameraScreen cameraScreen;
    @BindView(R.id.graphicOverlay) GraphicOverlay<OCR_GraphicOverlay> ocrGraphicOverlay;
    @BindView(R.id.fab) FloatingActionButton fab;

    private static OkHttpClient client;

    private CameraSource cameraSource;
    private ScaleGestureDetector scaleGestureDetector;
    private GestureDetector gestureDetector;
    private TextToSpeech textToSpeech;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_ocr);
        ButterKnife.bind(this);

        Context context = getApplicationContext();
        TranslateURL.setKey(context.getResources().getString(R.string.KEY));

        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            cameraSourceGen();
        } else {
            reqCameraPermission();
        }

        Snackbar.make(ocrGraphicOverlay, R.string.instructions, Snackbar.LENGTH_LONG)
                .show();

    }


    private void reqCameraPermission() {

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, CAMERA_PERMISSION_RC);
            return;
        }

        final Activity Activity_Context = this;
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(Activity_Context, permissions, CAMERA_PERMISSION_RC);
            }
        };

        Snackbar.make(ocrGraphicOverlay, R.string.camera_access,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.accept, listener)
                .show();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event) || scaleGestureDetector.onTouchEvent(event) || super.onTouchEvent(event);
    }


    private final boolean hasLowStorage(){
        return registerReceiver(null, new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW)) != null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode != CAMERA_PERMISSION_RC) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            cameraSourceGen();
            return;
        }

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };

        new AlertDialog.Builder(this)
                .setTitle("Camera Permission")
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(R.string.ok, listener)
                .show();
    }

    private void cameraSourceGen() {
        Context context = getApplicationContext();

        client = new OkHttpClient();

        textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS) textToSpeech.setLanguage(Locale.US);
                else Snackbar.make(ocrGraphicOverlay, R.string.speech_error, Snackbar.LENGTH_LONG)
                        .show();
            }
        });

        TextRecognizer textRecognizer = new TextRecognizer.Builder(context).build();
        textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
            @Override
            public void release() {
                ocrGraphicOverlay.clear();
            }

            @Override
            public void receiveDetections(Detector.Detections<TextBlock> detections) {
                ocrGraphicOverlay.clear();

                SparseArray<TextBlock> items = detections.getDetectedItems();
                for (int i = 0; i < items.size(); ++i)
                    ocrGraphicOverlay.add(new OCR_GraphicOverlay(ocrGraphicOverlay, items.valueAt(i)));
            }
        });

        if (!textRecognizer.isOperational()) {
            if (hasLowStorage()) {
                Toast.makeText(this, "Inadequate App Storage", Toast.LENGTH_LONG).show();
            }
        }


        cameraSource = new CameraSource.Builder(context, textRecognizer)
                        .setFacing(CameraSource.CAMERA_FACING_BACK)
                        .setRequestedPreviewSize(1280, 1024)
                        .setRequestedFps(15.0f)
                        .setFlashMode(flashLight ? Camera.Parameters.FLASH_MODE_TORCH : null)
                        .setFocusMode(autoFocus ? Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE : null)
                        .build();

        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                OCR_GraphicOverlay graphic = ocrGraphicOverlay.getGraphicAtLocation(e.getRawX(), e.getRawY());
                TextBlock text = null;
                if (graphic != null) {
                    text = graphic.getTextBlock();
                    if (text != null && text.getValue() != null) {
                        TranslateURL.setLanguage(text.getValue());
                        String url=TranslateURL.getURL();
                        Request req = new Request.Builder().url(url).build();

                        client.newCall(req).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {}

                            @Override
                            public void onResponse(Call call, final Response response) throws IOException {
                                try {
                                    String responseData = response.body().string();
                                    JSONObject json = new JSONObject(responseData);
                                    JSONArray translations = json.getJSONObject("data").getJSONArray("translations");

                                    StringBuilder builder = new StringBuilder();
                                    for (int i = 0; i < translations.length(); i++) {
                                        String translatedSent = translations.getJSONObject(i).getString("translatedText");
                                        builder.append(translatedSent);
                                        if (i + 1 < translations.length()) builder.append('\n');
                                    }
                                    final String responseUIData = builder.toString();
                                    OCR_Activity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Snackbar.make(ocrGraphicOverlay, responseUIData,
                                                    Snackbar.LENGTH_LONG)
                                                    .show();

                                            if(TranslateURL.hasSupportedLangSpeech())
                                                textToSpeech.speak(responseUIData, TextToSpeech.QUEUE_ADD, null, "DEFAULT");
                                            else
                                                textToSpeech.speak(TranslateURL.targetLanguageFull+" speech not supported", TextToSpeech.QUEUE_ADD, null, "DEFAULT");
                                        }
                                    });
                                } catch (JSONException e) {}
                            }
                        });
                    }
                }
                return (text != null) || super.onSingleTapConfirmed(e);
            }
        });

        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleGestureDetector.OnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
                return false;
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
                return true;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
                if (cameraSource != null) {
                    cameraSource.doZoom(scaleGestureDetector.getScaleFactor());
                }
            }
        });
    }

    public static final int isGooglePlayServicesAvailable(Context context){
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        return googleApiAvailability.isGooglePlayServicesAvailable(context);
    }


    private void startCameraSource() throws SecurityException {
        Context context=getApplicationContext();
        int resultCode = isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(this, resultCode, GMS_RequestCode);
            dialog.show();
        }

        if (cameraSource != null) {
            try {
                cameraScreen.start(cameraSource, ocrGraphicOverlay);
            } catch (IOException e) {
                cameraSource.release();
                cameraSource = null;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startCameraSource();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (cameraScreen != null) {
            cameraScreen.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraScreen != null)
            cameraScreen.release();
    }

    public void fabExecute(View view) {
        LanguageDialog dialog = new LanguageDialog();
        dialog.show(getFragmentManager(), "LangDialog");
    }
}
