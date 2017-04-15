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

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MealStoreActivity extends Activity implements FoodDataHelper.MealDetailsCallback {

    private FoodDataHelper mDataHelper;
    private TextView mCalories;
    private List<String> mFoodNameArray = new ArrayList<String>();
    private ArrayAdapter<String> mNameArrayAdapter;
    private ListView mCachedFoodListView;
    private ArrayList<String> mIntakeUuidArray = new ArrayList<String>();
    private int mMealType;
    private long mIntakeDay;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the intake day and meal type from Android Intent
        mIntakeDay = getIntent().getExtras().getLong(AppConstants.BUNDLE_KEY_INTAKE_DAY);
        mMealType = getIntent().getExtras().getInt(AppConstants.BUNDLE_KEY_MEAL_TYPE);

        setTitle(AppConstants.getMealTypeName(mMealType));
        setContentView(R.layout.meal_store);
        getActionBar().setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));

        mDataHelper = MainActivity.getInstance().getFoodDataHelper();
        // Get the cached foodintake data
        mDataHelper.readDailyIntakeDetails(MealStoreActivity.this, mIntakeDay, mMealType);

        mNameArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, mFoodNameArray);

        mCachedFoodListView = (ListView) findViewById(R.id.savedFoodList);
        mCachedFoodListView.setAdapter(mNameArrayAdapter);
        mCachedFoodListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.delete:
                        int id = mCachedFoodListView.getCheckedItemPosition();
                        if (id != ListView.INVALID_POSITION) {
                            // Get the foodintake UUID from mFoodNameArray
                            String selectedUuid = mIntakeUuidArray.get(id);
                            // Delete the selected food by the foodintake UUID
                            mDataHelper.deleteFoodIntake(selectedUuid);
                            if (!mFoodNameArray.get(id).trim().contains("(Not Deletable)")) {
                                mFoodNameArray.remove(id);
                            }
                            // Read the food intake data after deletion
                            mDataHelper.readDailyIntakeDetails(MealStoreActivity.this, mIntakeDay, mMealType);
                            // Change the UI, after deletion
                            mCachedFoodListView.clearChoices();
                            mNameArrayAdapter.notifyDataSetChanged();
                        }
                        break;
                }
            }
        });
    }

    @Override
    public void onMealDetailsRetrieved(float calories, List<String> uuidList, List<String> foodNameList) {
        mIntakeUuidArray.clear();
        mIntakeUuidArray.addAll(uuidList);

        mFoodNameArray.clear();
        mFoodNameArray.addAll(foodNameList);

        mCalories = (TextView) findViewById(R.id.editHealthDateValue);
        mCalories.setText(String.valueOf(calories));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == android.view.KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        }
        return super.onKeyDown(keyCode, event);
    }

}
