package no.turan.live.android.sensors;

import static no.turan.live.Constants.SAMPLE_POWER_KEY;
import static no.turan.live.Constants.TAG;
import no.turan.live.Constants;

import com.wahoofitness.api.WFHardwareConnectorTypes.WFSensorType;
import com.wahoofitness.api.comm.WFBikePowerConnection;
import com.wahoofitness.api.data.WFBikePowerData;

import android.content.Intent;
import android.util.Log;

public class PowerSensor extends Sensor implements IPowerSensor, ICadenceSensor {
	public PowerSensor() {
		super(WFSensorType.WF_SENSORTYPE_BIKE_POWER);
	}

	@Override
	public int getPower() {
		int power = -1;
		
		if (mSensor != null && mSensor.isConnected()) {
			WFBikePowerData data = (WFBikePowerData) mSensor.getData();
			Log.d(TAG, "PowerSensor.getValue - " + data.timestamp + " - " + data.ulAveragePower + " - " + data.ucInstCadence);
			if (data.timestamp != mPreviousSampleTime) {
				power = (int) data.ulAveragePower;
				mPreviousSampleTime = data.timestamp;
				mDeadSamples = 0;
			} else {
				deadSample();
			}
		} else {
			connectSensor();
		}
		
		return power;
	}

	@Override
	public int getCadence() {
		int cadence = -1;
		
		if (mSensor != null && mSensor.isConnected()) {
			WFBikePowerData data = (WFBikePowerData) mSensor.getData();
			if (mDeadSamples == 0) {
				cadence = data.ucInstCadence;
			}
		}
		
		return cadence;
	}

	@Override
	public void retrieveData(Intent intent) {
		int power = getPower();
		int cadence = getCadence();
		
		if (power >= 0) {
			intent.putExtra(SAMPLE_POWER_KEY, power);
		}
		if (cadence >= 0) {
			intent.putExtra(Constants.SAMPLE_CADENCE_KEY, cadence);
		}
	}

	@Override
	protected void connectSensor() {
		super.connectSensor();
		if (mSensor != null && mSensor.isConnected()) {
			WFBikePowerConnection bpc = (WFBikePowerConnection) mSensor;
			WFBikePowerData bpd = bpc.getBikePowerData();
			Log.d(TAG, "PowerSensor.connectSensor - " + bpd.sensorType);
		}
	}
}