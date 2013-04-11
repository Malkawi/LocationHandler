package org.oscim.app;

import java.io.File;

import org.oscim.view.MapScaleBar;
import org.oscim.view.MapView;



import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * An application which demonstrates how to use two MapView instances at the same time.
 */
public class DualMapViewer extends org.oscim.view.MapActivity {
        private static final File MAP_FILE = new File(Environment.getExternalStorageDirectory().getPath(), "berlin.map");

        private MapView mapView1;
        private MapView mapView2;

        @Override
        public boolean onKeyDown(int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                        finish();
                        return true;
                }
                // forward the event to both MapViews for simultaneous movement
                return this.mapView1.onKeyDown(keyCode, event) | this.mapView2.onKeyDown(keyCode, event);
        }

        @Override
        public boolean onKeyUp(int keyCode, KeyEvent event) {
                // forward the event to both MapViews for simultaneous movement
                return this.mapView1.onKeyUp(keyCode, event) | this.mapView2.onKeyUp(keyCode, event);
        }

        @Override
        public boolean onTrackballEvent(MotionEvent event) {
                // forward the event to both MapViews for simultaneous movement
                return this.mapView1.onTrackballEvent(event) | this.mapView2.onTrackballEvent(event);
        }

        private MapView createMapView(boolean imperialUnits) {
                MapView mapView = new MapView(this);
                mapView.setClickable(true);
               // mapView.en
               /// mapView.getMapFileCenter().setMoveSpeedFactor(1);

               

            

                return mapView;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                this.mapView1 = createMapView(false);
                this.mapView2 = createMapView(true);

                // create a LineaLayout that contains both MapViews
                LinearLayout linearLayout = new LinearLayout(this);

                // if the device orientation is portrait, change the orientation to vertical
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                        linearLayout.setOrientation(LinearLayout.VERTICAL);
                }

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT, 1);
                this.mapView1.setLayoutParams(layoutParams);
                this.mapView2.setLayoutParams(layoutParams);

                // add both MapViews to the LinearLayout
                linearLayout.addView(this.mapView1);
                linearLayout.addView(this.mapView2);
                setContentView(linearLayout);
        }
}