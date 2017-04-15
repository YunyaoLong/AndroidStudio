/**
 * Copyright (C) 2014 Samsung Electronics Co., Ltd. All rights reserved.
 *
 * Mobile Communication Division,
 * Digital Media & Communications Business, Samsung Electronics Co., Ltd.
 *
 * This software and its documentation are confidential and proprietary
 * information of Samsung Electronics Co., Ltd.  No part of the software and
 * documents may be copied, reproduced, transmitted, translated, or reduced to
 * any electronic medium or machine-readable form without the prior written
 * consent of Samsung Electronics.
 *
 * Samsung Electronics makes no representations with respect to the contents,
 * and assumes no responsibility for any errors that might appear in the
 * software and documents. This publication and the contents hereof are subject
 * to change without notice.
 */

package com.samsung.android.app.foodnote;

import com.samsung.android.sdk.healthdata.HealthConnectionErrorResult;
import com.samsung.android.sdk.healthdata.HealthConstants.FoodInfo;
import com.samsung.android.sdk.healthdata.HealthConstants.FoodIntake;
import com.samsung.android.sdk.healthdata.HealthDataObserver;
import com.samsung.android.sdk.healthdata.HealthDataService;
import com.samsung.android.sdk.healthdata.HealthDataStore;
import com.samsung.android.sdk.healthdata.HealthPermissionManager;
import com.samsung.android.sdk.healthdata.HealthPermissionManager.PermissionKey;
import com.samsung.android.sdk.healthdata.HealthPermissionManager.PermissionResult;
import com.samsung.android.sdk.healthdata.HealthPermissionManager.PermissionType;
import com.samsung.android.sdk.healthdata.HealthResultHolder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class MainActivity extends Activity implements FoodDataHelper.DailyCaloriesCallback {

    public static final String TAG = "FoodNote";

    private static final SimpleDateFormat INDEXTIME_FORMAT = new SimpleDateFormat("yyyy/MM/dd (E)", Locale.US);
    private static final int MENU_ITEM_PERMISSION_SETTING = 1;
    private static MainActivity mInstance = null;

    private HealthDataStore mStore;
    private HealthConnectionErrorResult mConnError;
    private FoodDataHelper mDataHelper;
    private long mDayStartTime;
    private TextView mDayTv;
    Set<PermissionKey> mPermissionkeySet = new HashSet<PermissionKey>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mInstance = this;
        setContentView(R.layout.activity_main);
        getActionBar().setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));

        // Get the current time and show it
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        mDayStartTime = calendar.getTimeInMillis();

        mDayTv = (TextView) findViewById(R.id.simpledateformat);
        mDayTv.setText(INDEXTIME_FORMAT.format(mDayStartTime));

        // Move the day before on button clicked
        ImageButton beforeButton = (ImageButton) findViewById(R.id.moveBefore);
        beforeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(mDayStartTime);
                calendar.add(Calendar.DAY_OF_YEAR, -1);
                mDayStartTime = calendar.getTimeInMillis();
                mDayTv.setText(INDEXTIME_FORMAT.format(mDayStartTime));
                mDataHelper.readDailyIntakeCalories(MainActivity.this, mDayStartTime);
            }
        });

        // Move the day after on button clicked
        ImageButton nextButton = (ImageButton) findViewById(R.id.moveNext);
        nextButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(mDayStartTime);
                calendar.add(Calendar.DAY_OF_YEAR, 1);
                mDayStartTime = calendar.getTimeInMillis();
                mDayTv.setText(INDEXTIME_FORMAT.format(mDayStartTime));
                mDataHelper.readDailyIntakeCalories(MainActivity.this, mDayStartTime);
            }
        });

        // Make for Food Note UI to access ChooseFoodActivity and MealStoreActivity
        ImageButton addBreakfastBtn = (ImageButton) findViewById(R.id.add_icon_breakfast);
        addBreakfastBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                generateIntent(ChooseFoodActivity.class, FoodIntake.MEAL_TYPE_BREAKFAST);
            }
        });

        LinearLayout breakfastLayout = (LinearLayout) findViewById(R.id.breakfastLayout);
        breakfastLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                generateIntent(MealStoreActivity.class, FoodIntake.MEAL_TYPE_BREAKFAST);
            }
        });

        ImageButton addLunchBtn = (ImageButton) findViewById(R.id.add_icon_lunch);
        addLunchBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                generateIntent(ChooseFoodActivity.class, FoodIntake.MEAL_TYPE_LUNCH);
            }
        });

        LinearLayout lunchLayout = (LinearLayout) findViewById(R.id.lunchLayout);
        lunchLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                generateIntent(MealStoreActivity.class, FoodIntake.MEAL_TYPE_LUNCH);
            }
        });

        ImageButton addDinnerBtn = (ImageButton) findViewById(R.id.add_icon_dinner);
        addDinnerBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                generateIntent(ChooseFoodActivity.class, FoodIntake.MEAL_TYPE_DINNER);
            }
        });

        LinearLayout dinnerLayout = (LinearLayout) findViewById(R.id.dinnerLayout);
        dinnerLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                generateIntent(MealStoreActivity.class, FoodIntake.MEAL_TYPE_DINNER);
            }
        });

        ImageButton addMorningSnackBtn = (ImageButton) findViewById(R.id.add_icon_morning_snack);
        addMorningSnackBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                generateIntent(ChooseFoodActivity.class, FoodIntake.MEAL_TYPE_MORNING_SNACK);
            }
        });

        LinearLayout morningSnackLayout = (LinearLayout) findViewById(R.id.morningSnackLayout);
        morningSnackLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                generateIntent(MealStoreActivity.class, FoodIntake.MEAL_TYPE_MORNING_SNACK);
            }
        });

        ImageButton addAfternoonSnackBtn = (ImageButton) findViewById(R.id.add_icon_afternoon_snack);
        addAfternoonSnackBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                generateIntent(ChooseFoodActivity.class, FoodIntake.MEAL_TYPE_AFTERNOON_SNACK);
            }
        });

        LinearLayout afternoonSnackLayout = (LinearLayout) findViewById(R.id.afternoonSnackLayout);
        afternoonSnackLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                generateIntent(MealStoreActivity.class, FoodIntake.MEAL_TYPE_AFTERNOON_SNACK);
            }
        });

        ImageButton addEveningSnackBtn = (ImageButton) findViewById(R.id.add_icon_evening_snack);
        addEveningSnackBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                generateIntent(ChooseFoodActivity.class, FoodIntake.MEAL_TYPE_EVENING_SNACK);
            }
        });

        LinearLayout eveningSnackLayout = (LinearLayout) findViewById(R.id.eveningSnackLayout);
        eveningSnackLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                generateIntent(MealStoreActivity.class, FoodIntake.MEAL_TYPE_EVENING_SNACK);
            }
        });

        HealthDataService healthDataService = new HealthDataService();
        try {
            healthDataService.initialize(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Create a HealthDataStore instance and set its listener
        mStore = new HealthDataStore(this, mConnectionListener);
        mDataHelper = new FoodDataHelper(mStore, this);

        // Add the read and write permissions to Permission KeySet
        mPermissionkeySet.add(new PermissionKey(FoodIntake.HEALTH_DATA_TYPE, PermissionType.READ));
        mPermissionkeySet.add(new PermissionKey(FoodIntake.HEALTH_DATA_TYPE, PermissionType.WRITE));
        mPermissionkeySet.add(new PermissionKey(FoodInfo.HEALTH_DATA_TYPE, PermissionType.READ));
        mPermissionkeySet.add(new PermissionKey(FoodInfo.HEALTH_DATA_TYPE, PermissionType.WRITE));

        // Request the connection to the health data store
        mStore.connectService();
    }

    @Override
    public void onDestroy() {
        HealthDataObserver.removeObserver(mStore, mObserver);
        mStore.disconnectService();
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            mDataHelper.readDailyIntakeCalories(MainActivity.this, mDayStartTime);
        } catch (Exception e) {
            Log.e(TAG, e.getClass().getName() + " - " + e.getMessage());
        }
    }

    public static MainActivity getInstance() {
        return mInstance;
    }

    public FoodDataHelper getFoodDataHelper() {
        return mDataHelper;
    }

    private final HealthDataStore.ConnectionListener mConnectionListener = new HealthDataStore.ConnectionListener() {
        @Override
        public void onConnected() {
            Log.d(TAG, "onConnected");
            HealthPermissionManager mPmsManager = new HealthPermissionManager(mStore);
            Map<PermissionKey, Boolean> mPermissionMap = mPmsManager.isPermissionAcquired(mPermissionkeySet);
            // Check the permissions acquired or not
            if (mPermissionMap.containsValue(Boolean.FALSE)) {
                requestPermissions();
            }
            mDataHelper.readDailyIntakeCalories(MainActivity.this, mDayStartTime);
        }

        @Override
        public void onConnectionFailed(HealthConnectionErrorResult error) {
            Log.d(TAG, "onConnectionFailed");
            showConnectionFailureDialog(error);
        }

        @Override
        public void onDisconnected() {
            Log.d(TAG, "onDisconnected");
        }
    };

    private final HealthDataObserver mObserver = new HealthDataObserver(null) {
        @Override
        public void onChange(String dataTypeName) {
            Log.d(TAG, "onChange");
            if (FoodIntake.HEALTH_DATA_TYPE.equals(dataTypeName)) {
                mDataHelper.readDailyIntakeCalories(MainActivity.this, mDayStartTime);
            }
        }
    };

    private void requestPermissions() {
        HealthPermissionManager pmsManager = new HealthPermissionManager(mStore);
        try {
            // Show user permission UI for allowing user to change options
            pmsManager.requestPermissions(mPermissionkeySet, MainActivity.this).setResultListener(mPermissionListener);
        } catch (Exception e) {
            Log.e(TAG, e.getClass().getName() + " - " + e.getMessage());
            Log.e(TAG, "Permission setting fails.");
        }
    }

    private final HealthResultHolder.ResultListener<PermissionResult> mPermissionListener = new HealthResultHolder.ResultListener<PermissionResult>() {

        @Override
        public void onResult(PermissionResult result) {
            Map<PermissionKey, Boolean> resultMap = result.getResultMap();
            // Show a permission alarm and initializes the calories if permissions are not acquired
            if (resultMap.values().contains(Boolean.FALSE)) {
                showPermissionAlarmDialog();
            } else {
                // Get the calories of Indexed time and display it
                mDataHelper.readDailyIntakeCalories(MainActivity.this, mDayStartTime);
                // Register an observer to listen changes of the calories
                HealthDataObserver.addObserver(mStore, FoodIntake.HEALTH_DATA_TYPE, mObserver);
            }
        }
    };

    private void showPermissionAlarmDialog() {
        if (isFinishing()) {
            return;
        }

        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setTitle("Notice");
        alert.setMessage("All permissions should be acquired");
        alert.setPositiveButton("OK", null);
        alert.show();
    }

    private void showConnectionFailureDialog(HealthConnectionErrorResult error) {

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        mConnError = error;
        String message = "Connection with S Health is not available";

        if (mConnError.hasResolution()) {
            switch(error.getErrorCode()) {
            case HealthConnectionErrorResult.PLATFORM_NOT_INSTALLED:
                message = "Please install S Health";
                break;
            case HealthConnectionErrorResult.OLD_VERSION_PLATFORM:
                message = "Please upgrade S Health";
                break;
            case HealthConnectionErrorResult.PLATFORM_DISABLED:
                message = "Please enable S Health";
                break;
            case HealthConnectionErrorResult.USER_AGREEMENT_NEEDED:
                message = "Please agree with S Health policy";
                break;
            default:
                message = "Please make S Health available";
                break;
            }
        }

        alert.setMessage(message);

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (mConnError.hasResolution()) {
                    mConnError.resolve(mInstance);
                }
            }
        });

        if (error.hasResolution()) {
            alert.setNegativeButton("Cancel", null);
        }

        alert.show();
    }

    private void showTotalCalories(String breakfast, String lunch, String dinner, String morningSnack,
                                 String afternoonSnack, String eveningSnack, String total) {

        TextView breakfastCaloriesTv = (TextView) findViewById(R.id.editHealthDateValue2);
        breakfastCaloriesTv.setText(breakfast);

        TextView lunchCaloriesTv = (TextView) findViewById(R.id.editHealthDateValue3);
        lunchCaloriesTv.setText(lunch);

        TextView dinnerCaloriesTv = (TextView) findViewById(R.id.editHealthDateValue4);
        dinnerCaloriesTv.setText(dinner);

        TextView morningSnackCaloriesTv = (TextView) findViewById(R.id.editHealthDateValue5);
        morningSnackCaloriesTv.setText(morningSnack);

        TextView afternoonSnackCaloriesTv = (TextView) findViewById(R.id.editHealthDateValue6);
        afternoonSnackCaloriesTv.setText(afternoonSnack);

        TextView eveningSnackCaloriesTv = (TextView) findViewById(R.id.editHealthDateValue7);
        eveningSnackCaloriesTv.setText(eveningSnack);

        TextView totalCaloriesTv = (TextView) findViewById(R.id.editHealthDateValue1);
        totalCaloriesTv.setText(total);
    }

    @Override
    public void onDailyCaloriesRetrieved(String breakfast, String lunch, String dinner, String morningSnack,
                                         String afternoonSnack, String eveningSnack, String total) {
        showTotalCalories(breakfast, lunch, dinner, morningSnack, afternoonSnack, eveningSnack, total);
    }

    // Method for generate Intent for meal type and activities
    private void generateIntent(Class<?> mealType, int mealTypeInteger) {
        Intent intent = new Intent(getBaseContext(), mealType);
        Bundle b = new Bundle();
        b.putInt(AppConstants.BUNDLE_KEY_MEAL_TYPE, mealTypeInteger);
        b.putLong(AppConstants.BUNDLE_KEY_INTAKE_DAY, mDayStartTime);
        intent.putExtras(b);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(1, MENU_ITEM_PERMISSION_SETTING, 0, "Connect to S Health");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {

        if (item.getItemId() == (MENU_ITEM_PERMISSION_SETTING)) {
            requestPermissions();
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

}
