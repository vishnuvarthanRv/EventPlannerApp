package com.example.eventplannerapp_jf_ict_25_23;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class eventStorage {

    private static final String PREF_NAME =
            "EventPlannerPreferences";

    private static final String KEY_EVENTS =
            "savedEvents";

    // Save Events
    public static void saveEvents(
            Context context,
            ArrayList<event> eventList) {

        try {

            JSONArray jsonArray = new JSONArray();

            for (event e : eventList) {

                JSONObject object = new JSONObject();

                object.put("id", e.getId());

                object.put("name", e.getName());

                object.put(
                        "description",
                        e.getDescription()
                );

                object.put(
                        "dateTime",
                        e.getDateTime()
                );

                object.put(
                        "reminderMinutes",
                        e.getReminderMinutes()
                );

                jsonArray.put(object);

            }

            SharedPreferences preferences =
                    context.getSharedPreferences(
                            PREF_NAME,
                            Context.MODE_PRIVATE
                    );

            preferences.edit()
                    .putString(
                            KEY_EVENTS,
                            jsonArray.toString()
                    )
                    .apply();

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    // Load Events
    public static ArrayList<event> loadEvents(
            Context context) {

        ArrayList<event> eventList =
                new ArrayList<>();

        SharedPreferences preferences =
                context.getSharedPreferences(
                        PREF_NAME,
                        Context.MODE_PRIVATE
                );

        String savedData =
                preferences.getString(
                        KEY_EVENTS,
                        "[]"
                );

        try {

            JSONArray jsonArray =
                    new JSONArray(savedData);

            for (int i = 0;
                 i < jsonArray.length();
                 i++) {

                JSONObject object =
                        jsonArray.getJSONObject(i);

                String id =
                        object.getString("id");

                String name =
                        object.getString("name");

                String description =
                        object.getString("description");

                long dateTime =
                        object.getLong("dateTime");

                int reminderMinutes =
                        object.getInt("reminderMinutes");

                event e = new event(
                        id,
                        name,
                        description,
                        dateTime,
                        reminderMinutes
                );

                eventList.add(e);

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        return eventList;

    }

}