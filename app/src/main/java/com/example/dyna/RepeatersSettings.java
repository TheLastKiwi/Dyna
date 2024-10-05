package com.example.dyna;

import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.NumberPicker;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceFragmentCompat;

import com.google.android.material.switchmaterial.SwitchMaterial;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class RepeatersSettings extends Fragment {

    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);

        view = inflater.inflate(R.layout.repeaters_settings,container, false);
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

        setOptions(view.findViewById(R.id.npReps), numberOptions.toArray(new String[0]));
        setOptions(view.findViewById(R.id.npSets), numberOptions.toArray(new String[0]));
        setOptions(view.findViewById(R.id.npPlotMin), numberOptions.toArray(new String[0]));
        setOptions(view.findViewById(R.id.npPlotMax), numberOptions.toArray(new String[0]));
        ((NumberPicker)view.findViewById(R.id.npPlotMin)).setValue(14);
        ((NumberPicker)view.findViewById(R.id.npPlotMax)).setValue(84);

        //Work time
        ArrayList<String> timeOptions = new ArrayList<>();
        for (int i = 1; i <= 120; i++) {
            String text = "";
            if (i >= 60) {
                text += (i / 60) + "m";
            }
            if (i % 60 > 0) {
                text += " " + i % 60 + "s";
            }
            timeOptions.add(text);
        }

        String[] timeOptionsArray = timeOptions.toArray(new String[0]);

        setOptions(view.findViewById(R.id.npWork), timeOptionsArray);
        setOptions(view.findViewById(R.id.npRest), timeOptionsArray);
        setOptions(view.findViewById(R.id.npPause), timeOptionsArray);
        setOptions(view.findViewById(R.id.npCountdown), timeOptionsArray);
        ((NumberPicker) view.findViewById(R.id.npSets)).setValue(3);
        ((NumberPicker) view.findViewById(R.id.npReps)).setValue(3);
        ((NumberPicker) view.findViewById(R.id.npWork)).setValue(6);
        ((NumberPicker) view.findViewById(R.id.npRest)).setValue(5);
        ((NumberPicker) view.findViewById(R.id.npPause)).setValue(10);
        ((NumberPicker) view.findViewById(R.id.npCountdown)).setValue(3);

        SwitchMaterial switchPlot = view.findViewById(R.id.switchPlot);
        switchPlot.setChecked(true);
        switchPlot.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            switchPlot.setBackgroundColor(isChecked?Color.GREEN:Color.RED);
            view.findViewById(R.id.llPlot).setVisibility(isChecked?View.VISIBLE:View.GONE);
        });
        SwitchMaterial switchSound = view.findViewById(R.id.switchSound);
        switchSound.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            switchSound.setBackgroundColor(isChecked?Color.GREEN:Color.RED);

        });


        //Preset
        //Load presets from disk
        //get all names
        //populate list

        //on click -> load settings
        NavController navController = Navigation.findNavController(requireActivity(), R.id.fragmentContainerView);
        view.findViewById(R.id.btnRepeaterStart).setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("session", createSession());
            navController.navigate(R.id.action_repeatersSettings_to_repeaterLiveData, bundle);
//            Intent intent = new Intent(this, RepeaterLiveData.class);
//            Session session = createSession();
//            intent.putExtra("session",session);
//            startActivity(intent);
        });
        view.findViewById(R.id.btnRepPresetSave).setOnClickListener(view -> {

        });
        return view;
    }

    public Session createSession() {
        Session session = new Session();
        session.sessionType = SessionType.REPEATER;
        session.numSets = ((NumberPicker) view.findViewById(R.id.npSets)).getValue() + 1;
        session.numReps = ((NumberPicker) view.findViewById(R.id.npReps)).getValue() + 1;
        session.workTime = ((NumberPicker) view.findViewById(R.id.npWork)).getValue() + 1;
        session.restTime = ((NumberPicker) view.findViewById(R.id.npRest)).getValue() + 1;
        session.pauseTime = ((NumberPicker) view.findViewById(R.id.npPause)).getValue() + 1;
        session.countdown = ((NumberPicker) view.findViewById(R.id.npCountdown)).getValue() + 1;
        session.sound = ((SwitchMaterial) view.findViewById(R.id.switchSound)).isChecked();
        session.plotTarget = ((SwitchMaterial) view.findViewById(R.id.switchPlot)).isChecked();
        session.plotMin = ((NumberPicker) view.findViewById(R.id.npPlotMin)).getValue() + 1;
        session.plotMax = ((NumberPicker) view.findViewById(R.id.npPlotMax)).getValue() + 1;
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