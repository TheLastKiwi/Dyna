package com.flying_kiwi.dyna.Utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.flying_kiwi.dyna.Profile;
import com.flying_kiwi.dyna.Session;
import com.flying_kiwi.dyna.SessionType;
import com.flying_kiwi.dyna.Settings.PresetSettings;
import com.flying_kiwi.dyna.TimestampedWeight;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

//TODO: Remove all these los in the try catch and raise an error so the user knows what happened
public class FileManager {
    Context context;
    Profile activeProfile;
    public FileManager(Context context){
        this.context = context;
        activeProfile = getActiveProfile();
    }

    public void saveSession(Session s){

            File userDirectory = new File(context.getFilesDir(), activeProfile.getName());
            if (!userDirectory.exists()) {
                userDirectory.mkdirs();
            }
            File sessionDirectory = new File(userDirectory,s.getSessionType().toString());
            if (!sessionDirectory.exists()) {
                sessionDirectory.mkdirs();
            }

            // Create the file in the subdirectory
            File file = new File(sessionDirectory, s.getName());

            saveFile(file,s, true);
    }
    public Session getSession(SessionType sessionType, String name){
        File profileDirectory = new File(context.getFilesDir(), activeProfile.getName());
        if (!profileDirectory.exists()) {
            profileDirectory.mkdirs();
        }
        File sessionDirectory = new File(profileDirectory, sessionType.toString());
        if (!sessionDirectory.exists()) {
            sessionDirectory.mkdirs();
        }
        File file = new File(sessionDirectory,name);

        return (Session) readFile(file);
    }

    public ArrayList<Session> getAllSessions(SessionType sessionType) {

        // Get the directory (if it exists) or create it
        File profileDirectory = new File(context.getFilesDir(), activeProfile.getName());
        if (!profileDirectory.exists()) {
            profileDirectory.mkdirs();
        }
        File sessionDirectory = new File(profileDirectory, sessionType.toString());
        if (!sessionDirectory.exists()) {
            sessionDirectory.mkdirs();
        }
        File[] allFiles = null;
        if (sessionDirectory.exists() && sessionDirectory.isDirectory()) {
            // List all files in the directory
            allFiles = sessionDirectory.listFiles();
        }
        ArrayList<Session> sessions = new ArrayList<>();

        for(File file: allFiles){
            if(file.isFile()){
                Session s = (Session) readFile(file);
                sessions.add(s);
            }
        }
        return sessions;
    }
    public Profile getProfile(String userName) {

        Profile profile = null;

        File profileDirectory = new File(context.getFilesDir(), "profile");
        if (!profileDirectory.exists()) {
            Log.d("Uhoh","Profile directory not found");
            return null;
        }
        File userFile = new File(profileDirectory,userName);
        profile = (Profile) readFile(userFile);

        return profile;
    }

    public void saveProfile(Profile profile) {

            // Create or get the profile directory
            File profileDirectory = new File(context.getFilesDir(), "profile");
            if (!profileDirectory.exists()) {
                profileDirectory.mkdirs();
            }
            // Create the file in the subdirectory
            File file = new File(profileDirectory, profile.getName());
            saveFile(file, profile, false);
    }

    public ArrayList<Profile> getAllProfiles() {
        File profileDirectory = new File(context.getFilesDir(), "profile");
        if (!profileDirectory.exists()) {
            profileDirectory.mkdirs();
        }

        File[] allFiles = null;
        if (profileDirectory.exists() && profileDirectory.isDirectory()) {
            // List all files in the directory
            allFiles = profileDirectory.listFiles();
        }
        ArrayList<Profile> profiles = new ArrayList<>();

        for(File file: allFiles){
            if(file.isFile()){
                profiles.add((Profile) readFile(file));
            }
        }
        return profiles;
    }

    public Profile getActiveProfile(){
        String activeUser = context.getSharedPreferences("Settings", Context.MODE_PRIVATE).getString("ActiveUser","Default");
        return getProfile(activeUser);
    }

