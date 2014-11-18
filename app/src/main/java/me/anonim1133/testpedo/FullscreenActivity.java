package me.anonim1133.testpedo;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import me.anonim1133.testpedo.util.SystemUiHider;



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
	short inactive_steps = 0;
	double threshold = 11.91;

	short time_between_steps = 300;
	float activity = 0;

	SensorManager sensorManager;
	Average avg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);




	    SeekBar bar = (SeekBar) findViewById(R.id.seekBar);
	    bar.setProgress(19);
	    bar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
		    @Override
		    public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
			    threshold = 10 + ((progresValue+0.1)/10);

			    TextView txt = (TextView) findViewById(R.id.txt_treshold);
			    txt.setText("Threshold: " + Double.toString(threshold));
		    }

		    @Override
		    public void onStartTrackingTouch(SeekBar seekBar) {

		    }

		    @Override
		    public void onStopTrackingTouch(SeekBar seekBar) {

		    }
	    });





	    sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
	    avg = new Average((short)10);

	    registerAcclerometer();
	    registerPedometer();

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

			long actualTime = System.currentTimeMillis();
			long difference = actualTime - lastTime;

			if ((difference > time_between_steps) && (v > threshold)) {
			//11.91 = chodzenie
			//20.01 = skoki i przysiady

				//Zabezpieczenie przed pierwszymi krokami ( pierwsze 7 kroków, takze po 5s nieaktywności )
				//które mogą być ruchem telefonu chowanego do kieszeni.

				if( (difference) > 5000 || (inactive_steps < 5 && inactive_steps != 0) || numSteps == 0) {
					inactive_steps++;

					if(inactive_steps >= 5){
						inactive_steps = 0;
						numSteps++;


					}

				}else{
					//zliczanie kroków
					numSteps++;
					ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
					toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 50);
					//zliczanie "aktywności" podczas chodu
					float average = avg.add((short)difference);
					//1000f bo chcemy aby jeden krok trwał mniej niż 1s
					if(average != 0 && average < 1000.0f){
						float tmp = 1000.0f - (average/1000);
						activity += tmp/1000;
					}
				}

				//utrzymywanie zmiennej czasowej możliwie blisko częstotliwości kroków.
				//utrzymujemy zmienną w zakresie 256-1024
				if((difference - time_between_steps) > 100 && time_between_steps < 1024){
					//tempo spada
					time_between_steps += 10;
				}else if((difference - time_between_steps) < 100 && time_between_steps > 256){
					//temporosnie
					time_between_steps -= 100;
				}

				//Wyświetlenie danych o krokach jak i zmiennych pomocniczych
				TextView txtActivity = (TextView) findViewById(R.id.txtActivity);
				TextView txtCounter = (TextView) findViewById(R.id.txtKrocz);
				TextView txtTime = (TextView) findViewById(R.id.txtTime);
				TextView txtStepTime = (TextView) findViewById(R.id.txtStepTime);

				txtActivity.setText("Aktywność: " + activity);
				txtCounter.setText(Long.toString(numSteps));
				txtTime.setText("Ostatni krok: " + (actualTime - lastTime));
				txtStepTime.setText("Interwał: " + time_between_steps);

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

		activity = 0;
		TextView txtActivity = (TextView) findViewById(R.id.txtActivity);
		txtActivity.setText("Aktywność: 0");
	}
}
