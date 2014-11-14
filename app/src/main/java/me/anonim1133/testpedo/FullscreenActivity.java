package me.anonim1133.testpedo;

import me.anonim1133.testpedo.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;



/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class FullscreenActivity extends Activity implements SensorEventListener{

	long lastTime = 0;
	long numSteps = 0;
	long numPSteps = 0;
	double treshold = 0.0;

	SensorManager sensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);




	    SeekBar bar = (SeekBar) findViewById(R.id.seekBar);
	    bar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
		    int progress = 0;

		    @Override
		    public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
			    treshold = 10 + ((progresValue+0.1)/10);

			    TextView txt = (TextView) findViewById(R.id.txt_treshold);
			    txt.setText("Threshold: " + Double.toString(treshold));
		    }

		    @Override
		    public void onStartTrackingTouch(SeekBar seekBar) {

		    }

		    @Override
		    public void onStopTrackingTouch(SeekBar seekBar) {

		    }
	    });





	    sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

	    registerAcclerometer();
	    registerPedometer();
	    Log.d("pedo", "main");
    }


    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */


	@Override
	public void onBackPressed() {

		sensorManager.unregisterListener(this);

		closeApp();

	}

	public void closeApp(){
		finish();

		//unregister all used sensors - step, acc, gps, timer...
	}

	@Override
	public void onSensorChanged(SensorEvent event) {

		if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			double x = event.values[0];
			double y = event.values[1];
			double z = event.values[2];
			double v;

			v = Math.abs(
					Math.sqrt(
							Math.pow(x, 2)
									+
									Math.pow(y, 2)
									+
									Math.pow(z, 2)
					));

			v = Math.abs(v);

			TextView txtCounter = (TextView) findViewById(R.id.txtKrocz);
			TextView txtTime = (TextView) findViewById(R.id.txtTime);


			long actualTime = System.currentTimeMillis();

			if ((actualTime - lastTime) > 300 && v > treshold) { //11.91
				numSteps++;
				txtCounter.setText(Long.toString(numSteps));

				txtTime.setText("Ostatni krok: " + (actualTime - lastTime));

				lastTime = System.currentTimeMillis();
			}
		}


		//Pedometr
		if(event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR){
			numPSteps++;// step counter - Float.floatToIntBits(event.values[0]);
			TextView txtCounter = (TextView) findViewById(R.id.txtKroczPedo);
			txtCounter.setText(Long.toString(numPSteps));
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int i) {

	}

	public boolean registerAcclerometer(){
		Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
		return true;
	}

	public boolean registerPedometer(){
		Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
		sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
		return true;
	}

	public void reset(View w){
		numPSteps = 0;
		TextView txtCounter = (TextView) findViewById(R.id.txtKroczPedo);
		txtCounter.setText(Long.toString(numPSteps));
		numSteps = 0;
		txtCounter = (TextView) findViewById(R.id.txtKrocz);
		txtCounter.setText(Long.toString(numPSteps));
	}
}
