package com.example.eventplannerapp_jf_ict_25_23;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditEventActivity extends Activity {

    private EditText etEventName;
    private EditText etEventDescription;

    private Button btnSelectDate;
    private Button btnSelectTime;
    private Button btnSaveEvent;

    private TextView tvSelectedDate;
    private TextView tvSelectedTime;
    private TextView tvFormTitle;

    private Spinner spReminder;

    private Calendar calendar;

    private ArrayList<event> eventList;

    private event editingEvent = null;

    private final int[] reminderValues = {
            0, 5, 15, 60
    };

    private final String[] reminderOptions = {
            "At event time",
            "5 minutes before",
            "15 minutes before",
            "1 hour before"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_event);

        calendar = Calendar.getInstance();

        calendar.add(Calendar.HOUR_OF_DAY, 1);

        eventList = eventStorage.loadEvents(this);

        // Connect XML components

        etEventName = findViewById(R.id.etEventName);

        etEventDescription =
                findViewById(R.id.etEventDescription);

        btnSelectDate =
                findViewById(R.id.btnSelectDate);

        btnSelectTime =
                findViewById(R.id.btnSelectTime);

        btnSaveEvent =
                findViewById(R.id.btnSaveEvent);

        tvSelectedDate =
                findViewById(R.id.tvSelectedDate);

        tvSelectedTime =
                findViewById(R.id.tvSelectedTime);

        tvFormTitle =
                findViewById(R.id.tvFormTitle);

        spReminder =
                findViewById(R.id.spReminder);

        // Setup Reminder Spinner

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        reminderOptions
                );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spReminder.setAdapter(adapter);

        // Check whether editing an existing event

        String editId =
                getIntent().getStringExtra("event_id");

        if (editId != null) {

            for (event e : eventList) {

                if (e.getId().equals(editId)) {

                    editingEvent = e;

                    break;
                }
            }
        }

        if (editingEvent != null) {

            tvFormTitle.setText("Edit Event");

            btnSaveEvent.setText("Update Event");

            etEventName.setText(
                    editingEvent.getName()
            );

            etEventDescription.setText(
                    editingEvent.getDescription()
            );

            calendar.setTimeInMillis(
                    editingEvent.getDateTime()
            );

            for (int i = 0;
                 i < reminderValues.length;
                 i++) {

                if (reminderValues[i] ==
                        editingEvent.getReminderMinutes()) {

                    spReminder.setSelection(i);

                    break;
                }
            }
        }

        updateDateTimeLabels();

        // Date Picker

        btnSelectDate.setOnClickListener(v -> {

            new DatePickerDialog(
                    this,

                    (view, year, month, day) -> {

                        calendar.set(
                                Calendar.YEAR, year
                        );

                        calendar.set(
                                Calendar.MONTH, month
                        );

                        calendar.set(
                                Calendar.DAY_OF_MONTH, day
                        );

                        updateDateTimeLabels();

                    },

                    calendar.get(Calendar.YEAR),

                    calendar.get(Calendar.MONTH),

                    calendar.get(Calendar.DAY_OF_MONTH)

            ).show();

        });

        // Time Picker

        btnSelectTime.setOnClickListener(v -> {

            new TimePickerDialog(
                    this,

                    (view, hour, minute) -> {

                        calendar.set(
                                Calendar.HOUR_OF_DAY,
                                hour
                        );

                        calendar.set(
                                Calendar.MINUTE,
                                minute
                        );

                        calendar.set(
                                Calendar.SECOND,
                                0
                        );

                        updateDateTimeLabels();

                    },

                    calendar.get(Calendar.HOUR_OF_DAY),

                    calendar.get(Calendar.MINUTE),

                    false

            ).show();

        });

        // Save Event

        btnSaveEvent.setOnClickListener(
                v -> saveEvent()
        );

    }

    // Update Date and Time Labels

    private void updateDateTimeLabels() {

        SimpleDateFormat dateFormat =
                new SimpleDateFormat(
                        "dd MMM yyyy",
                        Locale.getDefault()
                );

        SimpleDateFormat timeFormat =
                new SimpleDateFormat(
                        "hh:mm a",
                        Locale.getDefault()
                );

        tvSelectedDate.setText(
                "Date: " +
                        dateFormat.format(
                                calendar.getTime()
                        )
        );

        tvSelectedTime.setText(
                "Time: " +
                        timeFormat.format(
                                calendar.getTime()
                        )
        );

    }

    // Save Event

    private void saveEvent() {

        String name =
                etEventName.getText()
                        .toString().trim();

        String description =
                etEventDescription.getText()
                        .toString().trim();

        if (name.isEmpty()) {

            etEventName.setError(
                    "Event name is required"
            );

            return;
        }

        if (name.length() > 50) {

            etEventName.setError(
                    "Maximum 50 characters allowed"
            );

            return;
        }

        if (description.length() > 200) {

            etEventDescription.setError(
                    "Maximum 200 characters allowed"
            );

            return;
        }

        calendar.set(
                Calendar.SECOND, 0
        );

        calendar.set(
                Calendar.MILLISECOND, 0
        );

        if (calendar.getTimeInMillis()
                <= System.currentTimeMillis()) {

            Toast.makeText(
                    this,
                    "Please select a future date and time",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        int reminder =
                reminderValues[
                        spReminder.getSelectedItemPosition()
                        ];

        if (editingEvent != null) {

            ReminderScheduler.cancelReminder(
                    this, editingEvent
            );

            editingEvent.setName(name);

            editingEvent.setDescription(description);

            editingEvent.setDateTime(
                    calendar.getTimeInMillis()
            );

            editingEvent.setReminderMinutes(
                    reminder
            );

            ReminderScheduler.scheduleReminder(
                    this, editingEvent
            );

        } else {

            event newEvent = new event(
                    java.util.UUID.randomUUID().toString(),
                    name,
                    description,
                    calendar.getTimeInMillis(),
                    reminder
            );

            eventList.add(newEvent);

            ReminderScheduler.scheduleReminder(
                    this, newEvent
            );

        }

        eventStorage.saveEvents(
                this, eventList
        );

        Toast.makeText(
                this,
                "Event saved successfully!",
                Toast.LENGTH_SHORT
        ).show();

        finish();

    }

}