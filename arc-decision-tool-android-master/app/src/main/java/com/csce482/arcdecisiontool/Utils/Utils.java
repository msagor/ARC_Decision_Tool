package com.csce482.arcdecisiontool.Utils;

import com.csce482.arcdecisiontool.Models.Hurricane;
import com.csce482.arcdecisiontool.Models.Task;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * A set of global static functions and arrays for usage throughout the application.
 */
public class Utils {
    private static Realm realm;

    /**
    * This array of strings are just a list of all the
    *  tasks that depends on user input values.
    * */
    public static final String [] inputtedTasks = {
            "Airport Close Times",
            "Gale Force Wind Arrival",
            "Lead Time to Open Shelters",
            "Hunker Down Time",
            "Expected Re-Entry Time",
            "Conference Call for Region 1",
            "Conference Call for Region 2",
            "Conference Call for NHQ 1",
            "Conference Call for NHQ 2"
    };

    /**
    * id numbers of tasks from the hurricane object which are
    * high priority tasks.
    * */
    private static final String [] highPriorityTaskIDs = {
            "db58223a-2155-11e8-b467-0ed5f89f718b",
            "0acdcc7c-2156-11e8-b467-0ed5f89f718b"
    };


    /**
     * Checks if a task's name contains the substring 'Conference Call'.
     * @param task
     * @return Returns true if the task name contains the substring 'Conference Call', false otherwise.
     */
    public static boolean isConferenceCall(Task task){
        return task.getName().contains("Conference Call");
    }


    public static final Date [] inputtedTasksDates = new Date[inputtedTasks.length];

    /**
     * Initializes the array of inputted task dates to a non-null value.
     *
     */
    public static void init() {
        for (int i = 0 ; i < inputtedTasks.length; i++) {
            inputtedTasksDates[i] = new Date();
        }
    }

    /**
    *
     * Checks if a task's ID string is present in the array of high priority task IDs.
     * @param task
     * @return True if a task's ID string is present in the array of high priority task IDs, false otherwise.
     */
    public static boolean isHighPriority(Task task) {
        for (int i = 0; i < highPriorityTaskIDs.length; i++){
            if (task.getId().equals(highPriorityTaskIDs[i])) return true;
        }
        return false;
    }

    /**
    *
     * Checks if the current time is within an hour of expiration or past the expiration of the timeline passed to the function.
     * For example, if the parameter timeline was "96", this function will subtract the
     * date of landfall from the date at this moment and return true if the difference in hours
     * is less than or equal to 73 (72-48 being the next timeline phase).
     * @param timeline The timeline in question. the allowed values are "120", "96", "7248", "24", "0" for landfall, and "-1" for post landfall.
     * @return True if the current time is within an hour of expiration or past the expiration of the timeline passed to the function.
     */
    public static boolean timelineAboutToOrHasExpired(String timeline) {
        Date startTime = new Date();
        long diffMillis = Utils.getHurricane().getLandfall().getTime() - startTime.getTime();
        long diffHours = TimeUnit.MILLISECONDS.toHours(diffMillis);
        switch (timeline) {
            case "120":
                return diffHours < 97;
            case "96":
                return diffHours < 73;
            case "7248":
                return diffHours < 25;
            case "24":
                return diffHours < 1;
            case "0":
                return false;
            case "-1":
                return false;
            default:
                return false;
        }
    }


    /**
     * Determines the current timeline based on the difference between the landfall date and the current date.
     * @return "120" if the difference is greater than 96, "96" if it is greater than 72, "7248" if it is greater than 24, "24" if it is greater than 0, "0" if it is equal to 0, "-1" otherwise.
     */
    public static String currentTimeline(){
        Date startTime = new Date();
        long diffMillis = Utils.getHurricane().getLandfall().getTime() - startTime.getTime();
        long diffHours = TimeUnit.MILLISECONDS.toHours(diffMillis);
        if (diffHours > 96) return "120";
        if (diffHours > 72) return "96";
        if (diffHours > 24) return "7248";
        if (diffHours > 0) return "24";
        if (diffHours == 0) return "0";
        return "-1";
    }


    /**
     * Checks the number of milliseconds left until "time".
     * @param time The destination date, represented as a number of milliseconds since 00:00:00 Thursday, 1 January 1970.
     * @return Returns the number of milliseconds from now until the "time" parameter, which is a date represented in milliseconds since 00:00:00 Thursday, 1 January 1970.
     */
    public static long millisUntil(long time){
        return time - new Date().getTime();
    }


