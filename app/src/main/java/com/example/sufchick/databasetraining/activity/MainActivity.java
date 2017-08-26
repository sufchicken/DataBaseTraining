package com.example.sufchick.databasetraining.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sufchick.databasetraining.AppInfo;
import com.example.sufchick.databasetraining.fragment.IndexFragment;
import com.example.sufchick.databasetraining.fragment.MainFragment;
import com.example.sufchick.databasetraining.R;
import com.example.sufchick.databasetraining.fragment.TrainingFragment;
import com.qq.e.ads.banner.ADSize;
import com.qq.e.ads.banner.AbstractBannerADListener;
import com.qq.e.ads.banner.BannerView;

import java.util.List;

public class MainActivity extends BaseActivity {

    public static final int STRAT_BANNER=10001;

    private Toolbar mToolbar;

    private BottomNavigationView mBottomNavigationView;
    private NavigationView mNavigationView;
    private DrawerLayout mDrawerLayout;
    private TextView titleTextView;

    private ViewGroup bannerContainer;
    private BannerView bv;

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case STRAT_BANNER:
                    MainActivity.this.bv.loadAD();
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        banner();

        findView();

        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        changeFragment(new MainFragment());

        setListener();

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                mDrawerLayout.openDrawer(mNavigationView);
                break;
            case R.id.toolbar_menu:
                mDrawerLayout.openDrawer(mNavigationView);
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 1:
                int chapter=-1;
                if(resultCode==RESULT_OK){
                    chapter=data.getIntExtra("chapter",-1);
                }
                if(chapter!=-1){
                    IndexFragment indexFragment=
                            (IndexFragment) getSupportFragmentManager().findFragmentById(R.id.fragement);
                    indexFragment.changeContent(chapter);
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);

    }



    public void changeTitle(String title){
        titleTextView.setText(title);
    }

    private void changeFragment(Fragment fragment){
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction transaction=fragmentManager.beginTransaction();
        transaction.replace(R.id.fragement,fragment);
        transaction.commit();
    }

    private void findView(){
        mToolbar=(Toolbar)findViewById(R.id.toolbar);
        mBottomNavigationView=(BottomNavigationView)findViewById(R.id.bottom_nav);
        titleTextView=(TextView) findViewById(R.id.toolbar_textview);
        mDrawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
        mNavigationView=(NavigationView)findViewById(R.id.nav_view);
    }

    private boolean hasAnyMarketInstalled(Context context) {

        Intent intent =new Intent();

        intent.setData(Uri.parse("market://details?id=android.browser"));

        List list = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

        return 0!= list.size();
    }

    private void setListener(){
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                String title=getString(R.string.app_name);
                switch (item.getItemId()){
                    case R.id.bottom_menu:
                        changeTitle(title);
                        changeFragment(new MainFragment());
                        break;
                    case R.id.bottom_study:
                        changeFragment(new IndexFragment());
                        break;
                    case R.id.bottom_test:
                        changeTitle(title);
                        changeFragment(new TrainingFragment());
                        break;
                }

                return true;
            }
        });



        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                mDrawerLayout.closeDrawer(mNavigationView);
                switch (item.getItemId()){
                    case R.id.contactus:
                        Intent contactusIntent=new Intent(MainActivity.this,ContactusActivity.class);
                        startActivity(contactusIntent);
                        break;
                    case R.id.marking:
                        if(hasAnyMarketInstalled(MainActivity.this)){
                            Uri uri = Uri.parse("market://details?id="+getPackageName());
                            Intent markingIntent = new Intent(Intent.ACTION_VIEW,uri);
                            markingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(markingIntent);
                        }
                        else{
                            Toast.makeText(MainActivity.this, "没有找到应用市场", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    default:
                        Toast.makeText(MainActivity.this, "您的操作有误，请重试", Toast.LENGTH_SHORT).show();
                }

                return true;
            }
        });
    }

    private void banner(){
        bannerContainer = (ViewGroup) this.findViewById(R.id.bannerContainer);
        this.initBanner();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    Message message=new Message();
                    message.what=STRAT_BANNER;
                    handler.sendMessage(message);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    private void initBanner() {
        this.bv = new BannerView(this, ADSize.BANNER, AppInfo.APPID, AppInfo.BOTTOM_BANNER);
        // 注意：如果开发者的banner不是始终展示在屏幕中的话，请关闭自动刷新，否则将导致曝光率过低。
        // 并且应该自行处理：当banner广告区域出现在屏幕后，再手动loadAD。
        bv.setRefresh(30);
        bv.setADListener(new AbstractBannerADListener() {

            @Override
            public void onNoAD(int arg0) {
                Log.i("AD_DEMO", "BannerNoAD，eCode=" + arg0);
            }

            @Override
            public void onADReceiv() {
                Log.i("AD_DEMO", "ONBannerReceive");
            }
        });
        bannerContainer.addView(bv);
    }



    private void doRefreshBanner() {
        if (bv == null) {
            initBanner();
        }
        bv.loadAD();
    }

    private void doCloseBanner() {
        bannerContainer.removeAllViews();
        if (bv != null) {
            bv.destroy();
            bv = null;
        }
    }
}
