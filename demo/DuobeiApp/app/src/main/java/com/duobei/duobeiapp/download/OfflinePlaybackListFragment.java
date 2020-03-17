package com.duobei.duobeiapp.download;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.duobei.duobeiapp.R;

import java.util.ArrayList;
import java.util.List;

/***
 ***Create DuobeiApp by yangge at 2017/5/15 
 * 下载界面（管理下载和离线下载的跳转）
 **/
public class OfflinePlaybackListFragment extends Fragment {

    private TextView localCheck;
    private TextView downingCheck;
    private ViewPager container;
    private ImageButton back;
    private List<Fragment> tabFragments = new ArrayList<>();
    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = View.inflate(getActivity(), R.layout.fragment_offlineplayback_list, null);
        initView(view);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //当前android版本超过6.0时，需要动态申请权限
        if (Build.VERSION.SDK_INT >= 23) {
            requestPermission();
        }
        initViewPager();
        initClick();

    }


    private void initView(View view) {
        localCheck = (TextView) view.findViewById(R.id.tv_local);
        localCheck.setClickable(true);
        downingCheck = (TextView) view.findViewById(R.id.tv_downing);
        downingCheck.setClickable(true);
        container = (ViewPager) view.findViewById(R.id.vp_container);
        back = (ImageButton) view.findViewById(R.id.ib_back);
    }

    public void initClick() {
        localCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                container.setCurrentItem(0);
            }
        });

        downingCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                container.setCurrentItem(1);
            }
        });


    }


    public void initViewPager() {
        if (tabFragments.size() > 0) {
            tabFragments.clear();
        }
        tabFragments.add(DownloadFragment.getInstance());
        tabFragments.add(DownloadingFragment.getInstance());
        container.setAdapter(new FragAdapter(getChildFragmentManager()));
        container.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                checkChange(position == 0);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void checkChange(boolean isExchange) {
        if (!isExchange) {
            localCheck.setBackground(getResources().getDrawable(R.drawable.shape_local_no_check));
            localCheck.setTextColor(Color.BLACK);

            downingCheck.setBackground(getResources().getDrawable(R.drawable.shape_downing_no_check));
            downingCheck.setTextColor(Color.WHITE);
        } else {
            localCheck.setBackground(getResources().getDrawable(R.drawable.shape_local));
            localCheck.setTextColor(Color.WHITE);

            downingCheck.setBackground(getResources().getDrawable(R.drawable.shape_downing));
            downingCheck.setTextColor(Color.BLACK);

        }


    }

    public class FragAdapter extends FragmentPagerAdapter {
        public FragAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            fragment = tabFragments.get(position);
            Bundle bundle = new Bundle();
            bundle.putString("id", "" + position);
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public int getCount() {
            return tabFragments == null ? 0 : tabFragments.size();
        }


        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
           getChildFragmentManager().beginTransaction().show(fragment).commit();
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
//                super.destroyItem(container, position, object);  
            Fragment fragment = tabFragments.get(position);
           getChildFragmentManager().beginTransaction().hide(fragment).commit();
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        container.removeAllViews();
    }

    /**
     * android6.0及以上动态权限，需要用户允许
     */

    private static final int EXTERNAL_STORAGE_REQ_CODE = 10;

    private void requestPermission() {
        //判断当前Activity是否已经获得了该权限
        if (ContextCompat.checkSelfPermission(this.getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.getActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    EXTERNAL_STORAGE_REQ_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case EXTERNAL_STORAGE_REQ_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getContext(), "申请读写sd卡成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "申请sd卡读写失败，这可能会影响到下载", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public static OfflinePlaybackListFragment getInstance() {
        return new OfflinePlaybackListFragment();
    }
}
