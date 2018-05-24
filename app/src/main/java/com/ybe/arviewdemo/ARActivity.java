package com.ybe.arviewdemo;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.ybe.arview.DirectionARView;

public class ARActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);

        Location location = getIntent().getParcelableExtra("location");

        Bundle bundle = new Bundle();
        bundle.putParcelable("location", location);
        bundle.putInt("amountOfObjects", 2);
        bundle.putString("signObjFile", "sign.obj");
        bundle.putString("signTextureFile", "sign.jpg");
        bundle.putString("flagObjFile", "flag.obj");
        bundle.putString("flagTextureFile", "flag.jpg");

        DirectionARView arView = new DirectionARView();
        arView.setArguments(bundle);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.layout_ar_container, arView);
        ft.commit();

    }

}
