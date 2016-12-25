package com.aspirephile.petfeeder.android;

import android.database.SQLException;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.aspirephile.petfeeder.android.db.Db;
import com.aspirephile.petfeeder.android.db.async.OnInsertCompleteListener;
import com.aspirephile.petfeeder.android.schedule.Schedule;

public class ScheduleCreatorActivity extends AppCompatActivity {

    private Button createB;
    private EditText titleView;
    private Spinner dayOfWeekView;
    private TextInputLayout titleInputLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_creator);

        titleView = (EditText) findViewById(R.id.title_input);
        titleInputLayout = (TextInputLayout) findViewById(R.id.title_input_layout);

        dayOfWeekView = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(ScheduleCreatorActivity.this,
                R.array.days_of_week,
                android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dayOfWeekView.setAdapter(adapter);
        dayOfWeekView.setEnabled(false);

        ToggleButton repeatModeView = (ToggleButton) findViewById(R.id.t_repeatMode);
        repeatModeView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dayOfWeekView.setEnabled(isChecked);
            }
        });

        final SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);

        final TimePicker timePicker = (TimePicker) findViewById(R.id.timePicker);

        createB = (Button) findViewById(R.id.b_create);
        createB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Schedule.Content schedule = new Schedule.Content();
                schedule.name = titleView.getText().toString();
                int hour, minute;
                if (Build.VERSION.SDK_INT >= 23) {
                    hour = timePicker.getHour();
                } else {
                    //noinspection deprecation
                    hour = timePicker.getCurrentHour();
                }
                if (Build.VERSION.SDK_INT >= 23) {
                    minute = timePicker.getMinute();
                } else {
                    //noinspection deprecation
                    minute = timePicker.getCurrentMinute();
                }
                schedule.setHour((short) hour);
                schedule.setMinute((short) minute);
                if (dayOfWeekView.isEnabled()) {
                    schedule.setDayOfWeek(dayOfWeekView.getSelectedItemPosition() + 1);
                } else {
                    schedule.setDayOfWeek(0);
                }
                schedule.quantity = seekBar.getProgress();

                if (ensureValidity()) {
                    Db.getScheduleManager().insert(schedule).executeInBackground(new OnInsertCompleteListener() {
                        @Override
                        public void onInsertComplete(long rowId, SQLException e) {
                            if (e != null) {
                                e.printStackTrace();
                                Toast.makeText(ScheduleCreatorActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG)
                                        .show();
                                return;
                            }

                            Toast.makeText(ScheduleCreatorActivity.this, R.string.schedule_created, Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                }
            }
        });
    }

    private boolean ensureValidity() {
        if (titleView.getText().toString().isEmpty()) {
            titleInputLayout.setError(getString(R.string.error_schedule_name_empty));
            return false;
        }
        return true;
    }
}
