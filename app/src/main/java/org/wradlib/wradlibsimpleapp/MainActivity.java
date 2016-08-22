package org.wradlib.wradlibsimpleapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.carto.core.BinaryData;
import com.carto.core.MapPos;
import com.carto.core.Variant;
import com.carto.datasources.LocalVectorDataSource;
import com.carto.datasources.PackageManagerTileDataSource;
import com.carto.layers.CartoBaseMapStyle;
import com.carto.layers.CartoOnlineVectorTileLayer;
import com.carto.layers.Layer;
import com.carto.layers.VectorLayer;
import com.carto.layers.VectorTileLayer;
import com.carto.packagemanager.CartoPackageManager;
import com.carto.projections.Projection;
import com.carto.services.CartoVisBuilder;
import com.carto.services.CartoVisLoader;
import com.carto.styles.CompiledStyleSet;
import com.carto.ui.MapView;
import com.carto.utils.AssetUtils;
import com.carto.utils.ZippedAssetPackage;
import com.carto.vectortiles.MBVectorTileDecoder;

import java.io.File;
import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    static final String LICENSE = "XTUN3Q0ZDc1BGVmtzSng1bjVMdHNJbmFrc0I3d2psT3hBaFFTTUF1L21NSCt1M3FQcnYrYkxOWnJqTFRUbnc9PQoKcHJvZHVjdHM9c2RrLWFuZHJvaWQtNC4qLHNkay1pb3MtNC4qCnBhY2thZ2VOYW1lPSoKd2F0ZXJtYXJrPWN1c3RvbQp2YWxpZFVudGlsPTIwMTYtMDktMDEKdXNlcktleT0zNjNmMTc3Y2YzNjUwMzBmMWVlOGI5M2NiZjU2NzhkYQo=";
    MapView mapView;
    CartoPackageManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MapView.registerLicense(LICENSE, getApplicationContext());

        mapView = (MapView) this.findViewById(R.id.map_view);

        final String bonn = "bbox(7.0082,50.7284,7.1582,50.7454)";

        File folder = new File(getApplicationContext().getExternalFilesDir(null), "map_packages");

        if (!folder.isDirectory()) {
            folder.mkdir();
        }

        try {
            manager = new CartoPackageManager("nutiteq.osm", folder.getAbsolutePath());
        }
        catch (IOException e) {
            System.out.println("Exception: " + e.getMessage());
        }

        if (manager == null) {
            Toast.makeText(this, "Unable to initialize package manager", Toast.LENGTH_LONG).show();
            return;
        }

        manager.start();

        if (manager.getLocalPackage(bonn) == null) {
            manager.startPackageDownload(bonn);
        }


        PackageManagerTileDataSource source = new PackageManagerTileDataSource(manager);
        BinaryData styleBytes = AssetUtils.loadAsset("nutibright-v3.zip");
        CompiledStyleSet styleSet = new CompiledStyleSet(new ZippedAssetPackage(styleBytes));

        MBVectorTileDecoder decoder = new MBVectorTileDecoder(styleSet);

        VectorTileLayer layer = new VectorTileLayer(source, decoder);

        mapView.getLayers().add(layer);

        MapPos bonnPos = mapView.getOptions().getBaseProjection().fromWgs84(new MapPos(7.0982, 50.7374));

        mapView.setFocusPos(bonnPos, 0);
        mapView.setZoom(14, 0);
        String url = "http://documentation.carto.com/api/v2/viz/2b13c956-e7c1-11e2-806b-5404a6a683d5/viz.json";
        updateVis(url);


    }

    protected void updateVis(final String url) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                mapView.getLayers().clear();

                // Create overlay layer for popups
                Projection proj = mapView.getOptions().getBaseProjection();
                LocalVectorDataSource dataSource = new LocalVectorDataSource(proj);
                VectorLayer vectorLayer = new VectorLayer(dataSource);

                // Create VIS loader
                CartoVisLoader loader = new CartoVisLoader();
                loader.setDefaultVectorLayerMode(true);
                MyCartoVisBuilder visBuilder = new MyCartoVisBuilder(vectorLayer);
                try {
                    loader.loadVis(visBuilder, url);
                }
                catch (IOException e) {
                    Log.e("EXCEPTION", "Exception: " + e);
                }

                // Add the created popup overlay layer on top of all visJSON layers
                mapView.getLayers().add(vectorLayer);
            }
        });

        thread.start(); // TODO: should serialize execution
    }

    private class MyCartoVisBuilder extends CartoVisBuilder {
        private VectorLayer vectorLayer; // vector layer for popups

        public MyCartoVisBuilder(VectorLayer vectorLayer) {
            this.vectorLayer = vectorLayer;
        }

        @Override
        public void setCenter(MapPos mapPos) {
            MapPos position = mapView.getOptions().getBaseProjection().fromWgs84(mapPos);
            mapView.setFocusPos(position, 1.0f);
        }

        @Override
        public void setZoom(float zoom) {
            mapView.setZoom(zoom, 1.0f);
        }

        @Override
        public void addLayer(Layer layer, Variant attributes) {
            // Add the layer to the map view
            mapView.getLayers().add(layer);
        }
    }

}

