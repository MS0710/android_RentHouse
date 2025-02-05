package com.example.renthouse2;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class FragmentViewPagerAdapter extends FragmentStateAdapter {
    public FragmentViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0){
            return new RentHouseFragment();
        }
        else if (position == 1){
            return new CollectFragment();
        }
        else if (position == 2){
            return new MessageFragment();
        }else{
            return new MyInfoFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
