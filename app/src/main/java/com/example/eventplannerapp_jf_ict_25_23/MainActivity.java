package com.example.eventplannerapp_jf_ict_25_23;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import androidx.cardview.widget.CardView;

public class MainActivity extends Activity {

    private Button btnAddEvent;
    private Button btnPastEvents;
    private EditText etSearch;
    private LinearLayout eventContainer;

    private ArrayList<event> eventList;

    private boolean showPastEvents = false;

    private final int[] reminderValues = {
            0, 5, 15, 60
    };

    private final String[] reminderOptions = {
            "At event time",
            "5 minutes before",
            "15 minutes before",
            "1 hour before"
    };

    private final SimpleDateFormat dateFormat =
            new SimpleDateFormat(
                    "dd MMM yyyy, hh:mm a",
                    Locale.getDefault()
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Load saved events
        eventList = eventStorage.loadEvents(this);

        // Connect XML components
        btnAddEvent = findViewById(R.id.btnAddEvent);
        btnPastEvents = findViewById(R.id.btnPastEvents);
        etSearch = findViewById(R.id.etSearch);
        eventContainer = findViewById(R.id.eventContainer);

        // Notification permission
        requestNotificationPermission();

        // Add Event
        btnAddEvent.setOnClickListener(v ->
                showEventDialog(null)
        );

        // Show Past Events
        btnPastEvents.setOnClickListener(v -> {

            showPastEvents = !showPastEvents;

            displayEvents();

        });

        // Search Events
        etSearch.addTextChangedListener(
                new TextWatcher() {

                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after) {
                    }

                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count) {

                        displayEvents();
                    }

                    @Override
                    public void afterTextChanged(
                            Editable s) {
                    }

                }
        );

        // Restore upcoming reminders
        for (event e : eventList) {

            ReminderScheduler.scheduleReminder(
                    this, e
            );

        }

        displayEvents();

    }

    // =================================
    // ADD AND EDIT EVENT
    // =================================

    private void showEventDialog(event existingEvent) {

        LayoutInflater inflater =
                LayoutInflater.from(this);

        View formView = inflater.inflate(
                R.layout.activity_edit_event,
                null
        );

        EditText etEventName =
                formView.findViewById(
                        R.id.etEventName
                );

        EditText etEventDescription =
                formView.findViewById(
                        R.id.etEventDescription
                );

        Button btnSelectDate =
                formView.findViewById(
                        R.id.btnSelectDate
                );

        Button btnSelectTime =
                formView.findViewById(
                        R.id.btnSelectTime
                );

        TextView tvSelectedDate =
                formView.findViewById(
                        R.id.tvSelectedDate
                );

        TextView tvSelectedTime =
                formView.findViewById(
                        R.id.tvSelectedTime
                );

        Spinner spReminder =
                formView.findViewById(
                        R.id.spReminder
                );

        TextView tvFormTitle =
                formView.findViewById(
                        R.id.tvFormTitle
                );

        Button btnSaveEvent =
                formView.findViewById(
                        R.id.btnSaveEvent
                );

        Calendar selectedDate = Calendar.getInstance();

        if (existingEvent != null) {

            tvFormTitle.setText("Edit Event");

            btnSaveEvent.setText("Update Event");

            etEventName.setText(
                    existingEvent.getName()
            );

            etEventDescription.setText(
                    existingEvent.getDescription()
            );

            selectedDate.setTimeInMillis(
                    existingEvent.getDateTime()
            );

        }

        // Reminder Spinner
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

        if (existingEvent != null) {

            for (int i = 0; i < reminderValues.length; i++) {

                if (reminderValues[i] ==
                        existingEvent.getReminderMinutes()) {

                    spReminder.setSelection(i);

                    break;
                }
            }
        }

        updateDateTimeLabels(
                tvSelectedDate,
                tvSelectedTime,
                selectedDate
        );

        // Date Picker
        btnSelectDate.setOnClickListener(v -> {

            new DatePickerDialog(
                    this,
                    (view, year, month, day) -> {

                        selectedDate.set(
                                Calendar.YEAR, year
                        );

                        selectedDate.set(
                                Calendar.MONTH, month
                        );

                        selectedDate.set(
                                Calendar.DAY_OF_MONTH, day
                        );

                        updateDateTimeLabels(
                                tvSelectedDate,
                                tvSelectedTime,
                                selectedDate
                        );

                    },

                    selectedDate.get(Calendar.YEAR),

                    selectedDate.get(Calendar.MONTH),

                    selectedDate.get(Calendar.DAY_OF_MONTH)

            ).show();

        });

        // Time Picker
        btnSelectTime.setOnClickListener(v -> {

            new TimePickerDialog(
                    this,
                    (view, hour, minute) -> {

                        selectedDate.set(
                                Calendar.HOUR_OF_DAY,
                                hour
                        );

                        selectedDate.set(
                                Calendar.MINUTE,
                                minute
                        );

                        selectedDate.set(
                                Calendar.SECOND,
                                0
                        );

                        selectedDate.set(
                                Calendar.MILLISECOND,
                                0
                        );

                        updateDateTimeLabels(
                                tvSelectedDate,
                                tvSelectedTime,
                                selectedDate
                        );

                    },

                    selectedDate.get(Calendar.HOUR_OF_DAY),

                    selectedDate.get(Calendar.MINUTE),

                    false

            ).show();

        });

        AlertDialog dialog =
                new AlertDialog.Builder(this)
                        .setView(formView)
                        .setNegativeButton("Cancel", null)
                        .create();

        dialog.show();

        // Save Event
        btnSaveEvent.setOnClickListener(v -> {

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

            if (selectedDate.getTimeInMillis()
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

            if (existingEvent == null) {

                // Create new event

                event newEvent = new event(
                        UUID.randomUUID().toString(),
                        name,
                        description,
                        selectedDate.getTimeInMillis(),
                        reminder
                );

                eventList.add(newEvent);

                ReminderScheduler.scheduleReminder(
                        this, newEvent
                );

            } else {

                // Update existing event

                ReminderScheduler.cancelReminder(
                        this, existingEvent
                );

                existingEvent.setName(name);

                existingEvent.setDescription(
                        description
                );

                existingEvent.setDateTime(
                        selectedDate.getTimeInMillis()
                );

                existingEvent.setReminderMinutes(
                        reminder
                );

                ReminderScheduler.scheduleReminder(
                        this, existingEvent
                );

            }

            eventStorage.saveEvents(
                    this, eventList
            );

            displayEvents();

            dialog.dismiss();

            Toast.makeText(
                    this,
                    "Event saved successfully!",
                    Toast.LENGTH_SHORT
            ).show();

        });

    }

    // =================================
    // DATE AND TIME LABELS
    // =================================

    private void updateDateTimeLabels(
            TextView dateText,
            TextView timeText,
            Calendar calendar) {

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

        dateText.setText(
                "Date: " +
                        dateFormat.format(
                                calendar.getTime()
                        )
        );

        timeText.setText(
                "Time: " +
                        timeFormat.format(
                                calendar.getTime()
                        )
        );

    }

    // =================================
    // DISPLAY EVENTS
    // =================================

    private void displayEvents() {

        eventContainer.removeAllViews();

        Collections.sort(
                eventList,
                Comparator.comparingLong(
                        event::getDateTime
                )
        );

        String searchText =
                etSearch.getText()
                        .toString()
                        .trim()
                        .toLowerCase(Locale.getDefault());

        long currentTime =
                System.currentTimeMillis();

        int count = 0;

        for (event e : eventList) {

            boolean isPast =
                    e.getDateTime() < currentTime;

            if (isPast && !showPastEvents) {
                continue;
            }

            String formattedDate =
                    dateFormat.format(
                            new Date(e.getDateTime())
                    );

            boolean matchesName =
                    e.getName()
                            .toLowerCase(Locale.getDefault())
                            .contains(searchText);

            boolean matchesDate =
                    formattedDate
                            .toLowerCase(Locale.getDefault())
                            .contains(searchText);

            if (!matchesName && !matchesDate) {
                continue;
            }

            addEventCard(e, isPast);

            count++;

        }

        if (count == 0) {

            TextView emptyText = new TextView(this);

            emptyText.setText(
                    searchText.isEmpty()
                            ? "No events found. Add your first event!"
                            : "No matching events found."
            );

            emptyText.setTextSize(16);

            emptyText.setTextColor(
                    android.graphics.Color.GRAY
            );

            emptyText.setGravity(
                    android.view.Gravity.CENTER
            );

            emptyText.setPadding(
                    15, 40, 15, 40
            );

            eventContainer.addView(emptyText);

        }

    }

    // =================================
    // EVENT CARD
    // =================================

    private void addEventCard(
            event e,
            boolean isPast) {

        LayoutInflater inflater =
                LayoutInflater.from(this);

        View cardView = inflater.inflate(
                R.layout.item_event,
                eventContainer,
                false
        );

        TextView tvEventName =
                cardView.findViewById(
                        R.id.tvEventName
                );

        TextView tvEventDate =
                cardView.findViewById(
                        R.id.tvEventDate
                );

        TextView tvEventDescription =
                cardView.findViewById(
                        R.id.tvEventDescription
                );

        TextView tvEventReminder =
                cardView.findViewById(
                        R.id.tvEventReminder
                );

        Button btnEditEvent =
                cardView.findViewById(
                        R.id.btnEditEvent
                );

        Button btnDeleteEvent =
                cardView.findViewById(
                        R.id.btnDeleteEvent
                );

        tvEventName.setText(
                e.getName() +
                        (isPast ? " • Completed" : "")
        );

        tvEventDate.setText(
                dateFormat.format(
                        new Date(e.getDateTime())
                )
        );

        tvEventDescription.setText(
                e.getDescription().isEmpty()
                        ? "No description"
                        : e.getDescription()
        );

        tvEventReminder.setText(
                "Reminder: " +
                        getReminderText(
                                e.getReminderMinutes()
                        )
        );

        // Edit Button
        btnEditEvent.setOnClickListener(
                v -> showEventDialog(e)
        );

        // Delete Button
        btnDeleteEvent.setOnClickListener(v -> {

            new AlertDialog.Builder(this)

                    .setTitle("Delete Event")

                    .setMessage(
                            "Are you sure you want to delete this event?"
                    )

                    .setNegativeButton(
                            "Cancel", null
                    )

                    .setPositiveButton(
                            "Delete",
                            (dialog, which) -> {

                                ReminderScheduler.cancelReminder(
                                        this, e
                                );

                                eventList.remove(e);

                                eventStorage.saveEvents(
                                        this, eventList
                                );

                                displayEvents();

                                Toast.makeText(
                                        this,
                                        "Event deleted successfully!",
                                        Toast.LENGTH_SHORT
                                ).show();

                            }
                    )

                    .show();

        });

        eventContainer.addView(cardView);

    }

    // =================================
    // REMINDER TEXT
    // =================================

    private String getReminderText(int minutes) {

        if (minutes == 0) {
            return "At event time";
        }

        if (minutes == 60) {
            return "1 hour before";
        }

        return minutes + " minutes before";

    }

    // =================================
    // NOTIFICATION PERMISSION
    // =================================

    private void requestNotificationPermission() {

        if (Build.VERSION.SDK_INT >= 33) {

            if (checkSelfPermission(
                    Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(
                        new String[]{
                                Manifest.permission.POST_NOTIFICATIONS
                        },
                        100
                );

            }

        }

    }

}