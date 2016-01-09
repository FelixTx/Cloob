package fr.turfu.cloobi;

import java.util.ArrayList;
import java.util.List;
import org.osmdroid.util.ResourceProxyImpl;
import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.  ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.ScaleBarOverlay;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.MotionEvent;
import android.widget.ImageView;

public class MainActivity extends Activity {

    private MapView myOpenMapView;
    private MapController myMapController;

    LocationManager locationManager;

    ArrayList<OverlayItem> overlayItemArray;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myOpenMapView = (MapView) findViewById(R.id.openmapview);
        //Activer le zoom et le tactile
        myOpenMapView.setBuiltInZoomControls(true);
        myOpenMapView.setMultiTouchControls(true);

        myMapController = (MapController) myOpenMapView.getController();
        myMapController.setZoom(15);


        //--- Create Overlay
        overlayItemArray = new ArrayList<OverlayItem>();
// Il serait pratique d'utiliser un custom resource proxy, mais c'est galere
        DefaultResourceProxyImpl defaultResourceProxyImpl
                = new DefaultResourceProxyImpl(this);
        // AJOUTER LE POINT BLEU !
        MyItemizedIconOverlay myItemizedIconOverlay
                = new MyItemizedIconOverlay(
                overlayItemArray, null, defaultResourceProxyImpl);
        myOpenMapView.getOverlays().add(myItemizedIconOverlay);
        //---

        // AJOUTER TOUTES LES ICONES de la TAN  ====================================================
        // Liste des icones
        ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
        // on crée chaque element i
        OverlayItem i1 =new OverlayItem("Tramway", "Nike la Tan", new GeoPoint(47.22, -1.55));
        // On crée un item i2 a partir d'un element de la classe Station
        GeoPoint g=new GeoPoint(47.215, -1.55);
        Station s=new TStation((GeoPoint)g);
        // on génere i2 à partir de s
        OverlayItem i2 =new OverlayItem(s.nom, s.toString(), s.pos);
        // On crée un objet d'image "drawable", pour lui associer le fichier png "R.drawable.tan"
        Drawable icon_t = this.getResources().getDrawable(R.drawable.tan);
        // on l'associe aux Items Bicloos
        i1.setMarker(icon_t);
        i2.setMarker(icon_t);
        // On ajoute les items a la liste
        items.add(i1);
        items.add(i2);
//On inclut la liste des icones dans un overlay
// (we can change default color in itemizedoverlaywithfocus.java)
        ItemizedOverlayWithFocus<OverlayItem> mOverlay = new ItemizedOverlayWithFocus<OverlayItem>(items,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override
                    public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                        //do something
                        return true;
                    }
                    @Override
                    public boolean onItemLongPress(final int index, final OverlayItem item) {
                        return false;
                    }
                }, defaultResourceProxyImpl);
        // Affichage du texte au click sur icone. Le texte disparait
        // lorsqu'on click sur une autre icone du meme overlay (meme couleur ici)
        mOverlay.setFocusItemsOnTap(true);
// On ajoute l'overlay a la mapview
        myOpenMapView.getOverlays().add(mOverlay);


        // TEST BICLOO ===================================== =============================

        //IDEM AS TAN
        ArrayList<OverlayItem> items2 = new ArrayList<OverlayItem>();
        GeoPoint g2=new GeoPoint(47.21, -1.555);
        Station s2=new BStation((GeoPoint)g2);
        OverlayItem it1 =new OverlayItem(s2.nom, s2.toString(), s2.pos);
        OverlayItem it2 =new OverlayItem("5", "Station bicloo", "7 / 100", new GeoPoint(47.215, -1.56));
        Drawable icon_b = this.getResources().getDrawable(R.drawable.bic);
        it1.setMarker(icon_b);
        it2.setMarker(icon_b);
        items2.add(it1); // Lat/Lon decimal degrees
        items2.add(it2);
//the overlay (we can change default color in itemizedoverlaywithfocus.java)
        ItemizedOverlayWithFocus<OverlayItem> mOverlay2 = new ItemizedOverlayWithFocus<OverlayItem>(items2,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override
                    public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                        //do something
                        return true;
                    }
                    @Override
                    public boolean onItemLongPress(final int index, final OverlayItem item) {
                        return false;
                    }
                }, defaultResourceProxyImpl);
        mOverlay2.setFocusItemsOnTap(true);

        myOpenMapView.getOverlays().add(mOverlay2);



        //======================================================================== END test

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //for demo, getLastKnownLocation from GPS only, not from NETWORK
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location lastLocation
                = locationManager.getLastKnownLocation(
                LocationManager.GPS_PROVIDER);
        if (lastLocation != null) {
            updateCenterLoc(lastLocation);
        }

        //Add Scale Bar
        ScaleBarOverlay myScaleBarOverlay = new ScaleBarOverlay(this);
        myOpenMapView.getOverlays().add(myScaleBarOverlay);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, myLocationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, myLocationListener);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.removeUpdates(myLocationListener);
    }

    private void updateLoc(Location loc){
        GeoPoint locGeoPoint = new GeoPoint(loc.getLatitude(), loc.getLongitude());
        setOverlayLoc(loc);

        myOpenMapView.invalidate();
    }

    private void updateCenterLoc(Location loc){
        GeoPoint locGeoPoint = new GeoPoint(loc.getLatitude(), loc.getLongitude());
        myMapController.setCenter(locGeoPoint);

        setOverlayLoc(loc);

        myOpenMapView.invalidate();
    }

    private void setOverlayLoc(Location overlayloc){
        GeoPoint overlocGeoPoint = new GeoPoint(overlayloc);
        //---
        overlayItemArray.clear();

        OverlayItem newMyLocationItem = new OverlayItem(
                "My Location", "My Location", overlocGeoPoint);
        overlayItemArray.add(newMyLocationItem);
        //---
    }

    private LocationListener myLocationListener
            = new LocationListener(){

        @Override
        public void onLocationChanged(Location location) {
            // TODO Auto-generated method stub
            updateLoc(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub

        }

    };

    private class MyItemizedIconOverlay extends ItemizedIconOverlay<OverlayItem>{

        public MyItemizedIconOverlay(
                List<OverlayItem> pList,
                org.osmdroid.views.overlay.ItemizedIconOverlay.OnItemGestureListener<OverlayItem> pOnItemGestureListener,
                ResourceProxy pResourceProxy) {
            super(pList, pOnItemGestureListener, pResourceProxy);
            // TODO Auto-generated constructor stub
        }

        @Override
        public void draw(Canvas canvas, MapView mapview, boolean arg2) {
            // TODO Auto-generated method stub
            super.draw(canvas, mapview, arg2);

            if(!overlayItemArray.isEmpty()){

                //overlayItemArray have only ONE element only, so I hard code to get(0)
                GeoPoint in = (GeoPoint) overlayItemArray.get(0).getPoint();

                Point out = new Point();
                mapview.getProjection().toPixels(in, out);

                Bitmap bm = BitmapFactory.decodeResource(getResources(),
                        R.drawable.mylocation);
                canvas.drawBitmap(bm,
                        out.x - bm.getWidth()/2,  //shift the bitmap center
                        out.y - bm.getHeight()/2,  //shift the bitmap center
                        null);
            }
        }

        @Override
        public boolean onSingleTapUp(MotionEvent event, MapView mapView) {
            // TODO Auto-generated method stub
            //return super.onSingleTapUp(event, mapView);
            return true;
        }
    }
}