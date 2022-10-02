package com.example.eduscience;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import java.util.Objects;

public class ARCamera extends AppCompatActivity {

    private ArFragment arCam;
    private int clickNo = 0;
    private String modelUrl;

    public static boolean checkSystemSupport(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            String openGlVersion = ((ActivityManager) Objects.requireNonNull(activity.getSystemService(Context.ACTIVITY_SERVICE))).getDeviceConfigurationInfo().getGlEsVersion();

            if (Double.parseDouble(openGlVersion) >= 3.0) {
                return true;
            }
            else {
                Toast.makeText(activity, "OpenGl Version 3.0 or later is required.", Toast.LENGTH_SHORT).show();
                activity.finish();
                return false;
            }
        }
        else {
            Toast.makeText(activity, "App does not support required Build Version", Toast.LENGTH_SHORT).show();
            activity.finish();
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arcamera);

        Intent intent = getIntent();
        modelUrl = intent.getStringExtra("modelUrl");

        if (checkSystemSupport(this)) {
            arCam = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.arCameraArea);
            assert arCam != null;
            arCam.setOnTapArPlaneListener(((hitResult, plane, motionEvent) -> {
                clickNo++;
                //The 3d model comes on the screen only after click count is 1 that means once
                if(clickNo == 1){
                    Anchor anchor = hitResult.createAnchor();
                    ModelRenderable.builder()
                            .setSource(this, Uri.parse(modelUrl))
                            .setIsFilamentGltf(true)
                            .build()
                            .thenAccept(modelRenderable -> addModel(anchor , modelRenderable))
                            .exceptionally(throwable -> {
                                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                                builder.setMessage("Something went wrong" + throwable.getMessage()).show();
                                return null;
                            });
                }
            }));
        }
    }

    private void addModel(Anchor anchor , ModelRenderable modelRenderable){

        //Creating a AnchorNode with a specific anchor
        AnchorNode anchorNode = new AnchorNode(anchor);

        //Attaching the anchorNode with the ArFragment
        anchorNode.setParent(arCam.getArSceneView().getScene());

        //Attaching the anchorNode with the TransformableNode
        TransformableNode model = new TransformableNode(arCam.getTransformationSystem());
        model.setParent(anchorNode);

        //Following code is for giving the Ar image a size
        //so the image can be resized in between
        model.getScaleController().setMaxScale(0.2f);
        model.getScaleController().setMinScale(0.05f);

        //Attaching the 3d model with the TransformableNode that is already attached with the node
        model.setRenderable(modelRenderable);
        model.select();
    }
}