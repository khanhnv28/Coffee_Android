package com.example.doan1.Ban;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.doan1.Mon.Class_Mon;
import com.example.doan1.Mon.SharedCartViewModel;
import com.example.doan1.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.HashMap;

public class Ban_Fragment extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private SharedCartViewModel sharedCartViewModel;
    private String maBan,maNV;
    private ArrayList<Class_Mon> chosenDishes=new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ban_fragment);

        // Khởi tạo SharedCartViewModel trong Ban_Fragment (Activity cha)
        sharedCartViewModel = new ViewModelProvider(this).get(SharedCartViewModel.class);

        Intent intent = getIntent();
        if (intent != null) {
            maNV = intent.getStringExtra("maNV");
            maBan = intent.getStringExtra("maBan"); // Lấy maBan từ Intent
            chosenDishes = getIntent().getParcelableArrayListExtra("chosenDishes");
            HashMap<String, Integer> soLuongMon = (HashMap<String, Integer>) intent.getSerializableExtra("soLuongMon");

            // Lưu thông tin giỏ hàng tạm thời vào SharedCartViewModel
            sharedCartViewModel.setMaBan(maBan); // Cập nhật mã bàn
            sharedCartViewModel.setChosenDishes(chosenDishes); // Cập nhật danh sách các món đã chọn
            sharedCartViewModel.setSoLuongMon(soLuongMon); // Cập nhật số lượng món
        }
        String maNV = getIntent().getStringExtra("maNV"); // Lấy maNV từ Intent

        viewPager = findViewById(R.id.viewPager);
        setUpViewPager(viewPager, maNV);

        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
    }
    private void setUpViewPager(ViewPager viewPager, String maNV) {
        Ban_Adapter_Fragment adapter = new Ban_Adapter_Fragment(getSupportFragmentManager());
        adapter.addFragment(new TatCaBan_Fragment(sharedCartViewModel, maNV), "Tất Cả");
        adapter.addFragment(new BanSuDung_Fragment(sharedCartViewModel, maNV),"Bàn Sử Dụng");
        adapter.addFragment(new BanTrong_Fragment(sharedCartViewModel, maNV),"Bàn Còn Trống");
        viewPager.setAdapter(adapter);
    }
}
