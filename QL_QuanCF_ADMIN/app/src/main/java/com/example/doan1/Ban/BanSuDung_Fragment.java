package com.example.doan1.Ban;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.doan1.Connect.Connect;
import com.example.doan1.HoaDon.CartHelper;
import com.example.doan1.Mon.Class_Mon;
import com.example.doan1.Mon.LoaiMon_Fragment;
import com.example.doan1.Mon.SharedCartViewModel;
import com.example.doan1.R;
import com.example.doan1.Ban.SharedViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BanSuDung_Fragment extends Fragment {
    private View rootView;
    String ip = Connect.connectip;
    String url = "http://" + ip + "//DoAn_Android/QL_BanSuDung.php";
    private RecyclerView recyBan;
    private Ban_Adapter adapterBan;
    private List<Class_Ban> banList = new ArrayList<>();
    private List<Class_Mon> banList1 = new ArrayList<>();
    private ArrayList<Class_Mon> chosenDishes = new ArrayList<>();
    private String maNV;

    private SharedViewModel sharedViewModel;

    private SharedCartViewModel sharedCartViewModel;
    private String maBanTamTinh;
    HashMap<String, Integer> soLuongMon;
    private CartHelper cartHelper;

    public BanSuDung_Fragment(SharedCartViewModel sharedCartViewModel, String maNV) {
        this.sharedCartViewModel = sharedCartViewModel;
        this.maNV = maNV;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        maBanTamTinh = getActivity().getIntent().getStringExtra("maBanTamTinh");
        sharedCartViewModel = new ViewModelProvider(requireActivity()).get(SharedCartViewModel.class);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        cartHelper = new CartHelper(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.tatcaban, container, false);

        recyBan = rootView.findViewById(R.id.recyBan);
        recyBan.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapterBan = new Ban_Adapter(banList);
        recyBan.setAdapter(adapterBan);
        getActivity().setTitle("Danh Sách Bàn");
        getData(url);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (sharedCartViewModel != null) {
            adapterBan.setOnItemClickListener((view1, ban) -> {

                Intent intent = new Intent(getContext(), LoaiMon_Fragment.class);
                Bundle bundle = new Bundle();
                bundle.putString("maBan", ban.getMaBan());
                bundle.putString("tenBan", ban.getTenBan());
                bundle.putString("maNV", maNV);
                if (chosenDishes != null) {
                    bundle.putParcelableArrayList("chosenDishes", chosenDishes);
                }
                bundle.putSerializable("soLuongMon", sharedCartViewModel.getSoLuongMon());
                intent.putExtras(bundle);
                startActivity(intent);
            });

            adapterBan.setOnButtonClickListener(ban -> {
                updateBanTrangThai(ban.getMaBan(), 0);
            });
        } else {
            Toast.makeText(getContext(), "SharedCartViewModel is null", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateBanTrangThai(String maBan, int trangThai) {
        String updateUrl = "http://" + ip + "/DoAn_Android/QL_TrangThaiBan.php";
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, updateUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            if (status.equals("success")) {
                                Toast.makeText(getContext(), "Cập nhật trạng thái bàn thành công", Toast.LENGTH_SHORT).show();
                                getData(url);

                                sharedViewModel.setTableStatusUpdated(true);
                            } else {
                                Toast.makeText(getContext(), "Lỗi cập nhật trạng thái bàn", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Lỗi phản hồi từ máy chủ", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(getContext(), "Lỗi kết nối mạng", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("maBan", maBan);
                params.put("trangThai", String.valueOf(trangThai));
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }


    public void getData(String url) {
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    banList.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        banList.add(new Class_Ban(
                                jsonObject.getString("MaBan"),
                                jsonObject.getString("TenBan"),
                                jsonObject.getInt("TrangThai")
                        ));
                    }

                    adapterBan.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.e("NetworkError", "Xảy ra lỗi: " + error.getMessage());
                Toast.makeText(getContext(), "Lỗi kết nối mạng", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(stringRequest);
    }
}
