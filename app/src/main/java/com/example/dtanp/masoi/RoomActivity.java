package com.example.dtanp.masoi;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dtanp.masoi.adapter.CustomAdapter;
import com.example.dtanp.masoi.control.StaticFirebase;
import com.example.dtanp.masoi.control.StaticUser;
import com.example.dtanp.masoi.model.Phong;
import com.example.dtanp.masoi.model.User;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.LENGTH_SHORT;

public class RoomActivity extends Activity {

    private FirebaseDatabase database;
    ListView listroom;
    List<Phong> list,listSearch;
    Button btnnew,btnChoiNgay;
    ImageView imgback;
    DatabaseReference reference;
    CustomAdapter adapter,adapterSearch;
    EditText edtsearch;
    ImageButton imgsearch;
    TextView txtTenUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        database = StaticFirebase.database;
        reference =database.getReference();
        listSearch = new ArrayList<>();
        adapterSearch = new CustomAdapter(this,R.layout.custom_adapter,listSearch);
        AddConTrols();
        AddEvents();
        list=new ArrayList<>();
        laylistroom();
        adapter  = new CustomAdapter(this,R.layout.custom_adapter,list);
        adapter.notifyDataSetChanged();
        listroom.setAdapter(adapter);


    }

    private void AddEvents() {
        imgsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edtsearch.getText().toString().isEmpty()==false)
                {
                    boolean flag= false;
                    int sophong = Integer.parseInt(edtsearch.getText().toString());
                    for (Phong p : list)
                    {
                        if (p.getSophong()==sophong)
                        {
                            listSearch.clear();
                            listSearch.add(p);
                            listroom.setAdapter(adapterSearch);
                            adapterSearch.notifyDataSetChanged();
                            flag=true;
                            break;
                        }
                    }
                    if (flag==false)
                    {
                        Toast.makeText(RoomActivity.this,"Khong Tìm Thấy Phong Số " + sophong +" !",LENGTH_SHORT).show();
                    }
                }
            }
        });
        btnChoiNgay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Phong phong = getPhongChoiNgay();
                if(phong!=null)
                {
                    User us = new User();
                    String s = phong.getId();
                    us.setId(s);
                    StaticUser.userHost = us;
                    startmhban();
                    finish();
                }
                else
                {
                    startmhhost();
                    finish();
                }
            }
        });
        btnnew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startmhhost();
                finish();
            }
        });

        listroom.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if(list.get(i).getSonguoi()>=10)
                {
                    Toast.makeText(RoomActivity.this,"Phong Day!",LENGTH_SHORT).show();
                }
                else
                {
                    User us = new User();
                    Phong  s = (Phong) listroom.getAdapter().getItem(i);
                    us.setId(s.getId());
                    StaticUser.userHost = us;
                    StaticUser.phong= (Phong) listroom.getAdapter().getItem(i);
                    startmhban();
                    finish();
                }


            }
        });


        imgback.setClickable(true);
        imgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("back");
            }
        });
    }

    private void AddConTrols() {
        txtTenUser = findViewById(R.id.txtTenUser);
        txtTenUser.setText(StaticUser.user.getUser());
        listroom=findViewById(R.id.listroom);
        btnnew =findViewById(R.id.btnnew);
        imgback =  findViewById(R.id.imgback);
        btnChoiNgay = findViewById(R.id.btnchoingay);
        edtsearch = findViewById(R.id.edtsearch);
        imgsearch = findViewById(R.id.imgsearch);
    }

    public Phong getPhongChoiNgay()
    {
        for(Phong phong : list)
        {
            if (phong.getSonguoi()<10)
            {
                return phong;
            }
        }
        return null;
    }

    public void startmhhost()
    {
        Intent intent = new Intent(this,HostActivity.class);
        intent.putExtra("sophong",list.size()+1);
        startActivity(intent);
    }

    public void startmhban()
    {
        Intent intent = new Intent(this,BanActivity.class);
        startActivity(intent);
    }

    public void listroom()
    {

    }
    public void TaoChat()
    {

    }

    public void laylistroom()
    {
      reference.child("Room").addChildEventListener(new ChildEventListener() {
          @Override
          public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
              Phong phong = new Phong();
              phong.setId(dataSnapshot.child("id").getValue(String.class));
              phong.setTenphong(dataSnapshot.child("tenphong").getValue(String.class));
              phong.setSonguoi(dataSnapshot.child("songuoi").getValue(Integer.class));
              phong.setSophong(dataSnapshot.child("sophong").getValue(Integer.class));
              list.add(phong);
              adapter.notifyDataSetChanged();
          }

          @Override
          public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
              Phong phong = new Phong();
              phong.setId(dataSnapshot.child("id").getValue(String.class));
              phong.setSonguoi(dataSnapshot.child("songuoi").getValue(Integer.class));
              for (Phong p : list)
              {
                  if(p.getId().toString().equals(phong.getId().toString()))
                  {
                      p.setSonguoi(phong.getSonguoi());
                      break;
                  }
              }
              adapter.notifyDataSetChanged();

          }

          @Override
          public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

          }

          @Override
          public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

          }

          @Override
          public void onCancelled(@NonNull DatabaseError databaseError) {

          }
      })   ;
    }
}
