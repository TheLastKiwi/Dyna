package com.example.dyna;

import android.content.Context;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Objects;

public class FileManager {
    Context context;
    public FileManager(Context context){
        this.context = context;
    }

    /* TODO: Probably don't need to pass in profile to all these things
        Can just read from settings to get active profile
    */

    public void saveSession(Session s, Profile p){
        //
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;

        try {
            // Create or get the profile directory
            File profileDirectory = new File(context.getFilesDir(), p.name);
            if (!profileDirectory.exists()) {
                profileDirectory.mkdirs();
            }
            File sessionDirectory = new File(profileDirectory,s.sessionType.toString());
            if (!sessionDirectory.exists()) {
                sessionDirectory.mkdirs();
            }

            // Create the file in the subdirectory
            File file = new File(sessionDirectory, s.name);

            // Write the object to the file
            fos = new FileOutputStream(file);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(s);

        } catch (IOException e) {
            Log.d("Exception", Objects.requireNonNull(e.getMessage()));
        } finally {
            try {
                if (oos != null) oos.close();
                if (fos != null) fos.close();
            } catch (IOException e) {
                Log.d("Exception", Objects.requireNonNull(e.getMessage()));
            }
        }
    }
    public Session getSession(SessionType sessionType, Profile p, String name){
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        Session session = null;
        try {
            File profileDirectory = new File(context.getFilesDir(), p.name);
            if (!profileDirectory.exists()) {
                profileDirectory.mkdirs();
            }
            File sessionDirectory = new File(profileDirectory, sessionType.toString());
            if (!sessionDirectory.exists()) {
                sessionDirectory.mkdirs();
            }

            ///TODO THIS
            // Read the object from the file
            fis = new FileInputStream(name);
            ois = new ObjectInputStream(fis);
            session = (Session) ois.readObject();

        }  catch (IOException e) {
            Log.d("Exception", Objects.requireNonNull(e.getMessage()));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (ois != null) ois.close();
                if (fis != null) fis.close();
            } catch (IOException e) {
                Log.d("Exception", Objects.requireNonNull(e.getMessage()));
            }
        }
        return session;
    }
    public ArrayList<Session> getAllSessions(Context context, SessionType sessionType, Profile p) {

        // Get the directory (if it exists) or create it
        File profileDirectory = new File(context.getFilesDir(), p.name);
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
                try {
                    FileInputStream fis = new FileInputStream(file);
                    ObjectInputStream ois = new ObjectInputStream(fis);
                    Object object = ois.readObject();
                    Session s = (Session) object;
                    sessions.add(s);
                } catch (Exception e) {
                    Log.d("OH NO", Objects.requireNonNull(e.getMessage()));
                }
            }
        }
        return sessions;
    }
    public Profile getProfile(String userName) {

        Profile profile = null;
        FileInputStream fis = null;
        ObjectInputStream ois = null;

        try {
            File profileDirectory = new File(context.getFilesDir(), "profile");
            if (!profileDirectory.exists()) {
                Log.d("Uhoh","Profile directory not found");
                return null;
            }
            File userFile = new File(profileDirectory,userName);
            fis = new FileInputStream(userFile);
            ois = new ObjectInputStream(fis);
            profile = (Profile) ois.readObject();

        }  catch (IOException e) {
            Log.d("Exception", Objects.requireNonNull(e.getMessage()));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (ois != null) ois.close();
                if (fis != null) fis.close();
            } catch (IOException e) {
                Log.d("Exception", Objects.requireNonNull(e.getMessage()));
            }
        }
        return profile;
    }

    public void saveProfile(Profile profile) {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;

        try {
            // Create or get the profile directory
            File profileDirectory = new File(context.getFilesDir(), "profile");
            if (!profileDirectory.exists()) {
                profileDirectory.mkdirs();
            }
            // Create the file in the subdirectory
            File file = new File(profileDirectory, profile.name);

            // Write the object to the file
            fos = new FileOutputStream(file);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(profile);

        } catch (IOException e) {
            Log.d("Exception", Objects.requireNonNull(e.getMessage()));
        } finally {
            try {
                if (oos != null) oos.close();
                if (fos != null) fos.close();
            } catch (IOException e) {
                Log.d("Exception", Objects.requireNonNull(e.getMessage()));
            }
        }

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
                try {
                    FileInputStream fis = new FileInputStream(file);
                    ObjectInputStream ois = new ObjectInputStream(fis);
                    Object object = ois.readObject();
                    Profile p = (Profile) object;
                    profiles.add(p);
                } catch (Exception e) {
                    Log.d("OH NO", Objects.requireNonNull(e.getMessage()));
                }
            }
        }
        return profiles;
    }

}
