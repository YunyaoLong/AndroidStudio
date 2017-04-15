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

import com.samsung.android.sdk.healthdata.HealthConstants;
import com.samsung.android.sdk.healthdata.HealthConstants.FoodInfo;
import com.samsung.android.sdk.healthdata.HealthConstants.FoodIntake;
import com.samsung.android.sdk.healthdata.HealthData;
import com.samsung.android.sdk.healthdata.HealthDataResolver;
import com.samsung.android.sdk.healthdata.HealthDataResolver.DeleteRequest;
import com.samsung.android.sdk.healthdata.HealthDataResolver.Filter;
import com.samsung.android.sdk.healthdata.HealthDataResolver.InsertRequest;
import com.samsung.android.sdk.healthdata.HealthDataResolver.ReadRequest;
import com.samsung.android.sdk.healthdata.HealthDataResolver.ReadResult;
import com.samsung.android.sdk.healthdata.HealthDataStore;
import com.samsung.android.sdk.healthdata.HealthDeviceManager;
import com.samsung.android.sdk.healthdata.HealthPermissionManager.PermissionKey;
import com.samsung.android.sdk.healthdata.HealthResultHolder;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

public class FoodDataHelper {

    private static final long DAY_END_TIME = 999 + 59 * 1000 + 59 * 60 * 1000 + 23 * 60 * 60 * 1000;

    private HealthDataStore mStore;
    private DailyCaloriesCallback mDailyCalorieCallback;
    private MealDetailsCallback mMealDetailsCallback;
    private String mSavedUuid;
    private Context mContext;
    Set<PermissionKey> mPermissionkeySet = new HashSet<PermissionKey>();
    Map<PermissionKey, Boolean> mPermissionMap;

    public FoodDataHelper(HealthDataStore store, Context context) {
        mStore = store;
        mContext = context;
    }

    private void insertFoodInfo(String foodName) {

        HealthData data = new HealthData();
        // Get the FoodInfoTable's key from selected food name to use nutrition informations
        FoodInfoTable.FoodInfo foodInfo = FoodInfoTable.get(foodName);

        // Fill out the mandatory properties to insert data
        data.putString(FoodInfo.PROVIDER_FOOD_ID, foodInfo.providerFoodId);
        data.putString(FoodInfo.INFO_PROVIDER, foodInfo.infoProvider);
        data.putString(FoodInfo.NAME, foodInfo.name);
        data.putFloat(FoodInfo.CALORIE, foodInfo.calorie);
        data.putString(FoodInfo.DESCRIPTION, foodInfo.description);
        data.putInt(FoodInfo.METRIC_SERVING_AMOUNT, foodInfo.metricServingAmount);
        data.putString(FoodInfo.METRIC_SERVING_UNIT, foodInfo.metricServingUnit);
        data.putInt(FoodInfo.DEFAULT_NUMBER_OF_SERVING_UNIT, foodInfo.defaultNumberOfServingUnit);
        data.putFloat(FoodInfo.TOTAL_FAT, foodInfo.totalFat);
        data.putFloat(FoodInfo.SATURATED_FAT, foodInfo.saturatedFat);
        data.putFloat(FoodInfo.POLYSATURATED_FAT, foodInfo.polysaturatedFat);
        data.putFloat(FoodInfo.MONOSATURATED_FAT, foodInfo.monosaturatedFat);
        data.putFloat(FoodInfo.TRANS_FAT, foodInfo.transFat);
        data.putFloat(FoodInfo.CARBOHYDRATE, foodInfo.carbohydrate);
        data.putFloat(FoodInfo.DIETARY_FIBER, foodInfo.dietaryFiber);
        data.putFloat(FoodInfo.SUGAR, foodInfo.sugar);
        data.putFloat(FoodInfo.PROTEIN, foodInfo.protein);
        data.putFloat(FoodInfo.UNIT_COUNT_PER_CALORIE, foodInfo.unitCountPerCalorie);
        data.putFloat(FoodInfo.CHOLESTEROL, foodInfo.cholesterol);
        data.putFloat(FoodInfo.SODIUM, foodInfo.soduim);
        data.putFloat(FoodInfo.POTASSIUM, foodInfo.potassium);
        data.putFloat(FoodInfo.VITAMIN_A, foodInfo.vitaminA);
        data.putFloat(FoodInfo.VITAMIN_C, foodInfo.vitaminC);
        data.putFloat(FoodInfo.CALCIUM, foodInfo.calcium);
        data.putFloat(FoodInfo.IRON, foodInfo.iron);

        // Register the local device with the data if it is not registered
        data.setSourceDevice(new HealthDeviceManager(mStore).getLocalDevice().getUuid());

        HealthDataResolver resolver = new HealthDataResolver(mStore, null);
        InsertRequest request = new InsertRequest.Builder().setDataType(FoodInfo.HEALTH_DATA_TYPE).build();
        request.addHealthData(data);
        mSavedUuid = data.getUuid();

        resolver.insert(request);
    }

