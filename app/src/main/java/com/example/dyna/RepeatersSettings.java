package com.example.dyna;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;

import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.preference.PreferenceFragmentCompat;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class RepeatersSettings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.repeaters);
//        if (savedInstanceState == null) {
//            getSupportFragmentManager()
//                    .beginTransaction()
////                    .replace(R.id.settings, new SettingsFragment())
//                    .commit();
//        }
//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.setDisplayHomeAsUpEnabled(true);
//        }
        //Reps
        ArrayList<String> numberOptions = new ArrayList<>();
        for (int i = 1; i < 100; i++) {
            numberOptions.add(String.valueOf(i));
        }

        setOptions(findViewById(R.id.npReps), numberOptions.toArray(new String[0]));
        setOptions(findViewById(R.id.npSets), numberOptions.toArray(new String[0]));
        setOptions(findViewById(R.id.npPlotMin), numberOptions.toArray(new String[0]));
        setOptions(findViewById(R.id.npPlotMax), numberOptions.toArray(new String[0]));
        ((NumberPicker)findViewById(R.id.npPlotMin)).setValue(14);
        ((NumberPicker)findViewById(R.id.npPlotMax)).setValue(84);

        //Work time
        ArrayList<String> timeOptions = new ArrayList<>();
        for (int i = 1; i <= 120; i++) {
            String text = "";
            if (i >= 60) {
                text += (i / 60) + " min";
            }
            if (i % 60 > 0) {
                text += " " + i % 60 + " sec";
            }
            timeOptions.add(text);
        }

        String[] timeOptionsArray = timeOptions.toArray(new String[0]);

        setOptions(findViewById(R.id.npWork), timeOptionsArray);
        setOptions(findViewById(R.id.npRest), timeOptionsArray);
        setOptions(findViewById(R.id.npPause), timeOptionsArray);
        setOptions(findViewById(R.id.npCountdown), timeOptionsArray);


        Switch switchPlot = findViewById(R.id.switchPlot);
        switchPlot.setOnCheckedChangeListener((buttonView, isChecked) -> {
            switchPlot.setBackgroundColor(isChecked?Color.GREEN:Color.RED);
            findViewById(R.id.llPlot).setVisibility(isChecked?View.VISIBLE:View.INVISIBLE);
        });
        Switch switchSound = findViewById(R.id.switchSound);
        switchSound.setOnCheckedChangeListener((buttonView, isChecked) -> {
            switchSound.setBackgroundColor(isChecked?Color.GREEN:Color.RED);

        });
        //Preset
        //Load presets from disk
        //get all names
        //populate list

        //on click -> load settings

        findViewById(R.id.btnRepeaterStart).setOnClickListener(view -> {
            Intent intent = new Intent(this, LiveDataView.class);
            Session session = createSession();
            intent.putExtra("session",session);
            startActivity(intent);
        });
        findViewById(R.id.btnRepSave).setOnClickListener(view -> {

        });
    }

    public Session createSession() {
        Session session = new Session();
        session.sessionType = SessionType.REPEATER;
        session.numSets = ((NumberPicker) findViewById(R.id.npSets)).getValue() + 1;
        session.numReps = ((NumberPicker) findViewById(R.id.npReps)).getValue() + 1;
        session.workTime = ((NumberPicker) findViewById(R.id.npWork)).getValue() + 1;
        session.restTime = ((NumberPicker) findViewById(R.id.npRest)).getValue() + 1;
        session.pauseTime = ((NumberPicker) findViewById(R.id.npPause)).getValue() + 1;
        session.countdown = ((NumberPicker) findViewById(R.id.npCountdown)).getValue() + 1;
        session.sound = ((Switch) findViewById(R.id.switchSound)).isChecked();
        session.plotTarget = ((Switch) findViewById(R.id.switchPlot)).isChecked();
        session.plotMin = ((NumberPicker) findViewById(R.id.npPlotMin)).getValue() + 1;
        session.plotMax = ((NumberPicker) findViewById(R.id.npPlotMax)).getValue() + 1;
        return session;
    }

    public void setOptions(NumberPicker np, String[] options) {
    /*
        np.setWrapSelectorWheel(false); // Makes it look better, but first and last entries are sticky

        So now we add 2 null entries to the end of the list. If you scroll to one of them either forwards
        Or backwards it now moves you to the previous position you were at. This also sucks
        But it's better than sticky starting options
    */
        String[] emptyLastOptions = new String[options.length + 2];

        System.arraycopy(options, 0, emptyLastOptions, 0, options.length);
        emptyLastOptions[options.length] = "";
        emptyLastOptions[options.length + 1] = "";
        np.setOnValueChangedListener((picker, oldVal, newVal) -> {
            if (newVal >= picker.getMaxValue() - 1) {
                picker.setValue(oldVal);
            }
        });

        np.setDisplayedValues(emptyLastOptions);
        np.setMinValue(0);
        np.setMaxValue(emptyLastOptions.length - 1);

        //First option is able to be clicked and shows keyboard, stop that. Terrible way to do it
        //But it works
        try {

            Field field = NumberPicker.class.getDeclaredField("mInputText");
            field.setAccessible(true);
            EditText editText = (EditText) field.get(np);

            // Disable focus and prevent the keyboard from appearing
            editText.setFocusable(false);
            editText.setFocusableInTouchMode(false);
            editText.setInputType(InputType.TYPE_NULL); // Disable input
        } catch (Exception e){

        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }
    }


}