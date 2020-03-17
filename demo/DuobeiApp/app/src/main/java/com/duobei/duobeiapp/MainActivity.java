package com.duobei.duobeiapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.duobei.duobeiapp.download.OfflinePlaybackListFragment;
import com.duobei.duobeiapp.live.LiveListFragment;
import com.duobei.duobeiapp.playback.PlaybackListFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * create MainActivity by ygs in 2017/05/15
 * 说明：
 * 此demo主要演示多贝云sdk相关的功能操作，为了方便开发者快速开发；本demo基本上不做任何封装处理，
 */
public class MainActivity extends FragmentActivity {


    @BindView(R.id.container)
    FrameLayout container;
    @BindView(R.id.navigation)
    BottomNavigationView navigation;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    private String[] tabItems = new String[]{"直播课程", "我的课程", "离线下载"};
    private int[] noramlTabImage = new int[]{R.drawable.live_normal, R.drawable.class_normal, R.drawable.download_normal,};
    private int[] passTabImage = new int[]{R.drawable.live_pass, R.drawable.class_pass, R.drawable.download_pass,};
    //fragment列表
    private List<Fragment> fragmentlist = new ArrayList<>();
    private FragmentManager manager;
    private ViewPager pager;
    private long firstTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
    }

    //初始化布局
    private void initView() {
        initFragment();
        initTablayout();
        //initBottomNavigationView();
    }


    //初始化相应的fragment
    private void initFragment() {
        fragmentlist.add(LiveListFragment.getInstance());
        fragmentlist.add(PlaybackListFragment.getInstance());
        fragmentlist.add(OfflinePlaybackListFragment.getInstance());
        manager = getSupportFragmentManager();

    }

    private void changeFragment(int index) {
        if (manager != null) {
            manager.beginTransaction().replace(R.id.container, fragmentlist.get(index)).commit();
        }

    }


    private void initBottomNavigationView() {
        navigation.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.live:
                                changeFragment(0);
                                break;
                            case R.id.playback:
                                changeFragment(1);
                                break;
                            case R.id.download:
                                changeFragment(2);
                                break;
                        }
                        return true;
                    }
                });
    }

    //初始化tablayout,只想说tablayout的bug太多；
    private void initTablayout() {
        // 提供自定义的布局添加Tab
        for (int i = 0; i < 3; i++) {

            tabLayout.addTab(tabLayout.newTab().setCustomView(getTabView(i)));
        }
        changeFragment(0);
        //默认选中第一个tab
        checkOne();
        //tabLayout.getTabAt(0).select();//无效
        //监听tab的切换事件
        //ps :tablayout的tabSelectedListener有bug,在选中和没选中如果不进行判断选中的选项；则会出现多选的情况
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                changeFragment(tab.getPosition());
                for (int i = 0; i < tabLayout.getTabCount(); i++) {
                    View view = tabLayout.getTabAt(i).getCustomView();
                    ImageView icon = (ImageView) view.findViewById(R.id.iv_img);
                    TextView text = (TextView) view.findViewById(R.id.tv_name);
                    if (i == tab.getPosition()) { // 选中状态
                        icon.setImageResource(passTabImage[i]);
                        text.setTextColor(getResources().getColor(android.R.color.black));
                    } else {// 未选中状态
                        icon.setImageResource(noramlTabImage[i]);
                        text.setTextColor(getResources().getColor(android.R.color.darker_gray));
                    }
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.setIcon(noramlTabImage[tab.getPosition()]);

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    private void checkOne() {
        View customView = tabLayout.getTabAt(0).getCustomView();
        ImageView icon = (ImageView) customView.findViewById(R.id.iv_img);
        TextView text = (TextView) customView.findViewById(R.id.tv_name);
        icon.setImageResource(passTabImage[0]);
        text.setTextColor(getResources().getColor(android.R.color.black));
    }

    public View getTabView(int position) {
        View view = LayoutInflater.from(this).inflate(R.layout.tab_view, null);
        ImageView tabIcon = (ImageView) view.findViewById(R.id.iv_img);
        tabIcon.setImageResource(noramlTabImage[position]);
        TextView tabText = (TextView) view.findViewById(R.id.tv_name);
        tabText.setText(tabItems[position]);
        return view;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        long secondTime = System.currentTimeMillis();
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (secondTime - firstTime < 2000) {
                return super.onKeyDown(keyCode, event);
            } else {
                Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                firstTime = System.currentTimeMillis();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
