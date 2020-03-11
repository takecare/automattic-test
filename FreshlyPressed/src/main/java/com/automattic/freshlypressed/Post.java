package com.automattic.freshlypressed;

import android.net.Uri;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Post {
    private final JSONObject mJson;

    public static Post build(JSONObject json) {
        return new Post(json);
    }

    public Post(JSONObject json) {
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