    private void insertFoodIntake(String foodName, float intakeCount, int mealType, long intakeTime, String Uuid) {

        HealthData data = new HealthData();
        // Get the FoodInfoTable's key from selected food name to use nutrition informations
        FoodInfoTable.FoodInfo foodInfo = FoodInfoTable.get(foodName);

        // Fill out the mandatory properties to insert data
        data.putFloat(FoodIntake.CALORIE, foodInfo.calorie * intakeCount);
        data.putString(FoodIntake.NAME, foodName);
        data.putFloat(FoodIntake.AMOUNT, intakeCount);
        data.putString(FoodIntake.UNIT, foodInfo.metricServingUnit);
        data.putInt(FoodIntake.MEAL_TYPE, mealType);
        data.putString(FoodIntake.FOOD_INFO_ID, Uuid);
        switch (mealType) {
            case HealthConstants.FoodIntake.MEAL_TYPE_BREAKFAST:
                intakeTime += AppConstants.BREAKFAST_TIME;
                break;
            case HealthConstants.FoodIntake.MEAL_TYPE_LUNCH:
                intakeTime += AppConstants.LUNCH_TIME;
                break;
            case HealthConstants.FoodIntake.MEAL_TYPE_DINNER:
                intakeTime += AppConstants.DINNER_TIME;
                break;
            case HealthConstants.FoodIntake.MEAL_TYPE_MORNING_SNACK:
                intakeTime += AppConstants.MORNING_SNACK_TIME;
                break;
            case HealthConstants.FoodIntake.MEAL_TYPE_AFTERNOON_SNACK:
                intakeTime += AppConstants.AFTERNOON_SNACK_TIME;
                break;
            case HealthConstants.FoodIntake.MEAL_TYPE_EVENING_SNACK:
                intakeTime += AppConstants.EVENING_SNACK_TIME;
                break;
        }

        data.putLong(FoodIntake.START_TIME, intakeTime);
        data.putLong(FoodIntake.TIME_OFFSET, TimeZone.getDefault().getOffset(intakeTime));

        // Register the local device with the data if it is not registered
        data.setSourceDevice(new HealthDeviceManager(mStore).getLocalDevice().getUuid());

        HealthDataResolver resolver = new HealthDataResolver(mStore, null);
        InsertRequest request = new InsertRequest.Builder().setDataType(FoodIntake.HEALTH_DATA_TYPE).build();
        request.addHealthData(data);

        resolver.insert(request);
    }

