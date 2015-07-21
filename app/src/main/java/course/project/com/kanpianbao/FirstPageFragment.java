package course.project.com.kanpianbao;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.kpb.model.API;
import com.kpb.model.Film;
import org.json.JSONArray;
import java.util.List;

public class FirstPageFragment extends Fragment {
    private List<Film> movieList ;
    MainPageAdapter adapter;
    public RequestQueue mQueue;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_first_page, container,false);
        mQueue = Volley.newRequestQueue(getActivity());
        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(API.GET_RECOMMENDMOVIES_URL,
                new Response.Listener<JSONArray>(){

                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("TAG", response.toString());
                        parseJson(response);
                        initView(view);
                    }
                },
                new Response.ErrorListener(){

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("TAG", error.getMessage(), error);
                    }
                });

        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(50 * 1000, 1, 1.0f));
        mQueue.add(jsonArrayRequest);
        return view;
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
    public void parseJson(JSONArray response){
        movieList= JSON.parseArray(response.toString(),Film.class);
    }

    public void initView(View view){

        MainPageAdapter adapter = new MainPageAdapter(getActivity(), R.layout.movie_item, movieList);
        ListView listView = (ListView)view.findViewById(R.id.fp_movie_list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Film item = movieList.get(position);
                Intent intent = new Intent(getActivity(), MovieDetailActivity.class);
                Bundle bundle=new Bundle();
                bundle.putSerializable("film",item);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
    public class MainPageAdapter extends ArrayAdapter<Film> {
        private int resourceId;

        public MainPageAdapter(Context context, int textViewResourceId,
                               List<Film> objects) {
            super(context, textViewResourceId, objects);
            resourceId = textViewResourceId;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Film item = getItem(position);
            View view;
            ViewHolder holder;
            if(convertView == null){
                view = LayoutInflater.from(getContext()).inflate(resourceId, null);
                holder = new ViewHolder();
                holder.movieImage = (ImageView) view.findViewById(R.id.movie_image);
                holder.movieName = (TextView) view.findViewById(R.id.movie_name);
                holder.movieIntro = (TextView) view.findViewById(R.id.movie_intro);
                view.setTag(holder);
            }
            else{
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }
            loadImg(item.getFilmImg(),holder.movieImage);
            holder.movieName.setText(item.getFilmName());
            holder.movieIntro.setText(item.getFilmIntro().substring(0,15)+"...");
            return view;
        }
        class ViewHolder {
            ImageView movieImage;
            TextView movieName;
            TextView movieIntro;
        }
    }

}
