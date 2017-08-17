package org.schulcloud.mobile.util.crypt;

import android.util.Base64;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.UnsupportedEncodingException;


public class JWTUtil {
    private static final String KEY_USER_ID = "userId";

    public static String decodeToCurrentUser(String JWTEncoded) {
        try {
            String[] split = JWTEncoded.split("\\.");

            // get body of jwt
            JsonParser jsonParser = new JsonParser();
            String bodyJson = getJson(split[1]);
            JsonObject jsonObject = jsonParser.parse(bodyJson).getAsJsonObject();

            return jsonObject.get(KEY_USER_ID).getAsString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getJson(String strEncoded) throws UnsupportedEncodingException {
        byte[] decodedBytes = Base64.decode(strEncoded, Base64.URL_SAFE);
        return new String(decodedBytes, "UTF-8");
    }
}