    public void deleteSession(Session s){
        File profileDirectory = new File(context.getFilesDir(), activeProfile.getName());
        if (!profileDirectory.exists()) {
            profileDirectory.mkdirs();
        }
        File sessionDirectory = new File(profileDirectory, s.getSessionType().toString());
        if (!sessionDirectory.exists()) {
            sessionDirectory.mkdirs();
        }
        File sessionFile = new File(sessionDirectory, s.getName());
        sessionFile.delete();
    }
    public void deleteAllSessions(){
        for(SessionType st : SessionType.values()){
            for(Session s : getAllSessions(st)){
                deleteSession(s);
            }
        }

    }
    public void deleteProfile(Profile p){

    }
    public void exportSessionToCSV(Session s){
        StringBuilder csv = new StringBuilder();
        csv.append("Weight,Time").append("\n");
        for(TimestampedWeight tsw : s.getWeights()){
            csv.append(tsw.getWeight()).append(",").append(tsw.getTimestamp()).append('\n');
        }
        String fileName = s.getName();
        //Set a default name is they click export without saving first
        if(fileName == null) fileName = s.getSessionType().toString() + " " + System.currentTimeMillis();
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName + ".csv");
        if(saveFile(file, csv.toString(), false)) {
            Toast.makeText(context, "File saved at: " + file.getPath(), Toast.LENGTH_LONG).show();
        }
    }
    public Object readFile(File file) {
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        Object ret = null;
        try {
            fis = new FileInputStream(file);
            ois = new ObjectInputStream(fis);
            ret = ois.readObject();
        } catch (IOException | ClassNotFoundException ignore){
//            file.delete();
            Toast.makeText(context, "Error Reading", Toast.LENGTH_SHORT).show();
        } finally {
            try {
                if(fis != null) fis.close();
                if(ois != null) ois.close();
            } catch (IOException ignore){
                Toast.makeText(context,"Error closing file",Toast.LENGTH_SHORT).show();
            }
        }
        return ret;
    }
    public boolean saveFile(File file, Object contents, boolean showToast){
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = new FileOutputStream(file);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(contents);
        } catch (IOException ignored){
            Toast.makeText(context, "Error Saving", Toast.LENGTH_SHORT).show();
            return false;
        } finally {
            try {
                if (fos != null) fos.close();
                if (oos != null) oos.close();
            }catch (IOException ignored){
                Toast.makeText(context,"Error closing file",Toast.LENGTH_SHORT).show();
            }
        }
        if(showToast) Toast.makeText(context,"Session saved",Toast.LENGTH_LONG).show();
        return true;
    }

    public boolean savePreset(PresetSettings settings, SessionType sessionType) {
        File userDirectory = new File(context.getFilesDir(), activeProfile.getName());
        if (!userDirectory.exists()) {
            userDirectory.mkdirs();
        }
        File sessionDirectory = new File(userDirectory,sessionType.toString());
        if (!sessionDirectory.exists()) {
            sessionDirectory.mkdirs();
        }
        File presetDirectory = new File(sessionDirectory,"preset");
        if (!presetDirectory.exists()) {
            presetDirectory.mkdirs();
        }
        // Create the file in the subdirectory
        File file = new File(presetDirectory, settings.getName());
        if(saveFile(file, settings, false)){
            Toast.makeText(context,"Saved",Toast.LENGTH_SHORT).show();
            return true;
        } else {
            return false;
        }

    }
    public PresetSettings getPreset(String name, SessionType sessionType){
        File presetFile = new File(context.getFilesDir()+"/"+sessionType.toString()+"/preset",name);
        return (PresetSettings) readFile(presetFile);
    }

    public ArrayList<PresetSettings> getAllPresets(SessionType sessionType) {
        File presetDirectory = new File(context.getFilesDir()+"/" + activeProfile.getName() + "/" +sessionType.toString()+"/preset");
        if (!presetDirectory.exists()) {
            presetDirectory.mkdirs();
        }

        File[] allFiles = null;
        if (presetDirectory.exists() && presetDirectory.isDirectory()) {
            // List all files in the directory
            allFiles = presetDirectory.listFiles();
        }
        ArrayList<PresetSettings> presets = new ArrayList<>();

        for(File file: allFiles){
            if(file.isFile()){
                presets.add((PresetSettings) readFile(file));
            }
        }
        return presets;
    }
}
