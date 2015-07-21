package course.project.com.kanpianbao;


import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class MainActivity extends FragmentActivity implements View.OnClickListener{

    private FirstPageFragment fg1;
    private MovieFragment fg2;
    private PersonFragment fg3;

    private FrameLayout flayout;

    private LinearLayout firstpage_layout;
    private LinearLayout recommend_layout;
    private LinearLayout user_layout;

    private ImageView firstpage_image;
    private ImageView recommend_image;
    private ImageView user_image;
    private TextView firstpage_text;
    private TextView recommend_text;
    private TextView user_text;

    private int whirt = 0xFFFFFFFF;
    private int gray = 0xFF7597B3;
    private int blue =0xFF0AB2FB;

    FragmentManager fManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fManager = getSupportFragmentManager();
        initViews();
        setChioceItem(0);
    }

    public void initViews()
    {
        firstpage_image = (ImageView) findViewById(R.id.firstpage);
        recommend_image = (ImageView) findViewById(R.id.recommend);
        user_image = (ImageView) findViewById(R.id.userpage);
        firstpage_text = (TextView) findViewById(R.id.firstpage_text);
        recommend_text = (TextView) findViewById(R.id.recommend_text);
        user_text = (TextView) findViewById(R.id.user_text);
        firstpage_layout = (LinearLayout) findViewById(R.id.firstpage_layout);
        recommend_layout = (LinearLayout) findViewById(R.id.recommend_layout);
        user_layout = (LinearLayout) findViewById(R.id.user_layout);
        firstpage_layout.setOnClickListener(this);
        recommend_layout.setOnClickListener(this);
        user_layout.setOnClickListener(this);
    }

    //ÖØÐ´onClickÊÂ¼þ
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.firstpage_layout:
                setChioceItem(0);
                break;
            case R.id.recommend_layout:
                setChioceItem(1);
                break;
            case R.id.user_layout:
                setChioceItem(2);
                break;
            default:
                break;
        }

    }



    public void setChioceItem(int index)
    {

        FragmentTransaction transaction = fManager.beginTransaction();
        clearChioce();
        hideFragments(transaction);
        switch (index) {
            case 0:
                firstpage_image.setImageResource(R.drawable.firstpage_pressed);
                firstpage_text.setTextColor(blue);
                firstpage_layout.setBackgroundResource(R.drawable.fp_button_bg);
                if (fg1 == null) {

                    fg1 = new FirstPageFragment ();
                    transaction.add(R.id.content, fg1);
                } else {

                    transaction.show(fg1);
                }
                break;

            case 1:
                recommend_image.setImageResource(R.drawable.recommend_pressed);
                recommend_text.setTextColor(blue);
                recommend_layout.setBackgroundResource(R.drawable.rc_button_bg);
                if (fg2 == null) {

                    fg2 = new MovieFragment();
                    transaction.add(R.id.content, fg2);
                } else {

                    transaction.show(fg2);
                }
                break;

            case 2:
                user_image.setImageResource(R.drawable.user_pressed);
                user_text.setTextColor(blue);
                user_layout.setBackgroundResource(R.drawable.uc_button_bg);
                if (fg3 == null) {

                    fg3 = new PersonFragment();
                    transaction.add(R.id.content, fg3);
                } else {

                    transaction.show(fg3);
                }
                break;
        }
        transaction.commit();
    }


    private void hideFragments(FragmentTransaction transaction) {
        if (fg1 != null) {
            transaction.hide(fg1);
        }
        if (fg2 != null) {
            transaction.hide(fg2);
        }
        if (fg3 != null) {
            transaction.hide(fg3);
        }
    }



    public void clearChioce()
    {
        firstpage_image.setImageResource(R.drawable.firstpage);
        firstpage_layout.setBackgroundColor(whirt);
        firstpage_text.setTextColor(gray);
        recommend_image.setImageResource(R.drawable.recommend);
        recommend_layout.setBackgroundColor(whirt);
        recommend_text.setTextColor(gray);
        user_image.setImageResource(R.drawable.user);
        user_layout.setBackgroundColor(whirt);
        user_text.setTextColor(gray);
    }

}
