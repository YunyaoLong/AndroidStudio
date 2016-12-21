package sysu.mobile.mapsensor;

import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SensorActivity extends AppCompatActivity {

    private ArrowView mArrowView;
    private TextView mShakeCounterTextView;
    private TextView mLongitudeTextView;
    private TextView mLatitudeTextView;
    private TextView mRotationTextView;
    private TextView mLocationTextView;

    private int mShakeCounter;
    private SensorUtils mSensorUtils;

    private boolean isProcessingShake = false;

    private SensorUtils.OnSensorUpdateListener mOnSensorUpdateListener = new SensorUtils.OnSensorUpdateListener() {
        @Override
        public void onSensorUpdate(int type, float[] values) {
            switch (type) {
                case SensorUtils.TYPE_ROTATION_CHANGE:
                    mArrowView.onUpdateRotation(values[0]);
                    mRotationTextView.setText(String.format(getString(R.string.rotation_angle), values[0]));
                    break;
                case SensorUtils.TYPE_SHAKE:
                    if (!isProcessingShake) {
                        isProcessingShake = true;
                        // TODO Manage Shake Phone Event
                        Toast.makeText(SensorActivity.this, "SHAKE THE PHONE", Toast.LENGTH_SHORT).show();
                        mShakeCounter++;
                        mShakeCounterTextView.setText(String.format(getString(R.string.shake_counter), mShakeCounter));
                        isProcessingShake = false;
                    }
                    break;
                case SensorUtils.TYPE_LOCATION_CHANGE:
                    updateLocation(mSensorUtils.getCurrentLocation());
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        mArrowView = (ArrowView) findViewById(R.id.arrow);
        mShakeCounterTextView = (TextView) findViewById(R.id.tv_shake_counter);
        mLatitudeTextView = (TextView) findViewById(R.id.tv_latitude);
        mLongitudeTextView = (TextView) findViewById(R.id.tv_longitude);
        mRotationTextView = (TextView) findViewById(R.id.tv_rotation);
        mLocationTextView = (TextView) findViewById(R.id.tv_location);

        mShakeCounter = 0;
        mShakeCounterTextView.setText(String.format(getString(R.string.shake_counter), mShakeCounter));

        mSensorUtils = SensorUtils.getInstance();
        mSensorUtils.setOnSensorUpdateListener(mOnSensorUpdateListener);

        updateLocation(mSensorUtils.getCurrentLocation());
    }

    @Override
    protected void onResume() {
        super.onResume();

        mSensorUtils.registerSensor();
    }

    @Override
    protected void onPause() {
        mSensorUtils.unregisterSensor();

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void updateLocation(Location location) {
        if (location == null) {
            mLongitudeTextView.setText(String.format(getString(R.string.longitude), 0f));
            mLatitudeTextView.setText(String.format(getString(R.string.latitude), 0f));
        } else {
            mLongitudeTextView.setText(String.format(getString(R.string.longitude), location.getLongitude()));
            mLatitudeTextView.setText(String.format(getString(R.string.latitude), location.getLatitude()));

            new GeoDecoder().execute(location);
        }
    }


    private class GeoDecoder extends AsyncTask<Location, Integer, String> {

        @Override
        protected String doInBackground(Location... params) {
            Location param = params[0];
            try {
                String result = sendHttpRequest(String.format(getString(R.string.coor_change),
                        param.getLongitude(), param.getLatitude(), getString(R.string.server_ak)));
                JSONObject jObject = new JSONObject(result);
                JSONArray jsonArray = jObject.getJSONArray("result");
                JSONObject locObject = jsonArray.getJSONObject(0);
                double x = locObject.getDouble("x");
                double y = locObject.getDouble("y");

                String decode = sendHttpRequest(String.format(getString(R.string.geo_decode),
                        y, x, getString(R.string.server_ak)));
                JSONObject geoObject = new JSONObject(decode);
                return geoObject.getString("formatted_address");
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return "Error";
        }

        @Override
        protected void onPostExecute(String s) {
            mLocationTextView.setText(s);
        }

        private String sendHttpRequest(String request) throws IOException {
            URL url = new URL(request);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            InputStream is = connection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String response = "";
            String readLine;
            while ((readLine = br.readLine()) != null) {
                response = response + readLine;
            }
            is.close();
            br.close();
            connection.disconnect();
            return response;
        }
    }
}
