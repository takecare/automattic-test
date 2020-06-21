package com.automattic.freshlypressed;

import android.net.Uri;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONObject;

public class PojoPost {
    private final JSONObject mJson;

    public static PojoPost build(JSONObject json) {
        return new PojoPost(json);
    }

    public PojoPost(JSONObject json) {
        mJson = json;
    }

    public CharSequence getTitle() {
        return mJson.optString("title");
    }

    public CharSequence getExcerpt() {
        return mJson.optString("excerpt");
    }

    public CharSequence getAuthor() {
        return mJson.optJSONObject("author").optString("name");
    }

    public String getImageUrl() {
        return mJson.optString("featured_image");
    }

    public Date getDate() throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        return dateFormat.parse(mJson.optString("date"));
    }

    public Uri getAuthorUrl() {
        return Uri.parse(mJson.optJSONObject("author").optString("URL"));
    }

    public Uri getUri() {
        return Uri.parse(mJson.optString("URL"));
    }
}
