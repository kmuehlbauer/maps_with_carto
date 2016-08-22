package org.wradlib.wradlibsimpleapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.carto.core.BinaryData;
import com.carto.core.MapPos;
import com.carto.datasources.PackageManagerTileDataSource;
import com.carto.layers.CartoBaseMapStyle;
import com.carto.layers.CartoOnlineVectorTileLayer;
import com.carto.layers.VectorTileLayer;
import com.carto.packagemanager.CartoPackageManager;
import com.carto.projections.Projection;
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

    }
}
