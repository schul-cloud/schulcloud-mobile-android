package org.schulcloud.mobile.data.local;

import android.content.Context;
import android.content.SharedPreferences;

import org.schulcloud.mobile.data.model.AccessToken;
import org.schulcloud.mobile.injection.ApplicationContext;
import org.schulcloud.mobile.util.crypt.ObscuredSharedPreferences;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PreferencesHelper {
    private static final String PREF_FILE_NAME = "schulcloud_pref_file";

    public static final String PREFERENCE_ACCESS_TOKEN = "jwt";
    public static final String PREFERENCE_USER_ID = "currentUser";
    public static final String PREFERENCE_USERNAME = "username";
    public static final String PREFERENCE_MESSAGING_TOKEN = "messagingToken";
    public static final String PREFERENCE_STORAGE_CONTEXT = "storageContext";
    public static final String PREFERENCE_CALENDAR_SYNC_ENABLED = "calendarSyncEnabled";
    public static final String PREFERENCE_CALENDAR_SYNC_NAME = "calendarSyncName";

    private final ObscuredSharedPreferences mPref;

    @Inject
    public PreferencesHelper(@ApplicationContext Context context) {
        mPref = ObscuredSharedPreferences.getPrefs(context, PREF_FILE_NAME, Context.MODE_PRIVATE);
    }

    // ##### Setter

    /**
     * saves a accessToken to the shared prefs
     *
     * @param accessToken {AccessToken} - the accessToken which will be saved
     * @return the saved accessToken
     */
    public String saveAccessToken(AccessToken accessToken) {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString(PREFERENCE_ACCESS_TOKEN, accessToken.accessToken);
        if (editor.commit()) {
            return accessToken.getAccessToken();
        }

        return null;
    }

    /**
     * saves a userId to the shared prefs
     *
     * @param userId {String} - the userId which will be saved
     * @return the saved userId
     */
    public String saveCurrentUserId(String userId) {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString(PREFERENCE_USER_ID, userId);
        if (editor.commit()) {
            return userId;
        }

        return null;
    }

    public String saveCurrentUsername(String username) {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString(PREFERENCE_USERNAME, username);
        if (editor.commit()) {
            return username;
        }

        return null;
    }

    public String saveCurrentSchoolId(String schoolId) {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString("currentSchool", schoolId);

        if (editor.commit())
            return schoolId;
        return null;
    }

    public String saveMessagingToken(String tokenId) {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString(PREFERENCE_MESSAGING_TOKEN, tokenId);
        if (editor.commit()) {
            return tokenId;
        }

        return null;
    }

    public String saveCurrentStorageContext(String path) {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString(PREFERENCE_STORAGE_CONTEXT, path);
        if (editor.commit()) {
            return path;
        }

        return null;
    }

    public Boolean saveCalendarSyncEnabled(Boolean isEnabled) {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putBoolean(PREFERENCE_CALENDAR_SYNC_ENABLED, isEnabled);
        if (editor.commit()) {
            return isEnabled;
        }

        return null;
    }

    public String saveCalendarSyncName(String calendarName) {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString(PREFERENCE_CALENDAR_SYNC_NAME, calendarName);
        if (editor.commit()) {
            return calendarName;
        }

        return null;
    }

    // ##### Getter

    public String getAccessToken() {
        return mPref.getString(PREFERENCE_ACCESS_TOKEN, "null");
    }

    public String getCurrentUserId() {
        return mPref.getString(PREFERENCE_USER_ID, "null");
    }

    public String getCurrentUsername() {
        return mPref.getString(PREFERENCE_USERNAME, "null");
    }

    public String getMessagingToken() {
        return mPref.getString(PREFERENCE_MESSAGING_TOKEN, "null");
    }

    public String getCurrentSchoolId() {
        return mPref.getString("currentSchool", "null");
    }

    public String getCurrentStorageContext() {
        return mPref.getString(PREFERENCE_STORAGE_CONTEXT, "null");
    }

    public Boolean getCalendarSyncEnabled() {
        return mPref.getBoolean(PREFERENCE_CALENDAR_SYNC_ENABLED, false);
    }

    public String getCalendarSyncName() {
        return mPref.getString(PREFERENCE_CALENDAR_SYNC_NAME, "null");
    }

    // ##### Clearing

    public void clear() {
        mPref.edit().clear().apply();
    }

    /**
     * clears a pref for a given key
     *
     * @param key {String} - the key of the param which will be deleted
     */
    public void clear(String key) {
        mPref.edit().remove(key).apply();
    }

}