    /**
     * Check how many milliseconds are left until a timeline expired.
     * @param timeline The timeline in question. the allowed values are "120", "96", "7248", "24", "0" for landfall, and "-1" for post landfall.
     * @return Returns the number of milliseconds left before "timeline" expires.
     */
    public static long millisUntilTimelineExpires(String timeline){
        String nextTimeline;
        switch (timeline) {
            case "120":
                nextTimeline = "96";
                break;
            case "96":
                nextTimeline = "7248";
                break;
            case "7248":
                nextTimeline = "24";
                break;
            case "24":
                nextTimeline = "0";
                break;
            case "0":
                nextTimeline = "-1";
                break;
            case "-1":
                return 0;
            default:
                return 0;
        }
        return millisAtTimelineStart(nextTimeline) - System.currentTimeMillis();
    }


    /**
     * Checks the date at the start of a timeline.
     * @param timeline The timeline in question. the allowed values are "120", "96", "7248", "24", "0" for landfall, and "-1" for post landfall.
     * @return The date of when a timeline phase starts. For example, if the timeline in question is 96, returns the exact time 96 hours before landfall.
     */
    public static long millisAtTimelineStart(String timeline){
        if (!Utils.hurricaneEventExists()) return 0;
        long landFall = Utils.getHurricane().getLandfall().getTime();
        switch (timeline){
            case "120":
                return new Date(landFall - TimeUnit.HOURS.toMillis(120)).getTime();
            case "96":
                return new Date(landFall - TimeUnit.HOURS.toMillis(96)).getTime();
            case "7248":
                return new Date(landFall - TimeUnit.HOURS.toMillis(72)).getTime();
            case "24":
                return new Date(landFall - TimeUnit.HOURS.toMillis(24)).getTime();
            case "0":
                return landFall;
            case "-1":
                return new Date(landFall + TimeUnit.HOURS.toMillis(1)).getTime();
            default:
                return 0;
        }
    }

    /**
     * Checks if a timeline has expired.
     * @param timeline The timeline in question. the allowed values are "120", "96", "7248", "24", "0" for landfall, and "-1" for post landfall.
     * @return Returns true if a timeline phase is over. For example, if "timeline" is "120", returns true if there are less than or equal to 95 hours left until landfall.
     */
    public static boolean timelineHasExpired(String timeline) {
        Date startTime = new Date();
        long diffMillis = Utils.getHurricane().getLandfall().getTime() - startTime.getTime();
        long diffHours = TimeUnit.MILLISECONDS.toHours(diffMillis);
        switch (timeline) {
            case "120":
                return diffHours <= 95;
            case "96":
                return diffHours <= 71;
            case "7248":
                return diffHours <= 23;
            case "24":
                return diffHours <= -1;
            case "0":
                return false;
            case "-1":
                return false;
            default:
                return false;
        }
    }

    /**
     * Checks if a task is one of the tasks described in the input screen (by seeing if its name is present in the static Utils.inputtedTasks array).
     * @param task
     * @return Returns the index in inputtedTasks that tasks corresponds to, returns -1 otherwise.
     */
    public static int isInputtedTask(Task task) {
        realm = realm.getDefaultInstance();
        for (int i = 0; i < inputtedTasks.length; i++){
            if (task.getName().equals(inputtedTasks[i])) {
                return i;
            }
        }
        return -1;
    }


    /**
     * Checks if a hurricane event exists.
     * @return True if a hurricane object is in realm, false otherwise.
     */
    public static boolean hurricaneEventExists(){
        realm = realm.getDefaultInstance();
        return realm.where(Hurricane.class).findFirst() != null;
    }



    /**
     * Get the hurricane event data.
     * @return Returns the hurricane object stored in realm.
     */
    public static Hurricane getHurricane() {
        realm = realm.getDefaultInstance();
        return realm.where(Hurricane.class).findFirst();
    }



    /**
     * Deletes the hurricane event in realm and sets all tasks to incomplete.
     */
    public static void resetEvent() {
        deleteHurricane();
        resetTasks();
    }



    /**
     * Delete the huricane event in realm.
     */
    public static void deleteHurricane(){
        realm = Realm.getDefaultInstance();
        RealmResults<Hurricane> results = realm.where(Hurricane.class).findAll();
        realm.beginTransaction();
        results.deleteAllFromRealm();
        realm.commitTransaction();
    }



