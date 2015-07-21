package course.project.com.kanpianbao;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;



public class PersonFragment extends Fragment {
    private LinearLayout collectbtn;
    private LinearLayout orderbtn;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_person, container,false);
        collectbtn = (LinearLayout) view.findViewById(R.id.user_collection_button);
        orderbtn = (LinearLayout) view.findViewById(R.id.user_order_button);
        collectbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent=new Intent(getActivity(),FavoriteActivity.class);
                startActivity(intent);
            }

        });

        orderbtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),OrderListActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }

}
