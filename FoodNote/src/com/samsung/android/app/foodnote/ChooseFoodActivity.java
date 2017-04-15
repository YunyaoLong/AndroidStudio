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
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChooseFoodActivity extends Activity {

    private int mMealType;
    private long mIntakeDay;
    private String mSelectedFoodType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the intakes day and meal type from Android Intent
        mIntakeDay = getIntent().getExtras().getLong(AppConstants.BUNDLE_KEY_INTAKE_DAY);
        mMealType = getIntent().getExtras().getInt(AppConstants.BUNDLE_KEY_MEAL_TYPE);

        setTitle("Choose " + AppConstants.getMealTypeName(mMealType) + " Food");
        setContentView(R.layout.choose_food);
        getActionBar().setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));

        // Load the FoodList from FoodInfoTable class
        List<String> foodNameArray = new ArrayList<String>();
        foodNameArray.addAll(FoodInfoTable.keySet());
        Collections.sort(foodNameArray);

        ArrayAdapter<String> nameArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, foodNameArray);
        ListView nameListView = (ListView) findViewById(R.id.chooseFood);
        nameListView.setAdapter(nameArrayAdapter);

        // Show a Dialog to set intakes times, after tap the food from food list
        nameListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSelectedFoodType = ((TextView) view).getText().toString();
                AlertDialog.Builder intakeDialogBuiler = createDialogBuilder();
                intakeDialogBuiler.show();
            }
        });
    }

    private AlertDialog.Builder createDialogBuilder() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        // Set EditText of the dialog
        final EditText input = new EditText(this);
        dialogBuilder.setTitle(R.string.editTextTitle);
        dialogBuilder.setMessage(mSelectedFoodType + " : " + FoodInfoTable.get(mSelectedFoodType).calorie + " kcals/time");
        dialogBuilder.setView(input);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        dialogBuilder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Take the String value (intakes count) from EditText
                String takeCount = input.getText().toString();
                // Check for null or 0 value
                if (!takeCount.isEmpty() && !(".".equals(takeCount)) && (0.f != Float.valueOf(takeCount))) {
                    FoodDataHelper dataHelper = MainActivity.getInstance().getFoodDataHelper();
                    dataHelper.createFoodData(mSelectedFoodType, Float.valueOf(takeCount), mMealType, mIntakeDay);
                }
            }
        });

        return dialogBuilder;
    }

}
