package com.microsoft.projectoxford.face.samples.facereciever;

import android.Manifest;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.gms.samples.vision.face.facetracker.FaceGraphic;
import com.google.android.gms.samples.vision.face.facetracker.ui.camera.CameraSourcePreview;
import com.google.android.gms.samples.vision.face.facetracker.ui.camera.GraphicOverlay;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.microsoft.projectoxford.face.samples.R;

/**
 * Created by Sneh Jain on 04-11-2017.
 */

public class lockservice extends Service {

    private BroadcastReceiver mReceiver;
    private boolean isShowing = false;


    private CameraSourcePreview mPreview;
    private GraphicOverlay mGraphicOverlay;



    private CameraSource mCameraSource = null;


    View mview;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    private WindowManager windowManager;
    private TextView textview;
    WindowManager.LayoutParams params;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.w("Service","Running");
        windowManager = (WindowManager)getSystemService(WINDOW_SERVICE);

        //add textview and its properties
//        textview = new TextView(this);
//        textview.setText("Hello There!");
//        textview.setTextColor(ContextCompat.getColor(this, android.R.color.white));
//        textview.setTextSize(32f);

        //set parameters for the textview
        LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(getBaseContext().LAYOUT_INFLATER_SERVICE);
        mview = new View(this);
        mview = inflater.inflate(R.layout.main,null);


        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP;
        //Register receiver for determining screen off and if user is present
        mReceiver = new LockScreenStateReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);

        registerReceiver(mReceiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    public class LockScreenStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                //if screen is turn off show the textview
                if (!isShowing) {
                    mPreview = (CameraSourcePreview) mview.findViewById(com.google.android.gms.samples.vision.face.facetracker.R.id.preview);
                    mGraphicOverlay = (GraphicOverlay) mview.findViewById(com.google.android.gms.samples.vision.face.facetracker.R.id.faceOverlay);

                    // Check for the camera permission before accessing the camera.  If the
                    // permission is not granted yet, request permission.
                    int rc = ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA);
                    if (rc == PackageManager.PERMISSION_GRANTED) {
                        Log.w("-->","createCam");
                        createCameraSource();
                    } else {
//                        requestCameraPermission();
                    }
                    windowManager.addView(mview, params);
                    isShowing = true;
                }
            }

            else if(intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
                //Handle resuming events if user is present/screen is unlocked remove the textview immediately
                if (isShowing) {
                    windowManager.removeViewImmediate(mview);
                    isShowing = false;
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        //unregister receiver when the service is destroy
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }

        //remove view if it is showing and the service is destroy
        if (isShowing) {
            windowManager.removeViewImmediate(textview);
            isShowing = false;
        }
        super.onDestroy();
    }





    private void createCameraSource() {

        Context context = getBaseContext();
        FaceDetector detector = new FaceDetector.Builder(context)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        detector.setProcessor(
                new MultiProcessor.Builder<>(new GraphicFaceTrackerFactory())
                        .build());

        if (!detector.isOperational()) {
            Log.w("Service", "Face detector dependencies are not yet available.");
        }
        try {
            mCameraSource = new CameraSource.Builder(context, detector)
                    .setRequestedPreviewSize(640, 480)
                    .setFacing(CameraSource.CAMERA_FACING_FRONT)
                    .setRequestedFps(30.0f)
                    .build();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        Log.w("------>", "mCamera Resource initialize");
    }

    private class GraphicFaceTrackerFactory implements MultiProcessor.Factory<Face> {
        @Override
        public Tracker<Face> create(Face face) {
            return new GraphicFaceTracker(mGraphicOverlay);
        }
    }

    private class GraphicFaceTracker extends Tracker<Face> {
        private GraphicOverlay mOverlay;
        private FaceGraphic mFaceGraphic;

        GraphicFaceTracker(GraphicOverlay overlay) {
            mOverlay = overlay;
            mFaceGraphic = new FaceGraphic(overlay);
        }
        @Override
        public void onNewItem(int faceId, Face item) {
            mFaceGraphic.setId(faceId);
        }

        @Override
        public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {
            Log.w("Service", "adding mFaceGraphic");
            mOverlay.add(mFaceGraphic);
            mFaceGraphic.updateFace(face);
        }

        @Override
        public void onMissing(FaceDetector.Detections<Face> detectionResults) {
            mOverlay.remove(mFaceGraphic);
        }

        @Override
        public void onDone() {
            mOverlay.remove(mFaceGraphic);
        }
    }



//
//
//    private void startCameraSource() {
//
//        // check that the device has play services available.
//        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
//                getApplicationContext());
//        if (code != ConnectionResult.SUCCESS) {
//            Dialog dlg =
//                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
//            dlg.show();
//        }
//
//        if (mCameraSource != null) {
//            try {
//                mPreview.start(mCameraSource, mGraphicOverlay);
//            } catch (IOException e) {
//                Log.e(TAG, "Unable to start camera source.", e);
//                mCameraSource.release();
//                mCameraSource = null;
//            }
//        }
//    }

}
