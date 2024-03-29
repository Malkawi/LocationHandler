/* Copyright 2010, 2011, 2012 mapsforge.org
 * Copyright 2012 Hannes Janetzek
 * Copyright 2012 osmdroidbonuspack: M.Kergall
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.oscim.app;

import java.io.FileNotFoundException;  

import org.oscim.app.filepicker.FilePicker;
import org.oscim.app.preferences.EditPreferences;
import org.oscim.core.GeoPoint;
import org.oscim.core.MapPosition;
import org.oscim.core.MercatorProjection;

import org.oscim.database.MapDatabases;
import org.oscim.database.MapOptions;
import org.oscim.renderer.layer.Layer;
import org.oscim.renderer.layer.Layers;
import org.oscim.theme.InternalRenderTheme;
import org.oscim.utils.AndroidUtils;
import org.oscim.view.Compass;
import org.oscim.view.DebugSettings;
import org.oscim.view.MapActivity;
import org.oscim.view.MapView;
import org.oscim.view.MapViewPosition;
import org.osmdroid.location.POI;
import org.osmdroid.overlays.MapEventsOverlay;
import org.osmdroid.overlays.MapEventsReceiver;

import com.google.android.maps.MyLocationOverlay;



import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.PointF;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class TileMap extends MapActivity implements MapEventsReceiver ,  LocationListener {
	static final String TAG = TileMap.class.getSimpleName();

	//MapView mMapView;
	 private LocationManager locationManager;
	private static final String BUNDLE_SHOW_MY_LOCATION = "showMyLocation";
	private static final String BUNDLE_SNAP_TO_LOCATION = "snapToLocation";
	private static final int DIALOG_ENTER_COORDINATES = 0;
	private static final int DIALOG_LOCATION_PROVIDER_DISABLED = 2;
MyLocationOverlay  a;
	// Intents
	private static final int SELECT_RENDER_THEME_FILE = 1;
	protected static final int POIS_REQUEST = 2;

	LocationHandler mLocation;

	private MapDatabases mMapDatabase;
	Compass compass;
	//private WakeLock mWakeLock;
	private Menu mMenu = null;

	POISearch mPoiSearch;
	RouteSearch mRouteSearch;
	private MyLocationOverlay myLocationOverlay;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

		setContentView(R.layout.activity_tilemap);
		mMapView = (MapView) findViewById(R.id.mapView);
		
		
		//this.startActivity(intent)
		
		setMapDatabase(preferences);

		App.map = mMapView;
		App.activity = this;

		mMapView.setClickable(true);
		mMapView.setFocusable(true);

		mLocation = new LocationHandler(this);
		pos = mMapView.getMapViewPosition();
	
	//	myLocationOverlay = new FixedMyLocationOverlay(App.activity, mMapView);		// get the pointers to different system services
		//PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		//mWakeLock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "AMV");

		if (savedInstanceState != null &&
				savedInstanceState.getBoolean(BUNDLE_SHOW_MY_LOCATION) &&
				savedInstanceState.getBoolean(BUNDLE_SNAP_TO_LOCATION))
			mLocation.enableSnapToLocation(false);

		MapEventsOverlay overlay = new MapEventsOverlay(mMapView, this);
		mMapView.getOverlays().add(overlay);
		
		DrawableOverlay dOverlay = new  DrawableOverlay (mMapView);
		//mMapView.setRotation(0);
		App.poiSearch = mPoiSearch = new POISearch();
		
		//Enable the  compass at first run
		compass = new Compass (this,mMapView);
		
		
		mMapView.enableRotation(false); // neither rotation or compass should work at a time
		
		
		mMapView.setmCompass(compass);
		mMapView.enableCompass(true);
		ImageView Compass = (ImageView)  this.findViewById(R.id.imageView1);
		compass.arrowView = Compass;
		
		
		
//a  = new MyLocationOverlay ( this, mMapView);
		registerForContextMenu(mMapView);
		mRouteSearch = new RouteSearch();
mMapView.getOverlays().add(0,dOverlay);
//mMapView.getOverlays().
		//map.getOverlays().add(new AreaSelectionOverlay(map));
		Enable_GPS();
		enbaling_LocationManger();
		final Intent intent = getIntent();
		
	
		if (intent != null) {
			Uri uri = intent.getData();
			if (uri != null) {
				String scheme = uri.getSchemeSpecificPart();
				Log.d(TAG, "got intent " + (scheme == null ? "" : scheme));
			}
		}
	}

	
	

	public void restart ( View V){
		compass.rest();
		compass.stop();
		mMapView.enableCompass(false);
		mMapView.enableRotation(true);
		//compass.stop();
		//compass.stop();
	if(App.map.getOverlayManager().size()>2){
		App.map.getOverlays().remove(2);
		App.map.getOverlays().remove(1);
	}
	//onCreateOptionsMenu(menu);
	mLocation.gotoLastKnownPosition();
	mLocation.enableShowMyLocation(true);
/*	toggleMenuItem(mMenu,
			R.id.menu_position_my_location_enable,
			R.id.menu_position_my_location_disable,
			!mLocation.enableShowMyLocation(true));
		*/
		App.map.redrawMap(true);
		
		
		
		
		
	}

	String provider;
	private void enbaling_LocationManger () {
		
	    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	   
	    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
	            10000,          // 10-second interval.
	            10,             // 10 meters.
	            this);  
	    
	    Criteria criteria = new Criteria();
	    provider = locationManager.getBestProvider(criteria, false);
	    Location location = locationManager.getLastKnownLocation(provider);

	    
	
	    // Initialize the location fields
	    if (location != null) {
	      System.out.println("Provider " + provider + " has been selected.");
	      Toast.makeText(this, "here", Toast.LENGTH_LONG).show();
	      onLocationChanged(location);
	    }
		
            
	  
	}
	MapViewPosition pos;
