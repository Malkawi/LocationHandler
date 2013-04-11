package org.oscim.app;

import org.oscim.core.MapPosition;
import org.oscim.core.MercatorProjection;
import org.oscim.overlay.ItemizedOverlay;
import org.oscim.overlay.Overlay;
import org.oscim.overlay.OverlayItem.HotspotPlace;
import org.oscim.renderer.GLRenderer.Matrices;
import org.oscim.renderer.layer.SymbolItem;
import org.oscim.renderer.layer.SymbolLayer;
import org.oscim.renderer.overlays.BasicOverlay;
import org.oscim.renderer.overlays.CircleOverlay;

import org.oscim.view.MapView;

import android.widget.Toast;


public class DrawableOverlay extends Overlay {

	  private  float  lat ; 
	  
	  private float accuracy=2;
	  
	public float getAccuracy() {
		
		
		return accuracy;
		
	}

	public void setAccuracy(float accuracy) {
		DrawableLayer  temp = ( DrawableLayer) mLayer ;
		
		Toast.makeText(App.activity, String.valueOf(accuracy), Toast.LENGTH_SHORT).show();
		temp.accuracy=accuracy;
		this.accuracy = accuracy;
	}

	public float getLat() {
		return lat;
	}

	public void setLat(float lat) {
		this.lat = lat;
	}

	public float getLog() {
		return log;
	}

	public void setLog(float log) {
		this.log = log;
	}
	Triangle ta ;
	public   float log ;
	  private MapView Mv;
	class DrawableLayer extends BasicOverlay {

		private boolean initialized;
   public boolean oneTime;
   
   private float accuracy=2;
   
		public DrawableLayer(MapView mapView) {
			super(mapView);
  Mv = mapView;
			SymbolItem it = new SymbolItem();
 ta = new Triangle ();
			// draw item at center
			it.billboard = false;
			it.x = 0;
			it.y = 0;
			it.drawable = mapView.getContext().getResources().getDrawable(R.drawable.marker_default);
			
		
			
		//	it.drawable=resize (it.drawable,(int) MercatorProjection.latitudeToPixelY(   Math.abs(Mv.getMapPosition().getMapPosition().lat - mathOp.newLat(Mv.getMapPosition().getMapPosition().lat, accuracy) ),Mv.getMapPosition().getMapPosition().zoomLevel));
			ItemizedOverlay.boundToHotspot(it.drawable, HotspotPlace.CENTER);
// Toast.makeText(App.activity, "graphics", Toast.LENGTH_LONG).show();
			SymbolLayer l = new SymbolLayer();
			l.addSymbol(it);
			layers.textureLayers = l;
		}

		
		
		@Override
		public void update(MapPosition curPos, boolean positionChanged,
				boolean tilesChanged, Matrices matrices) {
	      //  GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
	//	ta.draw();
			//glBeginGLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

			// keep position constant (or update layer relative to new position)
			//mMapPosition.copy(curPos);
//Toast.makeText(App.activity, "Graphics", 2).show();
			if (!initialized ) {
				// fix at initial position, (see RenderOverlay.setMatrix, how it is used)
			
				mMapPosition.copy(curPos);
				//updateMapPosition();
			//	Toast.makeText(App.activity, String.valueOf(accuracy), Toast.LENGTH_SHORT).show();
				initialized= true;

				// render symbol to texture and create  vertex data
				((SymbolLayer)layers.textureLayers).prepare();

				// compile VBOs uploaded and drawn (i.e. flag to call 'BasicOverlay.compile()')
				
			
				
				
				newData = true;
				mMapPosition.setFromLatLon(lat, log, mMapPosition.zoomLevel);
			}

			
			
			
			if ( curPos.zoomLevel > 13 &&  ! oneTime){
				layers.clear();	
				mMapPosition.copy(curPos);
					oneTime=true ;
					SymbolItem it = new SymbolItem();

					// draw item at center
					it.billboard = true;
					it.x = 0;
					it.y = 0;
					it.drawable = Mv.getContext().getResources().getDrawable(R.drawable.circle03);

				
					ItemizedOverlay.boundToHotspot(it.drawable, HotspotPlace.CENTER);
					//it.drawable=resize (it.drawable,(int) MercatorProjection.latitudeToPixelY(   Math.abs(Mv.getMapPosition().getMapPosition().lat - mathOp.newLat(Mv.getMapPosition().getMapPosition().lat, accuracy) ),Mv.getMapPosition().getMapPosition().zoomLevel));
					SymbolLayer l = new SymbolLayer();
					//l.addSymbol(it);
		
				
				SymbolItem it2 = new SymbolItem();
					it2.billboard = true;
					it2.x = 0;
					it2.y = 0;
					it2.drawable = Mv.getContext().getResources().getDrawable(R.drawable.person);
					ItemizedOverlay.boundToHotspot(it2.drawable, HotspotPlace.CENTER);
					
					l.addSymbol(it2);
					//l.addSymbol(it);
					
					layers.textureLayers = l;
					((SymbolLayer)layers.textureLayers).prepare();
					newData = true;	
					mMapPosition.setFromLatLon(lat, log, mMapPosition.zoomLevel);
				}
			
			else  if (curPos.zoomLevel <=13&& oneTime){
				mMapPosition.copy(curPos);
				layers.clear();	
				oneTime = false ;
				SymbolItem it = new SymbolItem();

				// draw item at center
				it.billboard = false ;
				it.x = 0;
				it.y = 0F;
				it.drawable = Mv.getContext().getResources().getDrawable(R.drawable.marker_default);


				ItemizedOverlay.boundToHotspot(it.drawable, HotspotPlace.CENTER);

				SymbolLayer l = new SymbolLayer();
				l.addSymbol(it);

				layers.textureLayers = l;
				((SymbolLayer)layers.textureLayers).prepare();
				
				mMapPosition.setFromLatLon(lat, log, mMapPosition.zoomLevel);
				
				newData = true;	
			}
			// this should allow to draw the item at any lat/lon
			// (set center to be at lat/lon):
			mMapPosition.setFromLatLon(lat, log, mMapPosition.zoomLevel);
		}
	}


	public DrawableOverlay(MapView mapView) {
		super(mapView);

		mLayer = new DrawableLayer(mapView);


	}

}



