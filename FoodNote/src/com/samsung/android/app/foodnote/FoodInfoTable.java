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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FoodInfoTable {

    private static final Map<String, FoodInfo> mFoodInfoTable = Collections.unmodifiableMap(new HashMap<String, FoodInfo>() {
        private static final long serialVersionUID = 1L;

        {
            put("Croissant", new FoodInfo("FoodNote_Croissant", "user_define", "Croissant", 231f, "Croissant", 57, "g",
                    "Croissant", 1, 11.97f, 6.646f, 0.624f, 3.149f, 0f, 26.11f, 1.5f, 6.42f, 4.67f, 231f, 38f, 424f, 67f, 0f, 0f,
                    2f, 6f));
            put("Milk", new FoodInfo("FoodNote_Milk", "user_define", "Milk", 60f, "Milk", 100, "g", "Milk", 1, 3.25f, 1.865f,
                    0.195f, 0.812f, 0f, 4.52f, 0f, 5.26f, 3.22f, 60f, 10f, 40f, 143f, 0f, 0f, 11f, 0f));
            put("Beef Steak", new FoodInfo("FoodNote_BeefSteak", "user_define", "Beef Steak", 252f, "Beef Steak", 100, "g",
                    "Beef Steak", 1, 15.01f, 5.877f, 0.559f, 6.28f, 0f, 0f, 0f, 0f, 27.29f, 252f, 82f, 373f, 305f, 0f, 0f, 2f,
                    11f));
            put("Apple", new FoodInfo("FoodNote_Apple", "user_define", "Apple", 72f, "Apple", 138, "g", "Apple", 1, 0.23f,
                    0.039f, 0.07f, 0.01f, 0f, 19.06f, 3.3f, 14.34f, 0.36f, 72f, 0f, 1f, 148f, 2f, 10f, 1f, 1f));
            put("Cheese Pizza", new FoodInfo("FoodNote_CheesePizza", "user_define", "Cheese Pizza", 237f, "Cheese Pizza", 86,
                    "g", "Cheese Pizza", 1, 10.1f, 4.304f, 1.776f, 2.823f, 0f, 26.08f, 1.6f, 3.06f, 10.6f, 237f, 21f, 462f, 138f,
                    0f, 0f, 18f, 9f));
            put("Orange Juice", new FoodInfo("FoodNote_OrangeJuice", "user_define", "Orange Juice", 47f, "Orange Juice", 100,
                    "ml", "Orange Juice", 1, 0.21f, 0.025f, 0.042f, 0.038f, 0f, 10.9f, 0.2f, 8.81f, 0.73f, 47f, 0f, 1f, 210f, 4f,
                    87f, 1f, 1f));
            put("Spaghetti", new FoodInfo("FoodNote_Spaghetti", "user_define", "Spaghetti", 220f, "Spaghetti", 140, "g",
                    "Spaghetti", 1, 1.29f, 0.245f, 0.444f, 0.182f, 0f, 42.95f, 2.5f, 0.78f, 8.06f, 220f, 0f, 325f, 63f, 0f, 0f,
                    1f, 10f));
            put("Soda", new FoodInfo("FoodNote_Soda", "user_define", "Soda", 140f, "Soda", 12, "oz", "Soda", 1, 0.04f, 0f, 0f,
                    0f, 0f, 36.05f, 0f, 33.76f, 0.26f, 12f, 0f, 22f, 4f, 0f, 0f, 0f, 0f));
            put("Potato Chips", new FoodInfo("FoodNote_PotatoChips", "user_define", "Potato Chips", 153f, "Potato Chips", 28,
                    "g", "Potato Chips", 1, 10.49f, 3.069f, 3.408f, 2.755f, 0f, 13.93f, 1.2f, 1.15f, 1.84f, 153f, 0f, 147f, 460f,
                    0f, 9f, 1f, 2f));
            put("Hamburger", new FoodInfo("FoodNote_Hamburger", "user_define", "Hamburger", 257f, "Hamburger", 100, "g",
                    "Hamburger", 1, 9.22f, 3.361f, 0.948f, 3.212f, 0f, 32.31f, 2.2f, 6.32f, 11.62f, 257f, 28f, 504f, 237f, 1f,
                    4f, 12f, 14f));
        }
    });

    public static FoodInfo get(String foodName) {
        return mFoodInfoTable.get(foodName);
    }

    public static Set<String> keySet() {
        return mFoodInfoTable.keySet();
    }

    public static class FoodInfo {
        public String providerFoodId;
        public String infoProvider;
        public String name;
        public float calorie;
        public String description;
        public int metricServingAmount;
        public String metricServingUnit;
        public String servingDescription;
        public int defaultNumberOfServingUnit;
        public float totalFat;
        public float saturatedFat;
        public float polysaturatedFat;
        public float monosaturatedFat;
        public float transFat;
        public float carbohydrate;
        public float dietaryFiber;
        public float sugar;
        public float protein;
        public float unitCountPerCalorie;
        public float cholesterol;
        public float soduim;
        public float potassium;
        public float vitaminA;
        public float vitaminC;
        public float calcium;
        public float iron;

        private FoodInfo(String providerFoodId, String infoProvider, String name, float calorie, String description,
                int metricServingAmount, String metricServingUnit, String servingDescription, int defaultNumberOfServingUnit,
                float totalFat, float saturatedFat, float polysaturatedFat, float monosaturatedFat, float transFat,
                float carbohydrate, float dietaryFiber, float sugar, float protein, float unitCountPerCalorie, float cholesterol,
                float soduim, float potassium, float vitaminA, float vitaminC, float calcium, float iron) {
            this.providerFoodId = providerFoodId;
            this.infoProvider = infoProvider;
            this.name = name;
            this.calorie = calorie;
            this.description = description;
            this.metricServingAmount = metricServingAmount;
            this.metricServingUnit = metricServingUnit;
            this.servingDescription = servingDescription;
            this.defaultNumberOfServingUnit = defaultNumberOfServingUnit;
            this.totalFat = totalFat;
            this.saturatedFat = saturatedFat;
            this.polysaturatedFat = polysaturatedFat;
            this.monosaturatedFat = monosaturatedFat;
            this.transFat = transFat;
            this.carbohydrate = carbohydrate;
            this.dietaryFiber = dietaryFiber;
            this.sugar = sugar;
            this.protein = protein;
            this.unitCountPerCalorie = unitCountPerCalorie;
            this.cholesterol = cholesterol;
            this.soduim = soduim;
            this.potassium = potassium;
            this.vitaminA = vitaminA;
            this.vitaminC = vitaminC;
            this.calcium = calcium;
            this.iron = iron;
        }
    }

}
