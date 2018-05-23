package com.csce482.arcdecisiontool.Controllers.Main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

import com.csce482.arcdecisiontool.Controllers.Input.InputActivity;
import com.csce482.arcdecisiontool.Models.Hurricane;
import com.csce482.arcdecisiontool.Models.Task;
import com.csce482.arcdecisiontool.R;
import com.csce482.arcdecisiontool.Controllers.Timeline.TimelineActivity;
import com.csce482.arcdecisiontool.Utils.Utils;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * This activity sends the user to either TimelineActivity or InputActivity based on the presence of a Hurricane event in Realm.
 */
public class MainActivity extends Activity{

    private Realm realm;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Utils.init();
        setContentView(R.layout.activity_main );
        setUpRealm();
        createConferenceCalls();
        if (Utils.hurricaneEventExists()){

            refreshScheduledDates();
            Intent timelineIntent = new Intent(this, TimelineActivity.class);
            timelineIntent.putExtra("hurricane_obj_id", Utils.getHurricane().getId());
            startActivity(timelineIntent);
        }
        else {
            Intent intent = new Intent(this, InputActivity.class);
            startActivity(intent);
        }
    }

    /**
     * Checks the realm Task table for the presence of conference call tasks in other timelines. If
     * they do not exist, generates 20 more conference calls, four for each timeline (except landfall).
     * This only happens on the first time the application runs.
     */
    private void createConferenceCalls() {
        RealmResults<Task> conferenceCalls = realm.where(Task.class).contains("name", "Conference Call").findAll();

        if (conferenceCalls.size() == 4) {
            System.out.println("Creating conference calls...");
            String [] missingTimelines = {"120", "96", "7248", "24", "-1"};
            for (int i = 0; i < missingTimelines.length; i++){
                for (int j = 0; j < 4; j++){
                    Task task = new Task();
                    UUID idOne = UUID.randomUUID();
                    task.setId(idOne.toString());
                    task.setName(conferenceCalls.get(j).getName());
                    task.setCompleted(false);
                    task.setCompletionDate(new Date());
                    task.setTimeline(missingTimelines[i]);
                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(task);
                    realm.commitTransaction();
                }
            }
        }
    }

    /**
     * Loads Hurricane's associated input times from Realm into the corresponding slots in Utils.inputtedTasksDates.
     */
    public static void refreshScheduledDates() {
        Hurricane h = Utils.getHurricane();
        Utils.inputtedTasksDates[0] = h.getAirportsCloseTime();
        Utils.inputtedTasksDates[1] = h.getGaleForceWindArrive();
        Utils.inputtedTasksDates[2] = h.getLeadTimeToOpenShelters();
        Utils.inputtedTasksDates[3] = h.getHunkerDownTime();
        Utils.inputtedTasksDates[4] = h.getExpectedReEntryTime();
        Utils.inputtedTasksDates[5] = h.getConferenceCallTimesForRegion().first();
        Utils.inputtedTasksDates[6] = h.getConferenceCallTimesForRegion().last();
        Utils.inputtedTasksDates[7] = h.getConferenceCallTimesForNHQ().first();
        Utils.inputtedTasksDates[8] = h.getConferenceCallTimesForNHQ().last();
    }

    public void createNewEventClick(View view) {
        //from first screen- when user clicks to create a new event
        Intent intent = new Intent(this, InputActivity.class);
        startActivity(intent);
    }


    /**
     * In the application there are two Realm files. Firstly, the bundled realm file called "tasks.realm" which exists
     * in res/raw. The bundled realm file never changes, it comes with the application. The first time the application
     * runs on the phone, a copy of the bundled file is made and stored in the application's data folder in the Android.
     */
    private void setUpRealm() {
        boolean firstTime = false;
        //check if the db is already in place
        if (!fileFound("tasks.realm", this.getFilesDir())){
            copyBundledRealmFile(this.getResources().openRawResource(R.raw.tasks), "tasks.realm");
            firstTime = true;
        }

        //Config Realm for the application
        Realm.init(this);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .name("tasks.realm")
                .schemaVersion(0)
                .build();

        Realm.setDefaultConfiguration(realmConfiguration);
        realm = Realm.getDefaultInstance();
        if (firstTime) Utils.deleteHurricane(); //The bundled realm file comes with a sample Hurricane, which is unneeded in the application.
    }

    /**
     * Makes a copy of the bundled realm file in res/raw to the application's corresponding data folder.
     * The bundled file remains the same, the copy is what the application operates on.
     * @param inputStream An input stream to read from the bundled realm file res/raw/tasks.realm.
     * @param outFileName The name of the copy of the bundled realm file being made within the Android's corresponding data folder for this application.
     * @return Unused.
     */
    private String copyBundledRealmFile(InputStream inputStream, String outFileName) {
        try {
            File file = new File(this.getFilesDir(), outFileName);
            FileOutputStream outputStream = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buf)) > 0) {
                outputStream.write(buf, 0, bytesRead);
            }
            outputStream.close();
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Checks to see if the copy of the bundled realm file exists in the Android application's data folder.
     * This is used to determine whether or not a copy of the bundled file should be made.
     * This function should only return false on the first time the application is started after its installation.
     * @param name
     * @param file
     * @return
     */
    public boolean fileFound(String name, File file) {
        File[] list = file.listFiles();
        if (list != null)
            for (File fil : list) {
                if (fil.isDirectory()) {
                    fileFound(name, fil);
                } else if (name.equalsIgnoreCase(fil.getName())) {
                    return true;
                }
            }
        return false;
    }



}
