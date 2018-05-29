# ARView

## ARScene

ARScene maakt het mogelijk om 3D navigatie objecten toe te voegen aan een eigen ARCore activity of fragment.

### Initialisatie

``` Java
ARScene scene = new ARScene(getActivity(), destination, amountOfAnchors);  
```

### Lifecycle
``` Java
scene.resume();
scene.pause();
```

### Inladen objecten

De objecten moeten worden ingeladen voordat ze gerenderd kunnen worden. setupObjects zorgt er voor dat de objecten worden klaargezet, aan deze methode worden de object en texture files meegegeven.

``` Java
@Override
public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        ...
        try {
            ...
            scene.setupObjects(signObjFile, signTextureFile, flagObjFile, flagTextureFile);
            ...  
        }
        ...
}
```

### Tekenen objecten

Aan de scene kunnen anchors worden toegevoegd wanneer de gebruiker bijvoorbeeld op het scherm drukt. Vervolgens wordt de methode draw aangeroepen die als parameter 2 booleans heeft die er voor zorgen dat er een view kan worden gemaaakt met bijvoorbeeld enkel de arrow functionaliteit.

``` Java
@Override
public void onDrawFrame(GL10 gl) {
  ...
  scene.addAnchor(hit.createAnchor());
  ...
  scene.draw(frame, true, true);
}
```

## DirectionARView

DirectionARView is een fragment dat kan ingeladen worden. Dit fragment kan worden gebruikt wanneer er geen aanpassingen in de AR view moeten worden gemaakt.

```Java
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
ft.add(R.id.layout_ar_container, arView);
ft.commit();
```