// this function to ensure that The Gps is on 	
	private void Enable_GPS(){
		
		LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
		boolean enabled = service
		  .isProviderEnabled(LocationManager.GPS_PROVIDER);

		// Check if enabled and if not send user to the GSP settings
		// Better solution would be to display a dialog and suggesting to 
		// go to the settings
		if (!enabled) {
		  Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		  startActivity(intent);
		} 
		
	
	}
	private void setMapDatabase(SharedPreferences preferences) {
		MapDatabases mapDatabaseNew;
		String dbname = preferences.getString("mapDatabase",
				MapDatabases.OSCIMAP_READER.name());

		try {
			mapDatabaseNew = MapDatabases.valueOf(dbname);
		} catch (IllegalArgumentException e) {
			mapDatabaseNew = MapDatabases.OSCIMAP_READER;
		}

		if (mapDatabaseNew != mMapDatabase) {
			Log.d(TAG, "set map database " + mapDatabaseNew);
			MapOptions options = null;

			switch (mapDatabaseNew) {
			case PBMAP_READER:
				options = new MapOptions(mapDatabaseNew);
				options.put("url",
						"http://city.informatik.uni-bremen.de:80/osmstache/test/");
				break;
			case OSCIMAP_READER:
				options = new MapOptions(mapDatabaseNew);
				options.put("url",
						"http://city.informatik.uni-bremen.de:80/osci/map-live/");
				//"http://city.informatik.uni-bremen.de:80/osci/oscim/");
				break;
			case TEST_READER:
				options = new MapOptions(MapDatabases.OSCIMAP_READER);
				options.put("url",
						"http://city.informatik.uni-bremen.de:8000/");
				break;
			case MAP_READER:
				options = new MapOptions(mapDatabaseNew);
				options.put("file",
						"/storage/sdcard0/Download/berlin.map");
				break;
			default:
				break;
			}

			mMapView.setMapDatabase(options);
			mMapDatabase = mapDatabaseNew;
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Uri uri = intent.getData();
		if (uri != null) {
			String scheme = uri.getSchemeSpecificPart();
			Log.d(TAG, "got intent: " + (scheme == null ? "" : scheme));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.options_menu, menu);
		mMenu = menu;
		toggleMenuCheck();
		return true;
	}

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.menu_info_about:
			startActivity(new Intent(this, InfoView.class));
			return true;

		case R.id.menu_position:
			return true;

		case R.id.menu_rotation_enable:
			if (!item.isChecked()) {
				item.setChecked(true);
			
				mMapView.enableCompass(false);
				mMapView.enableRotation(true);
				//compass.stop();
			} else {
				item.setChecked(false);
				mMapView.enableRotation(false);
				mMapView.enableRotation(true);
			}
			toggleMenuCheck();
			return true;

		case R.id.menu_nearby:
			Intent intent = new Intent(this, POIActivity.class);
			startActivityForResult(intent, TileMap.POIS_REQUEST);
			return true;

		case R.id.menu_compass_enable:
			if (!item.isChecked()) {
				item.setChecked(true);
				mMapView.enableRotation(false);
			mMapView.enableCompass(true);
				
				//mMapView.setRotation(0);
				//ompass.start();
			} else {
				item.setChecked(false);
				mMapView.enableCompass(false);
				
				//compass.stop();
				
			}
			toggleMenuCheck();
			return true;

		case R.id.menu_position_my_location_enable:
			toggleMenuItem(mMenu,
					R.id.menu_position_my_location_enable,
					R.id.menu_position_my_location_disable,
					!mLocation.enableShowMyLocation(true));
			return true;

		case R.id.menu_position_my_location_disable:
			toggleMenuItem(mMenu,
					R.id.menu_position_my_location_enable,
					R.id.menu_position_my_location_disable,
					mLocation.disableShowMyLocation());
			return true;

		case R.id.menu_position_enter_coordinates:
			showDialog(DIALOG_ENTER_COORDINATES);
			return true;

		case R.id.menu_position_map_center:
			MapPosition mapCenter = mMapView.getMapFileCenter();
			if (mapCenter != null)
				mMapView.setMapCenter(mapCenter);
			return true;

		case R.id.menu_preferences:
			startActivity(new Intent(this, EditPreferences.class));
			overridePendingTransition(R.anim.slide_right, R.anim.slide_left2);
			return true;

		default:
			return false;
		}
	}

	private void toggleMenuCheck() {
		mMenu.findItem(R.id.menu_rotation_enable).setChecked(mMapView.getRotationEnabled());
		mMenu.findItem(R.id.menu_compass_enable).setChecked(mMapView.getCompassEnabled());
	}

	private static void toggleMenuItem(Menu menu, int id, int id2, boolean enable) {
		menu.findItem(id).setVisible(enable);
		menu.findItem(id).setEnabled(enable);
		menu.findItem(id2).setVisible(!enable);
		menu.findItem(id2).setEnabled(!enable);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		if (!isPreHoneyComb()) {
			menu.clear();
			onCreateOptionsMenu(menu);
		}

		toggleMenuItem(menu,
				R.id.menu_position_my_location_enable,
				R.id.menu_position_my_location_disable,
				!mLocation.isShowMyLocationEnabled());

		if (mMapDatabase == MapDatabases.MAP_READER) {
			//menu.findItem(R.id.menu_mapfile).setVisible(true);
			menu.findItem(R.id.menu_position_map_center).setVisible(true);
		} else {
			//menu.findItem(R.id.menu_mapfile).setVisible(false);
			menu.findItem(R.id.menu_position_map_center).setVisible(false);
		}

		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onTrackballEvent(MotionEvent event) {
		// forward the event to the MapView
		return mMapView.onTrackballEvent(event);
	}

	// private void startMapFilePicker() {
	// FilePicker.setFileDisplayFilter(FILE_FILTER_EXTENSION_MAP);
	// FilePicker.setFileSelectFilter(new ValidMapFile());
	// startActivityForResult(new Intent(this, FilePicker.class),
	// SELECT_MAP_FILE);
	// }

	//	private void startRenderThemePicker() {
	//		FilePicker.setFileDisplayFilter(FILE_FILTER_EXTENSION_XML);
	//		FilePicker.setFileSelectFilter(new ValidRenderTheme());
	//		startActivityForResult(new Intent(this, FilePicker.class),
	//				SELECT_RENDER_THEME_FILE);
	//	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		switch (requestCode) {
		case POIS_REQUEST:
			Log.d(TAG, "result: POIS_REQUEST");
			if (resultCode == RESULT_OK) {
				int id = intent.getIntExtra("ID", 0);
				Log.d(TAG, "result: POIS_REQUEST: " + id);

				mPoiSearch.poiMarkers.showBubbleOnItem(id);

				POI poi = mPoiSearch.getPOIs().get(id);

				if (poi.bbox != null)
					mMapView.getMapViewPosition().animateTo(poi.bbox);
				else
					mMapView.getMapViewPosition().animateTo(poi.location);
			}
			break;
		case SELECT_RENDER_THEME_FILE:
			if (resultCode == RESULT_OK && intent != null
					&& intent.getStringExtra(FilePicker.SELECTED_FILE) != null) {
				try {
					mMapView.setRenderTheme(intent
							.getStringExtra(FilePicker.SELECTED_FILE));
				} catch (FileNotFoundException e) {
					showToastOnUiThread(e.getLocalizedMessage());
				}
			}
			break;
		default:
			break;
		}

		// if (requestCode == SELECT_MAP_FILE) {
		// if (resultCode == RESULT_OK) {
		//
		// location.disableSnapToLocation(true);
		//
		// if (intent != null) {
		// if (intent.getStringExtra(FilePicker.SELECTED_FILE) != null) {
		// map.setMapFile(intent
		// .getStringExtra(FilePicker.SELECTED_FILE));
		// }
		// }
		// } else if (resultCode == RESULT_CANCELED) {
		// startActivity(new Intent(this, EditPreferences.class));
		// }
		// } else
		//		if (requestCode == SELECT_RENDER_THEME_FILE && resultCode == RESULT_OK
		//				&& intent != null
		//				&& intent.getStringExtra(FilePicker.SELECTED_FILE) != null) {
		//			try {
		//				map.setRenderTheme(intent
		//						.getStringExtra(FilePicker.SELECTED_FILE));
		//			} catch (FileNotFoundException e) {
		//				showToastOnUiThread(e.getLocalizedMessage());
		//			}
		//		}
	}

	static boolean isPreHoneyComb() {
		return Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		if (id == DIALOG_ENTER_COORDINATES) {
			if (mLocationDialog == null)
				mLocationDialog = new LocationDialog();

			return mLocationDialog.createDialog(this);

		} else if (id == DIALOG_LOCATION_PROVIDER_DISABLED) {
			builder.setIcon(android.R.drawable.ic_menu_info_details);
			builder.setTitle(R.string.error);
			builder.setMessage(R.string.no_location_provider_available);
			builder.setPositiveButton(R.string.ok, null);
			return builder.create();
		} else {
			// no dialog will be created
			return null;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mLocation.disableShowMyLocation();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// release the wake lock if necessary
		//if (mWakeLock.isHeld()) {
		//	mWakeLock.release();
		//}
	}

	LocationDialog mLocationDialog;

	@SuppressWarnings("deprecation")
	@Override
	protected void onPrepareDialog(int id, final Dialog dialog) {
		if (id == DIALOG_ENTER_COORDINATES) {

			mLocationDialog.prepareDialog(mMapView, dialog);

		} else {
			super.onPrepareDialog(id, dialog);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

		if (preferences.contains("mapDatabase")) {
			setMapDatabase(preferences);
		}
		if (preferences.contains("theme")) {
			String name = preferences.getString("theme",
					"OSMARENDER");
			InternalRenderTheme theme = null;

			try {
				theme = InternalRenderTheme.valueOf(name);
			} catch (IllegalArgumentException e) {
			}
			if (theme == null)
				mMapView.setRenderTheme(InternalRenderTheme.DEFAULT);
			else
				mMapView.setRenderTheme(theme);
			
		}

		if (preferences.getBoolean("fullscreen", false)) {
			Log.i("mapviewer", "FULLSCREEN");
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		} else {
			Log.i("mapviewer", "NO FULLSCREEN");
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		}
		if (preferences.getBoolean("fixOrientation", true)) {
			this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			// this all returns the orientation which is not currently active?!
			// getWindow().getWindowManager().getDefaultDisplay().getRotation());
			// getWindow().getWindowManager().getDefaultDisplay().getOrientation());
		}

		// try {
		// String textScaleDefault =
		// getString(R.string.preferences_text_scale_default);
		// map.setTextScale(Float.parseFloat(preferences.getString("textScale",
		// textScaleDefault)));
		// } catch (NumberFormatException e) {
		// map.setTextScale(1);
		// }

		// MapScaleBar mapScaleBar = mapView.getMapScaleBar();
		// mapScaleBar.setShowMapScaleBar(preferences.getBoolean("showScaleBar",
		// false));
		// String scaleBarUnitDefault =
		// getString(R.string.preferences_scale_bar_unit_default);
		// String scaleBarUnit = preferences.getString("scaleBarUnit",
		// scaleBarUnitDefault);
		// mapScaleBar.setImperialUnits(scaleBarUnit.equals("imperial"));

		//if (preferences.getBoolean("wakeLock", false) && !mWakeLock.isHeld()) {
		//	mWakeLock.acquire();
		//}

		boolean drawTileFrames = preferences.getBoolean("drawTileFrames", false);
		boolean drawTileCoordinates = preferences.getBoolean("drawTileCoordinates", false);
		boolean disablePolygons = preferences.getBoolean("disablePolygons", false);
		boolean drawUnmatchedWays = preferences.getBoolean("drawUnmatchedWays", false);
		boolean debugLabels = preferences.getBoolean("debugLabels", false);

		DebugSettings cur = mMapView.getDebugSettings();
		if (cur.disablePolygons != disablePolygons
				|| cur.drawTileCoordinates != drawTileCoordinates
				|| cur.drawTileFrames != drawTileFrames
				|| cur.debugTheme != drawUnmatchedWays
				|| cur.debugLabels != debugLabels) {

			DebugSettings debugSettings = new DebugSettings(drawTileCoordinates,
					drawTileFrames, disablePolygons, drawUnmatchedWays, debugLabels);

			mMapView.setDebugSettings(debugSettings);
		}

		mMapView.redrawMap(false);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(BUNDLE_SHOW_MY_LOCATION, mLocation.isShowMyLocationEnabled());
		// outState.putBoolean(BUNDLE_CENTER_AT_FIRST_FIX,
		// mMyLocationListener.isCenterAtFirstFix());
		// outState.putBoolean(BUNDLE_SNAP_TO_LOCATION, mSnapToLocation);
	}

	/**
	 * Uses the UI thread to display the given text message as toast
	 * notification.
	 * @param text
	 *            the text message to display
	 */
	void showToastOnUiThread(final String text) {

		if (AndroidUtils.currentThreadIsUiThread()) {
			Toast toast = Toast.makeText(this, text, Toast.LENGTH_LONG);
			toast.show();
		} else {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast toast = Toast.makeText(TileMap.this, text, Toast.LENGTH_LONG);
					toast.show();
				}
			});
		}
	}

	//----------- Context Menu when clicking on the map
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		// Log.d(TAG, "create context menu");

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.map_menu, menu);

		if (mPoiSearch.getPOIs().isEmpty())
			menu.removeItem(R.id.menu_clear_poi);

		if (mRouteSearch.isEmpty())
			menu.removeItem(R.id.menu_clear_route);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		//	Log.d(TAG, "context menu item selected " + item.getItemId());

		if (mPoiSearch.onContextItemSelected(item))
			return true;

		if (mRouteSearch.onContextItemSelected(item))
			return true;

		return super.onContextItemSelected(item);
	}

	//------------ MapEventsReceiver implementation

	@Override
	public boolean singleTapUpHelper(GeoPoint p) {
		mPoiSearch.singleTapUp();
		mRouteSearch.singleTapUp();
		return false;
	}

	@Override
	public boolean longPressHelper(GeoPoint p) {
		mRouteSearch.longPress(p);

		openContextMenu(mMapView);

		return true;
	}


	@Override
	public void onStart() {
		super.onStart();
		Log.d(TAG, "start compass");
		compass.start();
	}

	
	
	

	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub
		//Toast.makeText(this, "location changed", Toast.LENGTH_LONG ).show();
		//org.oscim.core.PointF  out =  new  org.oscim.core.PointF  () ;
	//pos.project(new  GeoPoint(arg0.getLatitude()*1E6, arg0.getLongitude() * 1E6), out);
		//mMapView.getMapViewPosition().moveMap(.2F, .1F);
	//	mMapView.getMapPosition().animateTo(new  GeoPoint(arg0.getLatitude()  , arg0.getLongitude()  ));
		
		
		//mMapView.getMapPosition().getMapPosition().zoomLevel= (byte) mMapView.getMapPosition().MAX_ZOOMLEVEL;
		
	//Toast.makeText(this, String.valueOf(MercatorProjection.longitudeToPixelX(arg0.getLongitude()*1E6 , mMapView.getMapPosition().getMapPosition().zoomLevel)), 800).show();
	//Toast.makeText(this, String.valueOf(out.y), 700).show()f;
	}



	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
	}

}