    /**
     * Sets all tasks in realm to incomplete.
     */
    public static void resetTasks(){
        realm = Realm.getDefaultInstance();
        RealmResults<Task> results = realm.where(Task.class).findAll();
        realm.beginTransaction();
        for (int i = 0; i < results.size(); i++) {
            results.get(i).setCompleted(false);
        }
        realm.commitTransaction();
    }



    /**
     * Check how many tasks are complete.
     * @return The number of tasks in realm that have "isCompleted" marked as true.
     */
    public static long completedTasksCount() {
        realm = Realm.getDefaultInstance();
        return realm.where(Task.class).equalTo("isCompleted", true).count();
    }



    /**
     * Check how many tasks are incomplete.
     * @return The number of tasks in realm that have "isCompleted" marked as false.
     */
    public static long incompleteTasksCount() {
        realm = Realm.getDefaultInstance();
        return realm.where(Task.class).equalTo("isCompleted", false).count();
    }



    /**
     * Return number of tasks in timeline.
     * @param timeline The timeline in question. the allowed values are "120", "96", "7248", "24", "0" for landfall, and "-1" for post landfall.
     * @return The number of tasks whose "timeline" value is equal to the parameter's value.
     */
    public static long numTasksInTimeline(String timeline) {
        realm = Realm.getDefaultInstance();
        return realm.where(Task.class).equalTo("timeline", timeline).count();
    }



    /**
     * Return number of tasks in timeline where their "isCompleted" value is equal to the isCompleted parameter.
     * @param timeline The timeline in question. the allowed values are "120", "96", "7248", "24", "0" for landfall, and "-1" for post landfall.
     * @param isCompleted
     * @return If isCompleted is true, return number of tasks in timeline that are completed, otherwise return number of tasks in timeline that are incomplete.
     */
    public static long numTasksInTimeline(String timeline, boolean isCompleted) {
        realm = Realm.getDefaultInstance();
        return realm.where(Task.class).equalTo("timeline", timeline).and().equalTo("isCompleted", isCompleted).count();
    }



    /**
     * Formats a date object into a printable string.
     * @param date
     * @return Returns a String of a Date object represented in "MM/dd/yyyy at hh:mm a".
     */
    public static String formattedDate(Date date) {
        return new SimpleDateFormat("MM/dd/yyyy 'at' hh:mm a").format(date);
    }



    /**
     * Formats a date into a printable string (only the time).
     * @param date
     * @return Returns a String of a Date object represented in "hh:mm a".
     */
    public static String getTimeFormatted(Date date) {
        return new SimpleDateFormat("hh:mm a").format(date);
    }



    /**
     * Return a date formatted as a string representation of a day.
     * @param date
     * @return The date in "MM/dd/yyyy" format.
     */
    public static String getDayFormatted(Date date) {
        return new SimpleDateFormat("MM/dd/yyyy").format(date);
    }



    /**
     * Return a date formatted as a string representation of hours, minutes and seconds.
     * @param date
     * @return The date in "hh:mm:ss" format.
     */
    public static String hmsFormat(Date date) {
        long millis = date.getTime();
        return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
    }



    /**
     * Return a date represented in milliseconds since 01/01/1970 formatted as a string representation of hours, minutes and seconds.
     * @param millis
     * @return The date in "hh:mm:ss" format.
     */
    public static String hmsFormat(long millis) {
        return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
    }



    /**
     * Gets the corresponding timeline a certain amount of hours from now.
     * @param hoursFromNow A number of hours.
     * @return Returns the corresponding timeline phase of the time hoursFromNow. For example if 12 hours from now we will be in the 24 hour timeline, return "24".
     */
    public static String getCorrespondingTimeline(long hoursFromNow){
        long msTilLandfall = Utils.getHurricane().getLandfall().getTime() - (new Date()).getTime();
        long hoursTilLandfall = TimeUnit.MILLISECONDS.toHours(msTilLandfall);
        hoursFromNow = hoursTilLandfall - hoursFromNow;
        if (hoursFromNow > 96) return "120";
        if (hoursFromNow > 72) return "96";
        if (hoursFromNow > 24) return "7248";
        if (hoursFromNow > 0) return "24";
        if (hoursFromNow == 0) return "0";
        return "-1";
    }
}
