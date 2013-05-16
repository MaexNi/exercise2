package com.example.muc13_02_bachnigsch;

import java.util.TreeMap;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import de.dfki.ccaal.gestures.Distribution;
import de.dfki.ccaal.gestures.IGestureRecognitionListener;
import de.dfki.ccaal.gestures.IGestureRecognitionService;

/**
 * 
 * @author Max Nigsch
 * @author Martin Bach
 * 
 * The Gesture activity 
 *
 */

public class GestureHandler   {
	
	GameActivity gameActivity;
	long mTimeStamp;
	TreeMap< Long, String> mTreeMap = new TreeMap< Long, String>();
	
	//Constructor
	public GestureHandler(GameActivity gameActivity) {
		this.gameActivity = gameActivity;
	}

	
	/**
	 *  Gesture Stuff
	 */

	IGestureRecognitionService mRecService;
	String mGestureName;
	
	//create gestureListener
	IBinder mGestureListenerStub = new IGestureRecognitionListener.Stub() {
		@Override
		public void onGestureRecognized(Distribution distr) {
			mGestureName = distr.getBestMatch();
			mTimeStamp = System.currentTimeMillis();
			// put gesture name with timestamp into treemap
			mTreeMap.put(mTimeStamp, mGestureName);
				
		}

		@Override
		public void onGestureLearned(String gestureName) throws RemoteException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onTrainingSetDeleted(String trainingSet)
				throws RemoteException {
			// TODO Auto-generated method stub
			
		}
		
	};
	
	//create a service connection to the recognition service
	private ServiceConnection mGestureConn = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className,	IBinder service) {
			mRecService = IGestureRecognitionService.Stub.asInterface(service);
			try {
				// register listener
				mRecService.registerListener(IGestureRecognitionListener.Stub.asInterface(mGestureListenerStub));
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {			
				// start the recognition service in recognition mode with given training set. 
				// new performed gestures are classified with the training set as base.
				mRecService.startClassificationMode("muc");
				
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			// TODO Auto-generated method stub
			
		}
	};
	
	
	public TreeMap getGesture(){
		return mTreeMap;
	}
	

	/**
	 * Service Stuff
	 */
	
	
	protected void bind() {
		
		// bind service
		Intent gestureBindIntent = new Intent("de.dfki.ccaal.gestures.GESTURE_RECOGNIZER");
		gameActivity.bindService(gestureBindIntent, mGestureConn, Context.BIND_AUTO_CREATE);
		
	}
	
	protected void unbind(){
		// unbind service
		try {
			mRecService.unregisterListener(IGestureRecognitionListener.Stub.asInterface(mGestureListenerStub));
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mRecService = null;
		gameActivity.unbindService(mGestureConn);
	}
	


}
