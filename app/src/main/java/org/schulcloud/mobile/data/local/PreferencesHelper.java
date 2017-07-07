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

    public static final String PREF_FILE_NAME = "schulcloud_pref_file";

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
        // todo: save pref-keys in strings.xml or constant
        editor.putString("jwt", accessToken.accessToken);
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
        // todo: save pref-keys in strings.xml or constant
        editor.putString("currentUser", userId);
        if (editor.commit()) {
            return userId;
        }

        return null;
    }

    public String saveCurrentUsername(String username) {
        SharedPreferences.Editor editor = mPref.edit();

        editor.putString("username", username);

        if (editor.commit()) {
            return username;
        }

        return null;
    }

    public String saveMessagingToken(String tokenId) {
        SharedPreferences.Editor editor = mPref.edit();

        editor.putString("messagingToken", tokenId);
        if (editor.commit()) {
            return tokenId;
        }

        return null;
    }

    public String saveCurrentStorageContext(String path) {
        SharedPreferences.Editor editor = mPref.edit();

        editor.putString("storageContext", path);
        if (editor.commit()) {
            return path;
        }

        return null;
    }

    public Boolean saveCalendarSyncEnabled(Boolean isEnabled) {
        SharedPreferences.Editor editor = mPref.edit();

        editor.putBoolean("calendarSyncEnabled", isEnabled);
        if (editor.commit()) {
            return isEnabled;
        }

        return null;
    }

    public String saveCalendarSyncName(String calendarName) {
        SharedPreferences.Editor editor = mPref.edit();

        editor.putString("calendarSyncName", calendarName);
        if (editor.commit()) {
            return calendarName;
        }

        return null;
    }

    // ##### Getter

    public String getAccessToken() {
        return mPref.getString("jwt", "null");
    }

    public String getCurrentUserId() {
        return mPref.getString("currentUser", "null");
    }

    public String getMessagingToken() {
        return mPref.getString("messagingToken", "null");
    }

    public String getCurrentUsername() {
        return mPref.getString("username", "null");
    }

    public String getCurrentStorageContext() {
        return mPref.getString("storageContext", "null");
    }

    public Boolean getCalendarSyncEnabled() {
        return mPref.getBoolean("calendarSyncEnabled", false);
    }

    public String getCalendarSyncName() {
        return mPref.getString("calendarSyncName", "null");
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
