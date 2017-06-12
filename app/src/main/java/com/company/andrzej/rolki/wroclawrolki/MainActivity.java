package com.company.andrzej.rolki.wroclawrolki;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;

import com.github.bkhezry.extramaputils.builder.ExtraMarkerBuilder;
import com.github.bkhezry.extramaputils.builder.ExtraPolygonBuilder;
import com.github.bkhezry.extramaputils.builder.ExtraPolylineBuilder;
import com.github.bkhezry.extramaputils.builder.ViewOptionBuilder;
import com.github.bkhezry.extramaputils.model.ExtraMarker;
import com.github.bkhezry.extramaputils.model.ViewOption;
import com.github.bkhezry.extramaputils.utils.MapUtils;
import com.github.bkhezry.mapdrawingtools.model.DataModel;
import com.github.bkhezry.mapdrawingtools.model.DrawingOption;
import com.github.bkhezry.mapdrawingtools.model.DrawingOptionBuilder;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import static com.github.bkhezry.mapdrawingtools.ui.MapsActivity.POINTS;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    public static final int REQUEST_CODE = 1;
    private MapView mMap;
    private GoogleMap googleMap;
    private DrawingOption.DrawingType currentDrawingType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMap = (MapView) findViewById(R.id.mapLite);
        mMap.onCreate(savedInstanceState);
        mMap.getMapAsync(this);
        setUpButtons();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        MapsInitializer.initialize(this);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(51.110, 17.030), 13));
    }

    private void setUpButtons() {
        AppCompatButton btnDrawPolygon = (AppCompatButton) findViewById(R.id.btnDrawPolygon);
        btnDrawPolygon.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(this, R.drawable.ic_polygon), null, null, null);
        btnDrawPolygon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentDrawingType = DrawingOption.DrawingType.POLYGON;
                Intent intent =
                        new DrawingOptionBuilder()
                                .withLocation(35.744502, 51.368966)
                                .withFillColor(Color.argb(60, 0, 0, 255))
                                .withStrokeColor(Color.argb(100, 255, 0, 0))
                                .withStrokeWidth(3)
                                .withRequestGPSEnabling(false)
                                .withDrawingType(currentDrawingType)
                                .build(getApplicationContext());
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
        AppCompatButton btnDrawPolyline = (AppCompatButton) findViewById(R.id.btnDrawPolyline);
        btnDrawPolyline.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(this, R.drawable.ic_polyline), null, null, null);
        btnDrawPolyline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentDrawingType = DrawingOption.DrawingType.POLYLINE;
                Intent intent =
                        new DrawingOptionBuilder()
                                .withLocation(35.744502, 51.368966)
                                .withFillColor(Color.argb(60, 0, 0, 255))
                                .withStrokeColor(Color.argb(100, 255, 0, 0))
                                .withStrokeWidth(3)
                                .withRequestGPSEnabling(false)
                                .withDrawingType(currentDrawingType)
                                .build(getApplicationContext());
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
        AppCompatButton btnDrawPoints = (AppCompatButton) findViewById(R.id.btnDrawPoints);
        btnDrawPoints.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(this, R.drawable.ic_point), null, null, null);
        btnDrawPoints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentDrawingType = DrawingOption.DrawingType.POINT;
                Intent intent =
                        new DrawingOptionBuilder()
                                .withLocation(35.744502, 51.368966)
                                .withFillColor(Color.argb(60, 0, 0, 255))
                                .withStrokeColor(Color.argb(100, 0, 0, 0))
                                .withStrokeWidth(3)
                                .withRequestGPSEnabling(false)
                                .withDrawingType(currentDrawingType)
                                .build(getApplicationContext());
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE && data != null) {
            DataModel dataModel =
                    data.getExtras().getParcelable(POINTS);
            ViewOption viewOption;
            if (currentDrawingType == DrawingOption.DrawingType.POLYGON) {
                viewOption = new ViewOptionBuilder()
                        .withIsListView(false)
                        .withPolygons(
                                new ExtraPolygonBuilder()
                                        .setFillColor(Color.argb(100, 0, 0, 255))
                                        .setPoints(dataModel.getPoints())
                                        .setStrokeColor(Color.argb(100, 255, 0, 0))
                                        .setStrokeWidth(5)
                                        .build())
                        .build();
            } else if (currentDrawingType == DrawingOption.DrawingType.POLYLINE) {
                viewOption = new ViewOptionBuilder()
                        .withIsListView(false)
                        .withPolylines(
                                new ExtraPolylineBuilder()
                                        .setPoints(dataModel.getPoints())
                                        .setStrokeColor(Color.argb(100, 255, 0, 0))
                                        .setStrokeWidth(5)
                                        .build()
                        ).build();

            } else {
                viewOption = new ViewOptionBuilder()
                        .withIsListView(false)
                        .withMarkers(getMarkers(dataModel.getPoints()))
                        .build();
            }
            googleMap.clear();
            MapUtils.showElements(viewOption, googleMap, this);

        }
    }

    private List<ExtraMarker> getMarkers(LatLng[] points) {
        List<ExtraMarker> extraMarkers = new ArrayList<>();
        @IdRes int icon = R.drawable.ic_beenhere_blue_grey_500_24dp;
        for (LatLng latLng : points) {
            ExtraMarker extraMarker =
                    new ExtraMarkerBuilder()
                            .setCenter(latLng)
                            .setIcon(icon)
                            .build();
            extraMarkers.add(extraMarker);
        }
        return extraMarkers;
    }
}
