package com.example.doan1.Ban;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.doan1.Connect.Connect;
import com.example.doan1.R;

import java.util.HashMap;
import java.util.Map;

public class ThemBan extends AppCompatActivity {

    private EditText edtMaBan;
    private EditText edtTenBan;
    String url = "http://" + Connect.connectip + "/DoAn_Android/Them_Ban.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_them_ban);

        edtMaBan=findViewById(R.id.edtmaban);
        edtTenBan=findViewById(R.id.edttenban);

        Button btnAdd = (Button) findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postData(
                        edtMaBan.getText().toString(),
                        edtTenBan.getText().toString()

                );
            }
        });
    }

    public void postData(String maban, String tenban) {
        RequestQueue requestQueue = Volley.newRequestQueue(ThemBan.this);
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.trim().equals("success")) {
                    Toast.makeText(ThemBan.this, "Thêm thành công", Toast.LENGTH_SHORT).show();
                    finish();
                    Intent intent = new Intent(ThemBan.this, Ban_Activity.class);
                    intent.putExtra("REFRESH_DATA", true);
                    startActivity(intent);
                } else if (response.trim().equals("exist")) {
                    Toast.makeText(ThemBan.this, "Mã Bàn đã tồn tại", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ThemBan.this, "Có lỗi xảy ra: " + response.trim(), Toast.LENGTH_SHORT).show();
                    Log.e("Lỗi phản hồi", "Có lỗi xảy ra: " + response.trim());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ThemBan.this, "Xảy ra lỗi, vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                Log.e("Thêm bàn", "Xảy ra lỗi khi thêm bàn", error); // Sửa lỗi tại đây
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("MaBan", maban);
                params.put("TenBan", tenban);
                return params;
            }
        };
        requestQueue.add(request);
    }
}