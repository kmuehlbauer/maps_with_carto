package org.wradlib.wradlibsimpleapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.carto.core.MapPos;
import com.carto.layers.CartoBaseMapStyle;
import com.carto.layers.CartoOnlineVectorTileLayer;
import com.carto.projections.Projection;
import com.carto.ui.MapView;


public class MainActivity extends AppCompatActivity {

    static final String LICENSE = "XTUN3Q0ZDc1BGVmtzSng1bjVMdHNJbmFrc0I3d2psT3hBaFFTTUF1L21NSCt1M3FQcnYrYkxOWnJqTFRUbnc9PQoKcHJvZHVjdHM9c2RrLWFuZHJvaWQtNC4qLHNkay1pb3MtNC4qCnBhY2thZ2VOYW1lPSoKd2F0ZXJtYXJrPWN1c3RvbQp2YWxpZFVudGlsPTIwMTYtMDktMDEKdXNlcktleT0zNjNmMTc3Y2YzNjUwMzBmMWVlOGI5M2NiZjU2NzhkYQo=";
    MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MapView.registerLicense(LICENSE, getApplicationContext());

        MapView mapView = (MapView) this.findViewById(R.id.map_view);

        CartoOnlineVectorTileLayer layer =
                new CartoOnlineVectorTileLayer(CartoBaseMapStyle.CARTO_BASEMAP_STYLE_DEFAULT);
        mapView.getLayers().add(layer);

        Projection projection = mapView.getOptions().getBaseProjection();

        MapPos berlin = projection.fromWgs84(new MapPos(7.141209, 50.704452));
        mapView.setFocusPos(berlin, 0);
        mapView.setZoom(10, 0);


    }
}