    public void deleteFoodIntake(String intakeUuid) {

        HealthDataResolver resolver = new HealthDataResolver(mStore, null);
        Filter filter = Filter.eq(FoodIntake.UUID, intakeUuid);
        HealthDataResolver.DeleteRequest deleteRequest = new DeleteRequest.Builder().setDataType(FoodIntake.HEALTH_DATA_TYPE)
                .setFilter(filter).build();
        try {
            resolver.delete(deleteRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void readDailyIntakeCalories(DailyCaloriesCallback callback, long startTime) {

        mDailyCalorieCallback = callback;
        HealthDataResolver resolver = new HealthDataResolver(mStore, null);

        HealthDataResolver.Filter filter = Filter.and(Filter.greaterThanEquals(FoodIntake.START_TIME, startTime),
                Filter.lessThanEquals(FoodIntake.START_TIME, startTime + DAY_END_TIME));

        // Read the foodIntake data of specified day(startTime to end)
        HealthDataResolver.ReadRequest request = new ReadRequest.Builder().setDataType(FoodIntake.HEALTH_DATA_TYPE)
                .setProperties(new String[] {
                        FoodIntake.CALORIE, FoodIntake.MEAL_TYPE
                }).setFilter(filter).build();
        try {
            resolver.read(request).setResultListener(mIntakeCaloriesListener);
        } catch (Exception e) {
            Log.e(MainActivity.TAG, e.getClass().getName() + " - " + e.getMessage());
            Log.e(MainActivity.TAG, "Getting daily calories fails.");
            mDailyCalorieCallback.onDailyCaloriesRetrieved("", "", "", "", "", "", "");
        }
    }

    private final HealthResultHolder.ResultListener<ReadResult> mIntakeCaloriesListener =
            new HealthResultHolder.ResultListener<ReadResult>() {
        @Override
        public void onResult(ReadResult result) {
            Cursor c = null;
            try {
                c = result.getResultCursor();
                if (c == null) {
                    Log.e(MainActivity.TAG, "null cursor!");
                    return;
                }

                float breakfast = 0.f;
                float lunch = 0.f;
                float dinner = 0.f;
                float morningSnack = 0.f;
                float afternoonSnack = 0.f;
                float eveningSnack = 0.f;
                float total = 0.f;

                // Read the food intake calories of each meal type
                while (c.moveToNext()) {
                    String calorie = c.getString(c.getColumnIndex(FoodIntake.CALORIE));
                    int mealType = c.getInt(c.getColumnIndex(FoodIntake.MEAL_TYPE));

                    switch (mealType) {
                        case FoodIntake.MEAL_TYPE_BREAKFAST:
                            breakfast += Float.valueOf(calorie);
                            break;
                        case FoodIntake.MEAL_TYPE_LUNCH:
                            lunch += Float.valueOf(calorie);
                            break;
                        case FoodIntake.MEAL_TYPE_DINNER:
                            dinner += Float.valueOf(calorie);
                            break;
                        case FoodIntake.MEAL_TYPE_MORNING_SNACK:
                            morningSnack += Float.valueOf(calorie);
                            break;
                        case FoodIntake.MEAL_TYPE_AFTERNOON_SNACK:
                            afternoonSnack += Float.valueOf(calorie);
                            break;
                        case FoodIntake.MEAL_TYPE_EVENING_SNACK:
                            eveningSnack += Float.valueOf(calorie);
                            break;
                        default:
                            break;
                    }
                }

                // Show the food intake calories
                total = breakfast + lunch + dinner + morningSnack + afternoonSnack + eveningSnack;
                mDailyCalorieCallback.onDailyCaloriesRetrieved(String.valueOf(breakfast), String.valueOf(lunch),
                        String.valueOf(dinner), String.valueOf(morningSnack), String.valueOf(afternoonSnack),
                        String.valueOf(eveningSnack), String.valueOf(total));

            } finally {
                if (c != null) {
                    c.close();
                }
            }
        }
    };

    public void readDailyIntakeDetails(MealDetailsCallback callback, long startTime, int mealType) {

        mMealDetailsCallback = callback;
        HealthDataResolver resolver = new HealthDataResolver(mStore, null);

        HealthDataResolver.Filter filter = Filter
                .and(Filter.greaterThanEquals(FoodIntake.START_TIME, startTime),
                        Filter.lessThanEquals(FoodIntake.START_TIME, startTime + DAY_END_TIME),
                        Filter.eq(FoodIntake.MEAL_TYPE, mealType));
        // Read the foodIntake data of specified day and meal type(startTime to end)
        HealthDataResolver.ReadRequest request = new ReadRequest.Builder().setDataType(FoodIntake.HEALTH_DATA_TYPE)
                .setProperties(new String[] {
                        FoodIntake.UUID, FoodIntake.NAME, FoodIntake.CALORIE, FoodIntake.AMOUNT, FoodIntake.PACKAGE_NAME
                }).setFilter(filter).build();
        try {
            resolver.read(request).setResultListener(mIntakeDetailListener);
        } catch (Exception e) {
            Log.e(MainActivity.TAG, "read error!");
            Log.e(MainActivity.TAG, e.getClass().getName() + " - " + e.getMessage());
        }

    }

    private final HealthResultHolder.ResultListener<ReadResult> mIntakeDetailListener =
            new HealthResultHolder.ResultListener<ReadResult>() {
        @Override
        public void onResult(ReadResult result) {
            Cursor c = null;
            float intakeTimes;
            float intakeCalories;
            String foodName;
            float totalCalories = 0.f;
            String packageName;

            try {
                c = result.getResultCursor();
                if (c == null) {
                    Log.e(MainActivity.TAG, "null cursor!");
                    return;
                }

                ArrayList<String> savedUuidList = new ArrayList<String>();
                List<String> foodNameList = new ArrayList<String>();

                while (c.moveToNext()) {
                    savedUuidList.add(c.getString(c.getColumnIndex(FoodIntake.UUID)));
                    // Set the variables to get the food intake details
                    foodName = c.getString(c.getColumnIndex(FoodIntake.NAME));
                    intakeTimes = c.getFloat(c.getColumnIndex(FoodIntake.AMOUNT));
                    intakeCalories = c.getFloat(c.getColumnIndex(FoodIntake.CALORIE));
                    packageName = c.getString(c.getColumnIndex(FoodIntake.PACKAGE_NAME));
                    if (packageName.equals("com.samsung.android.app.foodnote")) {
                        foodNameList.add(foodName + " : " + intakeTimes + " times" + " (" + intakeCalories + " Cals)");
                    } else {
                        foodNameList.add(foodName + " : " + intakeTimes + " times" + " (" + intakeCalories + " Cals)"
                                + " (Not Deletable)");
                    }

                    // Calculate the total calories from food intakes data
                    totalCalories += intakeCalories;
                }
                mMealDetailsCallback.onMealDetailsRetrieved(totalCalories, savedUuidList, foodNameList);

            } finally {
                if (c != null) {
                    c.close();
                }
            }
        }
    };

    public void createFoodData(final String foodName, final float intakeCount, final int mealType, final long intakeTime) {

        HealthDataResolver resolver = new HealthDataResolver(mStore, null);

        HealthDataResolver.Filter filter = Filter.eq(FoodIntake.NAME, mealType);
        HealthDataResolver.ReadRequest request = new ReadRequest.Builder().setDataType(FoodInfo.HEALTH_DATA_TYPE)
                .setProperties(new String[] {
                        FoodInfo.NAME, FoodInfo.UUID
                }).setFilter(filter).build();
        try {
            resolver.read(request).setResultListener(new HealthResultHolder.ResultListener<ReadResult>() {
                @Override
                public void onResult(ReadResult result) {
                    Cursor c = null;
                    try {
                        mSavedUuid = "";
                        c = result.getResultCursor();
                        if (c != null && c.moveToFirst()) {
                            if (foodName.equals(c.getString(c.getColumnIndex(FoodInfo.NAME)))) {
                                mSavedUuid = c.getString(c.getColumnIndex(FoodInfo.UUID));
                            }
                        }
                        if (mSavedUuid.equals("")) {
                            insertFoodInfo(foodName);
                            insertFoodIntake(foodName, intakeCount, mealType, intakeTime, mSavedUuid);
                        } else {
                            insertFoodIntake(foodName, intakeCount, mealType, intakeTime, mSavedUuid);
                        }
                        Intent intent = new Intent(mContext.getApplicationContext(), MealStoreActivity.class);
                        Bundle b = new Bundle();
                        b.putInt(AppConstants.BUNDLE_KEY_MEAL_TYPE, mealType);
                        b.putLong(AppConstants.BUNDLE_KEY_INTAKE_DAY, intakeTime);
                        intent.putExtras(b);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);
                    } catch (Exception e) {
                        Toast.makeText(mContext.getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    } finally {
                        if (c != null) {
                            c.close();
                        }
                    }
                }
            });
        } catch (Exception e) {
            Log.e(MainActivity.TAG, e.getClass().getName() + " - " + e.getMessage());
            Log.e(MainActivity.TAG, "Permission should be requried.");
            Toast.makeText(mContext.getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public interface DailyCaloriesCallback {
        public void onDailyCaloriesRetrieved(String breakfast, String lunch, String dinner, String morningSnack,
                                             String afternoonSnack, String eveningSnack, String total);
    }

    public interface MealDetailsCallback {
        public void onMealDetailsRetrieved(float calories, List<String> uuidList, List<String> foodNameList);
    }

}
