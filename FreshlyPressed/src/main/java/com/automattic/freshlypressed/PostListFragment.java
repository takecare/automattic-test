package com.automattic.freshlypressed;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.ParseException;

import org.json.JSONArray;

import androidx.fragment.app.ListFragment;
import okhttp3.OkHttpClient;

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
        PojoPost post = adapter.getItem(position);

        Intent browseIntent = new Intent();
        browseIntent.setAction(Intent.ACTION_VIEW);
        browseIntent.setData(post.getUri());

        getActivity().startActivity(browseIntent);
    }

    protected void updatePosts(JSONArray posts) {

        setListAdapter(new PostsAdapter(posts));
    }

    public void refreshPosts() {
        // TODO drop this thread. only here to get this to run @RUI
        new Thread(new Runnable() {
            @Override
            public void run() {
                final JSONArray arr = postsApi.loadPosts();
                // ((JSONObject)arr.get(0)).getString("featured_image")
                Handler mainH = new Handler(Looper.getMainLooper());
                mainH.post(new Runnable() {
                    @Override
                    public void run() {
                        updatePosts(arr);
                    }
                });
            }
        }).start();
//        updatePosts(postsApi.loadPosts());
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
        public PojoPost getItem(int position) {
            return PojoPost.build(mPostData.optJSONObject(position));
        }

        private boolean isNewDay(PojoPost currentItem, PojoPost previousItem) {
            try {
                long dayOfPreviousItem = previousItem.getDate().getTime() - 60000 * 60 * 24;
                return dayOfPreviousItem > currentItem.getDate().getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            PojoPost previousItem = position > 0 ? getItem(position - 1) : null;
            PojoPost post = getItem(position);
            boolean isNewDay = previousItem == null || isNewDay(post, previousItem);

            int layout = isNewDay ? R.layout.post_list_fragment_header_item : R.layout.post_list_fragment_item;
            final View itemView = getActivity().getLayoutInflater().inflate(layout, parent, false);

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

            ImageView imageView = itemView.findViewById(R.id.image);
            Glide.with(parent.getContext()).load(post.getImageUrl()).into(imageView);

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
