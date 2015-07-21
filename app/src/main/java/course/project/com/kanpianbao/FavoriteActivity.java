package course.project.com.kanpianbao;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.kpb.model.API;
import com.kpb.model.Favorite;
import com.kpb.model.Film;
import com.kpb.model.Order;
import com.kpb.model.ScreenSche;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FavoriteActivity extends Activity {

    private List<Film> movieList;
    private List<Favorite> favoriteList;
    private ListView listView;
    FavAdapter adapter;
    private RequestQueue mQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        mQueue = Volley.newRequestQueue(getApplicationContext());

        getFavorites();

    }
    public void getFavorites(){

        Map<String, String> params = new HashMap<String, String>();
        SharedPreferences sharedPreferences=getSharedPreferences("account", Context.MODE_PRIVATE);
        int userId= sharedPreferences.getInt("userId",-1);
        params.put("userId",String.valueOf(userId));
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, API.GET_FAVORITES_URL, new JSONObject(params),
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("TAG", response.toString());
                        parseFavJson(response);
                        initView();
                    }
                },
                new Response.ErrorListener(){

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("TAG", error.getMessage(), error);
                    }
                });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(50 * 1000, 1, 1.0f));
        mQueue.add(jsonObjectRequest);
    }

    public void parseFavJson(JSONObject response){
        try {
            JSONArray ja=response.getJSONArray("favorites");
            favoriteList= JSON.parseArray(ja.toString(), Favorite.class);
            JSONArray ja2=response.getJSONArray("films");
            movieList=JSON.parseArray(ja2.toString(),Film.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void initView(){
        adapter = new FavAdapter(FavoriteActivity.this, R.layout.fav_item, movieList);
        listView = (ListView) findViewById(R.id.movie_list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Film item = movieList.get(position);
                Intent intent = new Intent(getApplicationContext(), MovieDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("film", item);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    public class FavAdapter extends ArrayAdapter<Film> {
        private int resourceId;

        public FavAdapter(Context context, int textViewResourceId,
                          List<Film> objects) {
            super(context, textViewResourceId, objects);
            resourceId = textViewResourceId;
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            Film item = getItem(position);
            View view;
            ViewHolder holder;
            if(convertView == null){
                view = LayoutInflater.from(getContext()).inflate(resourceId, null);
                holder = new ViewHolder();
                holder.movieImage = (ImageView) view.findViewById(R.id.movie_image);
                holder.movieName = (TextView) view.findViewById(R.id.movie_name);
                holder.movieIntro = (TextView) view.findViewById(R.id.movie_intro);
                holder.del_btn = (Button) view.findViewById(R.id.del_btn);
                view.setTag(holder);
            }
            else{
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }
            loadImg(item.getFilmImg(),holder.movieImage);
            holder.movieName.setText(item.getFilmName());
            holder.movieIntro.setText(item.getFilmIntro().substring(0,15)+"...");
            holder.del_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    del_alert(position);
                }
            });
            return view;
        }
        class ViewHolder {
            ImageView movieImage;
            TextView movieName;
            TextView movieIntro;
            Button del_btn;
        }
    }

    public void del_alert(final int position){
        new AlertDialog.Builder(this).setTitle("��ʾ").setMessage("ȷ��ɾ���ղأ�").setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        }).setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Film item = movieList.get(position);
                Map<String, String> params = new HashMap<String, String>();
                params.put("filmId", String.valueOf(item.getFilmId()));
                SharedPreferences sharedPreferences=getSharedPreferences("account", Context.MODE_PRIVATE);
                int userId= sharedPreferences.getInt("userId",-1);
                params.put("userId",String.valueOf(userId));
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, API.CANCEL_FAVORITE_URL, new JSONObject(params),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d("TAG", response.toString());
                                try {
                                    if (response.getBoolean("status")) {
                                        movieList.remove(position);
                                        listView.setAdapter(adapter);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("TAG", error.getMessage(), error);
                            }
                        });
                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(50 * 1000, 1, 1.0f));
                mQueue.add(jsonObjectRequest);
            }
        }).show();
    }
    public void loadImg(String url,ImageView postView){
        final ImageView imageView=postView;
        ImageRequest imageRequest = new ImageRequest(
                url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        imageView.setImageBitmap(response);
                    }
                }, 0, 0, Bitmap.Config.RGB_565, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                imageView.setImageResource(R.drawable.empty_photo);
            }
        });
        mQueue.add(imageRequest);
    }

}
