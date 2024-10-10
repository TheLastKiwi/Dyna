package com.flying_kiwi.dyna.Settings;

import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceFragmentCompat;

import com.flying_kiwi.dyna.R;
import com.flying_kiwi.dyna.Session;
import com.flying_kiwi.dyna.SessionType;
import com.flying_kiwi.dyna.Utils.FileManager;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

public class RepeatersSettings extends Fragment {

    View view;
    HashMap<String,PresetSettings> presetSettingsMap;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.repeaters_settings_fragment, container, false);

        setInitialOptions();

        loadPresetOptions();

        SwitchMaterial switchPlot = view.findViewById(R.id.switchPlot);
        switchPlot.setChecked(true);
        switchPlot.setOnCheckedChangeListener((buttonView, isChecked) -> {
            view.findViewById(R.id.llPlot).setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });

        //TODO save preset and load settings from preset
        //Preset
        //Load presets from disk
        //get all names
        //populate list

        NavController navController = Navigation.findNavController(requireActivity(), R.id.fragmentContainerView);
        view.findViewById(R.id.btnRepeaterStart).setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("session", createSession());
            navController.navigate(R.id.action_repeatersSettings_to_repeaterLiveData, bundle);
        });
        view.findViewById(R.id.btnRepPresetSave).setOnClickListener(v -> {
            PresetSettings presetSettings = new PresetSettings(
                    ((NumberPicker) view.findViewById(R.id.npSets)).getValue(),
                    ((NumberPicker) view.findViewById(R.id.npReps)).getValue(),
                    ((NumberPicker) view.findViewById(R.id.npWork)).getValue(),
                    ((NumberPicker) view.findViewById(R.id.npRest)).getValue(),
                    ((NumberPicker) view.findViewById(R.id.npPause)).getValue(),
                    ((NumberPicker) view.findViewById(R.id.npCountdown)).getValue(),
                    switchPlot.isChecked(),
                    ((NumberPicker) view.findViewById(R.id.npPlotMin)).getValue(),
                    ((NumberPicker) view.findViewById(R.id.npPlotMax)).getValue(),
                    ((SwitchMaterial) view.findViewById(R.id.switchSound)).isChecked()
            );
            showSavePresetDialog(presetSettings);
            //Get all new options
            //reset dropdown options
            //invalidate to redraw?
        });

        Spinner presetSpinner = view.findViewById(R.id.spinRepeaterPreset);
        presetSpinner.setSelection(-1);
        presetSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if(position == 0) return;
                String selectedValue = parentView.getItemAtPosition(position).toString();
                loadPreset(presetSettingsMap.get(selectedValue));
                // Your action on item selected
                Log.d("SpinnerSelection", "Selected value: " + selectedValue);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Optional: Handle if nothing is selected
            }
        });

        return view;
    }

    public void loadPreset(PresetSettings settings){
        ((NumberPicker) view.findViewById(R.id.npSets)).setValue(settings.getNumSets());
        ((NumberPicker) view.findViewById(R.id.npReps)).setValue(settings.getNumReps());
        ((NumberPicker) view.findViewById(R.id.npWork)).setValue(settings.getWorkTime());
        ((NumberPicker) view.findViewById(R.id.npRest)).setValue(settings.getRestTime());
        ((NumberPicker) view.findViewById(R.id.npPause)).setValue(settings.getPauseTime());
        ((NumberPicker) view.findViewById(R.id.npCountdown)).setValue(settings.getCountdown());
        ((SwitchMaterial) view.findViewById(R.id.switchPlot)).setChecked(settings.isPlotTarget());
        ((NumberPicker) view.findViewById(R.id.npPlotMin)).setValue(settings.getPlotMin());
        ((NumberPicker) view.findViewById(R.id.npPlotMax)).setValue(settings.getPlotMax());
        ((SwitchMaterial) view.findViewById(R.id.switchSound)).setChecked(settings.isSound());
    }

    void showSavePresetDialog(PresetSettings presetSettings) {
        final EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("Enter session name");

        // Build the dialog
        new AlertDialog.Builder(requireContext())
                .setTitle("Save Preset")
                .setMessage("Please enter a name for the preset")
                .setView(input)
                .setPositiveButton("Save", (dialog, which) -> {
                    // Get the user input
                    String fileName = input.getText().toString().replace('/','_').trim();

                    if (!fileName.isEmpty()) {
                        // Handle the file saving process here
                        FileManager fm = new FileManager(requireContext());
                        presetSettings.setName(fileName);
                        if(fm.savePreset(presetSettings, SessionType.REPEATER)){
                            presetSettingsMap.put(fileName,presetSettings);
                            ArrayAdapter<String> adapter = (ArrayAdapter<String>)((Spinner)view.findViewById(R.id.spinRepeaterPreset)).getAdapter();
                            adapter.add(fileName);
                            adapter.notifyDataSetChanged();
                            Toast.makeText(requireContext(), "Preset saved", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(requireContext(), "Preset name cannot be empty", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    public void setInitialOptions(){
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
        ((NumberPicker) view.findViewById(R.id.npSets)).setValue(2);
        ((NumberPicker) view.findViewById(R.id.npReps)).setValue(3);
        ((NumberPicker) view.findViewById(R.id.npWork)).setValue(4);
        ((NumberPicker) view.findViewById(R.id.npRest)).setValue(3);
        ((NumberPicker) view.findViewById(R.id.npPause)).setValue(9);
        ((NumberPicker) view.findViewById(R.id.npCountdown)).setValue(3);
    }

    public void loadPresetOptions(){
        HashMap<String,PresetSettings> presetSettings = new HashMap<>();
        FileManager fm = new FileManager(requireContext());
        Spinner presetSpinner = view.findViewById(R.id.spinRepeaterPreset);

        ArrayList<String> options = new ArrayList<>();
        options.add("Load a preset");
        for(PresetSettings settings : fm.getAllPresets(SessionType.REPEATER)){
            presetSettings.put(settings.getName(),settings);
            options.add(settings.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        presetSpinner.setAdapter(adapter);
        this.presetSettingsMap = presetSettings;
    }

    public Session createSession() {
        Session session = new Session(SessionType.REPEATER);
        session.setNumSets(((NumberPicker) view.findViewById(R.id.npSets)).getValue() + 1);
        session.setNumReps(((NumberPicker) view.findViewById(R.id.npReps)).getValue() + 1);
        session.setWorkTime(((NumberPicker) view.findViewById(R.id.npWork)).getValue() + 1);
        session.setRestTime(((NumberPicker) view.findViewById(R.id.npRest)).getValue() + 1);
        session.setPauseTime(((NumberPicker) view.findViewById(R.id.npPause)).getValue() + 1);
        session.setCountdown(((NumberPicker) view.findViewById(R.id.npCountdown)).getValue() + 1);
        session.setSound(((SwitchMaterial) view.findViewById(R.id.switchSound)).isChecked());
        session.setPlotTarget(((SwitchMaterial) view.findViewById(R.id.switchPlot)).isChecked());
        session.setPlotMin(((NumberPicker) view.findViewById(R.id.npPlotMin)).getValue() + 1);
        session.setPlotMax(((NumberPicker) view.findViewById(R.id.npPlotMax)).getValue() + 1);
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