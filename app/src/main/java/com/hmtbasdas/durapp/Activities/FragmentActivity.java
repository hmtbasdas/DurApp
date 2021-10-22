package com.hmtbasdas.durapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;

import com.hmtbasdas.durapp.Adapters.SlidePagerAdapter;
import com.hmtbasdas.durapp.Fragments.Fragment1;
import com.hmtbasdas.durapp.Fragments.Fragment2;
import com.hmtbasdas.durapp.Fragments.Fragment3;
import com.hmtbasdas.durapp.databinding.ActivityFragmentBinding;

import java.util.ArrayList;

public class FragmentActivity extends BaseActivity  {

    private ActivityFragmentBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFragmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }

    private void init(){
        ArrayList<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new Fragment2());
        fragmentList.add(new Fragment3());

        PagerAdapter pagerAdapter = new SlidePagerAdapter(getSupportFragmentManager(), fragmentList);
        binding.viewPager.setAdapter(pagerAdapter);
    }

    public void skipFr(View view){
        Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
        startActivity(intent);
        finish();
    }
}