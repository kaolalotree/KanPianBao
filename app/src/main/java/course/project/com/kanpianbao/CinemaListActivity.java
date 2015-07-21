package course.project.com.kanpianbao;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import android.widget.ArrayAdapter;
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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.kpb.model.API;
import com.kpb.model.Cinema;
import com.kpb.model.Film;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CinemaListActivity extends Activity {
    private List<Cinema> cinemaList ;
    private RequestQueue mQueue;
    private int filmId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cinema_list);
        filmId=getIntent().getIntExtra("filmId",-1);
        mQueue = Volley.newRequestQueue(getApplicationContext());
        Map<String, String> params = new HashMap<String, String>();
        params.put("filmId", String.valueOf(filmId));
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, API.CINEMA_CHOOSE_URL, new JSONObject(params),
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("TAG", response.toString());
                        parseJson(response);
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
    public void parseJson(JSONObject response){
        try {
            JSONArray ja=response.getJSONArray("cinemas");
            Log.d("HEKK", ja.toString());
            cinemaList= JSON.parseArray(ja.toString(), Cinema.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

    public void initView(){
        CinemaAdapter adapter = new CinemaAdapter(CinemaListActivity.this, R.layout.cinema_item, cinemaList);
        ListView listView = (ListView) findViewById(R.id.cinema_list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cinema item = cinemaList.get(position);
                Intent intent=new Intent(CinemaListActivity.this,ScreenScheActivity.class);
                intent.putExtra("filmId",filmId);
                intent.putExtra("cinemaId",item.getCinemaId());
                startActivity(intent);
                finish();
            }
        });
    }

    public class CinemaAdapter extends ArrayAdapter<Cinema> {
        private int resourceId;

        public CinemaAdapter(Context context, int textViewResourceId,
                             List<Cinema> objects) {
            super(context, textViewResourceId, objects);
            resourceId = textViewResourceId;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Cinema item = getItem(position);
            View view;
            ViewHolder holder;
            if(convertView == null){
                view = LayoutInflater.from(getContext()).inflate(resourceId, null);
                holder = new ViewHolder();
                holder.cinemaImage = (ImageView) view.findViewById(R.id.cinema_image);
                holder.cinemaName = (TextView) view.findViewById(R.id.cinema_name);
                holder.cinemaIntro = (TextView) view.findViewById(R.id.cinema_intro);
                view.setTag(holder);
            }
            else{
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }
            loadImg(item.getCinemaImg(),holder.cinemaImage);
            holder.cinemaName.setText(item.getCinemaName());
            holder.cinemaIntro.setText(item.getCinemaIntro());
            return view;
        }
        class ViewHolder {
            ImageView cinemaImage;
            TextView cinemaName;
            TextView cinemaIntro;
        }
    }


}





