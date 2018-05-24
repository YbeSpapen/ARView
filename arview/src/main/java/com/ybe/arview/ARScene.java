package com.ybe.arview;

import android.content.Context;
import android.location.Location;
import android.opengl.Matrix;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.google.ar.core.Anchor;
import com.google.ar.core.Camera;
import com.google.ar.core.Frame;
import com.google.ar.core.Pose;
import com.google.ar.core.TrackingState;
import com.ybe.arview.helper.MathHelper;
import com.ybe.arview.render.ObjectRenderer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ARScene {
    private static final String TAG = "ARScene";
    private Context mContext;
    private MathHelper helper;

    private int amountOfObjects;

    public Location location;

    private final float[] anchorMatrix = new float[16];
    private final float[] needleMatrix = new float[16];

    private final ArrayList<Anchor> anchors = new ArrayList<>();
    private List<Pose> poses = new ArrayList<>();


    private final ObjectRenderer compassNeedle = new ObjectRenderer();
    private final ObjectRenderer flag = new ObjectRenderer();
    private final ObjectRenderer sign = new ObjectRenderer();

    private final Pose cameraRelativeCompassPose = Pose.makeTranslation(0, -0.07f, -0.2f);

    @RequiresApi(api = Build.VERSION_CODES.M)
    public ARScene(Context mContext, Location location, int amountOfObjects ) {
        this.mContext = mContext;

        this.location = location;

        this.amountOfObjects = amountOfObjects;

        this.helper = new MathHelper(mContext, location);
    }

    public void draw(Frame frame, boolean drawSign, boolean drawCompass) {
        Camera camera = frame.getCamera();

        float[] projmtx = new float[16];
        camera.getProjectionMatrix(projmtx, 0, 0.1f, 100.0f);


        float[] viewmtx = new float[16];
        camera.getViewMatrix(viewmtx, 0);

        final float lightIntensity = frame.getLightEstimate().getPixelIntensity();

        if (drawSign) {
            for (Anchor anchor : anchors) {
                if (anchor.getTrackingState() != TrackingState.TRACKING) {
                    continue;
                }

                if (helper.getDistanceToPoi() > 50) {
                    Pose newPose = poses.get(anchors.indexOf(anchor)).extractRotation();
                    Pose finalPose = anchor.getPose().extractTranslation().compose(newPose);
                    finalPose.toMatrix(anchorMatrix, 0);

                    sign.updateModelMatrix(anchorMatrix, 0.5f);
                    sign.draw(viewmtx, projmtx, lightIntensity);
                } else {
                    anchor.getPose().toMatrix(anchorMatrix, 0);

                    flag.updateModelMatrix(anchorMatrix, 0.05f);
                    flag.draw(viewmtx, projmtx, lightIntensity);
                }
            }
        }

        if (drawCompass) {
            Pose compassBase = camera.getPose().compose(cameraRelativeCompassPose).extractTranslation();
            Pose needleRotation = compassBase.compose(camera.getDisplayOrientedPose().extractRotation()).extractRotation();
            Pose finalPose = compassBase.compose(needleRotation);

            finalPose.toMatrix(needleMatrix, 0);
            Matrix.rotateM(needleMatrix, 0, helper.getAngle(), 0f, 1f, 0f);

            compassNeedle.updateModelMatrix(needleMatrix, .003f);
            compassNeedle.draw(viewmtx, projmtx, lightIntensity);
        }
    }

    public void setupObjects(String signObjFile, String signTexture, String flagObjFile, String flagTexture) {
        try {
            sign.createOnGlThread( /*context=*/ mContext, signObjFile, signTexture);
            sign.setMaterialProperties(0.0f, 3.5f, 1.0f, 6.0f);

            flag.createOnGlThread( /*context=*/ mContext, flagObjFile, flagTexture);
            flag.setMaterialProperties(0.0f, 3.5f, 1.0f, 6.0f);

            compassNeedle.createOnGlThread( /*context=*/ mContext, "compass_needle.obj", "compass_needle.png");
            compassNeedle.setMaterialProperties(0.0f, 3.5f, 1.0f, 6.0f);
        } catch (IOException e) {
            Log.e(TAG, "Failed to read obj file");
        }
    }

    public void addAnchor(Anchor anchor) {
        if (anchors.size() >= amountOfObjects) {
            removeAnchor();
        }

        anchors.add(anchor);
        poses.add(helper.getSignAngle());

    }

    public void removeAnchor() {
        anchors.get(0).detach();
        anchors.remove(0);
        poses.remove(0);
    }

    public void pause() {
        helper.onPause();
    }

    public void resume() {
        helper.onResume();
        helper.refreshLocation();
    }

}
