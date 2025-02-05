package com.example.renthouse2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class HomeActivity extends AppCompatActivity {
    private String TAG = "HomeActivity";
    private ViewPager2 viewPager;
    private TabLayout tab;
    private FragmentViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initView();
    }
    private void initView(){
        viewPager = findViewById(R.id.viewpager);
        tab = findViewById(R.id.tab);

        adapter = new FragmentViewPagerAdapter(this);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);

        new TabLayoutMediator(tab, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                if (position == 0) tab.setText("租屋");
                if (position == 1) tab.setText("收藏");
                if (position == 2) tab.setText("訊息");
                if (position == 3) tab.setText("我的");
            }
        }).attach();

        //點擊事件
        tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //Toast.makeText(MainActivity.this, tab.getText(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}