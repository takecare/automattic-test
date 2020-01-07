package com.automattic.freshlypressed;

import android.app.ListFragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.util.function.Consumer;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PostListFragment extends ListFragment implements AdapterView.OnItemClickListener {
    PostsApi postsApi = new PostsApi(new OkHttpClient());
    // empty constructor necessary for fragments
    public PostListFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();

        refreshPosts();

        getListView().setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        PostsAdapter adapter = (PostsAdapter) getListAdapter();
        Post post = adapter.getItem(position);

        Intent browseIntent = new Intent();
        browseIntent.setAction(Intent.ACTION_VIEW);
        browseIntent.setData(post.getUri());

        getActivity().startActivity(browseIntent);
    }

    protected void updatePosts(JSONArray posts) {

        setListAdapter(new PostsAdapter(posts));
    }

    public void refreshPosts() {
        updatePosts(postsApi.loadPosts());
    }

    private class PostsAdapter extends BaseAdapter {
        private final JSONArray mPostData;

        public PostsAdapter(JSONArray postData) {
            if (postData == null) {
                throw new IllegalArgumentException("postData must not be null");
            }

            mPostData = postData;
        }

        @Override
        public Post getItem(int position) {
            return Post.build(mPostData.optJSONObject(position));
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            Post previousItem = position > 0 ? getItem(position - 1) : null;
            Post post = getItem(position);
            boolean isNewDay = false;
            try {
                isNewDay = previousItem == null || (previousItem.getDate().getTime() - 60000 * 60 * 24) > post.getDate().getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            final View itemView = getActivity().getLayoutInflater()
                    .inflate(!isNewDay ? R.layout.post_list_fragment_item : R.layout.post_list_fragment_header_item, parent, false);

            if (isNewDay) {
                TextView header = itemView.findViewById(R.id.header);
                try {
                    header.setText(PostUtils.INSTANCE.printDate(post.getDate()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            TextView title = itemView.findViewById(R.id.title);
            TextView summary = itemView.findViewById(R.id.summary);
            TextView author = itemView.findViewById(R.id.author_name);


            title.setText(post.getTitle());
            summary.setText(Html.fromHtml(post.getExcerpt().toString()));
            author.setText(post.getAuthor());

            new LoadCountTask(new LoadCountCallback() {
                @Override
                public void run(int param) {
                    TextView postCount = itemView.findViewById(R.id.subscribers_count);
                    postCount.setText(String.valueOf(param));
                }
            }).execute(post.getAuthorUrl().getHost());

            return itemView;
        }

        @Override
        public long getItemId(int position) {
            return -1L;
        }

        @Override
        public int getCount() {
            return mPostData.length();
        }
    }

    private class LoadCountTask extends AsyncTask<String, Void, Integer> {

        private LoadCountCallback callback;

        LoadCountTask(LoadCountCallback callback) {
            this.callback = callback;
        }

        @Override
        protected Integer doInBackground(String... strings) {
            String blogUrl = strings[0];
            return postsApi.loadSubscribersCount(blogUrl);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            callback.run(integer);
        }
    }

    interface LoadCountCallback {
        void run(int param);
    }
}
