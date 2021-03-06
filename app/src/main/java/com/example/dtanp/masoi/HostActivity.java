package com.example.dtanp.masoi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dtanp.masoi.adapter.CustomAdapterChat;
import com.example.dtanp.masoi.control.StaticFirebase;
import com.example.dtanp.masoi.control.StaticUser;
import com.example.dtanp.masoi.model.Chat;
import com.example.dtanp.masoi.model.NhanVat;
import com.example.dtanp.masoi.model.Phong;
import com.example.dtanp.masoi.model.User;
import com.example.dtanp.masoi.model.UserRoom;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class HostActivity extends Activity {
    ListView listChat;
    CustomAdapterChat adapterChat;
    Button btnBatDau, btnSend, btnGiet, btnKhongGiet;
    ImageView imgNhanVat, imgTreoCo;
    List<UserRoom> userRoomList, userRoomListSong, userRoomListDanThuong;
    List<Chat> list;
    FirebaseDatabase database;
    EditText edtChat;
    public List<User> listUser, listUserInGame;
    DatabaseReference reference;
    TextView user1, user2, user3, user4, user5, user6, user7, user8, user9,txtTenUser,txtSoPhong,txtTenPhong,txtThoiGian, txtLuot, txtTreoCo;
    LinearLayout linearLayoutChat, linearLayoutListUser, linearLayoutTreoCo,linearLayoutKhungChat;
    private Timer timer;
    private Handler handler, handlerMaSoi;
    private ImageButton btnUser1, btnUser2, btnUser3, btnUser4, btnUser5, btnUser6, btnUser7, btnUser8, btnUser9;
    List<User> listUserMaSoi,listUserDanLang;
    User userThoSan, userGiaLang, userBaoVe, userTienTri;
    List<NhanVat> listNhanVat;
    Phong phong;
    NhanVat nhanvat;
    int manv,countUserReady=0;
    List<String> listIdMaSoichon, listAllChon;
    String idThoSanChon, idTienTriChon = "", idBaoVeChon, IDBoPhieu;
    HashMap<String, String> hashMap;
    boolean die = false;
    private boolean flagThoSan = false, flagTienTri = false, flagGiaLang = false, flagBaoVe = false;
    AlertDialog dialog;


    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);
        AnhXa();
        database = StaticFirebase.database;
        reference = database.getReference();
        taophong();
        capnhatlistChat();
        laylistUser();
        ConTrols();
        setThoiGian();
        XuLyChon();
        LangNgheUserReady();
        LamMoUser();

    }

    public  void LangNgheUserReady()
    {
        reference.child("Room").child(StaticUser.user.getId()).child("listUserReady").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    boolean flag = dataSnapshot.getValue(Boolean.class);
                    if(flag==true)
                    {
                        countUserReady++;
                        if (countUserReady>8)
                        {
                            countUserReady=0;
                            btnBatDau.setAlpha(1);
                            btnBatDau.setEnabled(true);

                        }
                    }
                    else if(flag==false)
                    {
                        countUserReady--;
                        if (countUserReady<8)
                        {
                            countUserReady=0;
                            btnBatDau.setAlpha(0.3f);
                            btnBatDau.setEnabled(false);

                        }
                    }

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                countUserReady--;
                btnBatDau.setAlpha(0.3f);
                btnBatDau.setEnabled(false);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void ConTrols() {
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (die == false) {
                    Chat chat = new Chat();
                    if (edtChat.getText().toString() != "") {
                        chat.setUsername(StaticUser.user.getUser());
                        chat.setMesage(edtChat.getText().toString());
                        send(chat);
                        edtChat.setText("");
                        //Toast.makeText(HostActivity.this,"send",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        findViewById(R.id.btnBatDau).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                OffTouchUser(userRoomList);
                RanDom();
                PushNhanVat();
                reference.child("Room").child(StaticUser.user.getId()).child("OK").setValue(true);
                pushNgay();
                XuLyLuot(1, true);
                LangNgheLuotDB();
                getNhanVat();
                LangNgheAllManHinh();
                LangNgheLuot();
                //HienThiLuot(1);
                LangNgheChat();
                ListenSuKien();
                getListXuLy();
                getTextViewAddList();
                ListenIdBiGiet();
                LangNgheKQBP();
                LangNgheBangIDChon();
                LangNgheDie();
                LangNgheOK();
                btnBatDau.setVisibility(View.INVISIBLE);



            }
        });
        btnGiet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference.child("Room").child(StaticUser.user.getId()).child("BangBoPhieu").child(StaticUser.user.getId()).setValue(1);
                btnGiet.setVisibility(View.INVISIBLE);
                btnKhongGiet.setVisibility(View.INVISIBLE);
            }
        });
        btnKhongGiet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference.child("Room").child(StaticUser.user.getId()).child("BangBoPhieu").child(StaticUser.user.getId()).setValue(2);
                btnKhongGiet.setVisibility(View.INVISIBLE);
                btnGiet.setVisibility(View.INVISIBLE);
            }
        });
    }

    public void LangNgheOK() {
        reference.child("Room").child(StaticUser.user.getId()).child("OK").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean flag = dataSnapshot.getValue(Boolean.class);
                if (flag == false) {
                    OntouchUser(userRoomList);
                    linearLayoutChat.setVisibility(View.INVISIBLE);
                    imgNhanVat.setVisibility(View.INVISIBLE);
                    txtThoiGian.setVisibility(View.INVISIBLE);
                    linearLayoutTreoCo.setVisibility(View.INVISIBLE);
                    linearLayoutListUser.setVisibility(View.VISIBLE);
                    resetLaiGameMoi();
                    btnBatDau.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void AnhXa() {
        user1 = findViewById(R.id.txtuser1);
        user2 = findViewById(R.id.txtuser2);
        user3 = findViewById(R.id.txtuser3);
        user4 = findViewById(R.id.txtuser4);
        user5 = findViewById(R.id.txtuser5);
        user6 = findViewById(R.id.txtuser6);
        user7 = findViewById(R.id.txtuser7);
        user8 = findViewById(R.id.txtuser8);
        user9 = findViewById(R.id.txtuser9);

        btnUser1 = findViewById(R.id.btnUser1);
        btnUser2 = findViewById(R.id.btnUser2);
        btnUser3 = findViewById(R.id.btnUser3);
        btnUser4 = findViewById(R.id.btnUser4);
        btnUser5 = findViewById(R.id.btnUser5);
        btnUser6 = findViewById(R.id.btnUser6);
        btnUser7 = findViewById(R.id.btnUser7);
        btnUser8 = findViewById(R.id.btnUser8);
        btnUser9 = findViewById(R.id.btnUser9);
        userRoomList = new ArrayList<>();


        userRoomList.add(new UserRoom(btnUser1, user1, false));
        userRoomList.add(new UserRoom(btnUser2, user2, false));
        userRoomList.add(new UserRoom(btnUser3, user3, false));
        userRoomList.add(new UserRoom(btnUser4, user4, false));
        userRoomList.add(new UserRoom(btnUser5, user5, false));
        userRoomList.add(new UserRoom(btnUser6, user6, false));
        userRoomList.add(new UserRoom(btnUser7, user7, false));
        userRoomList.add(new UserRoom(btnUser8, user8, false));
        userRoomList.add(new UserRoom(btnUser9, user9, false));



        txtTenUser = findViewById(R.id.txtTenUser);
        txtTenUser.setText(StaticUser.user.getUser());

        txtSoPhong = findViewById(R.id.txtSoPhong);
        txtTenPhong = findViewById(R.id.txtTenPhong);

        btnBatDau = findViewById(R.id.btnBatDau);
        btnBatDau.setAlpha(0.6f);
        btnBatDau.setEnabled(false);


        imgNhanVat = findViewById(R.id.imgNhanVat);
        // imgNhanVat.setAlpha(0);
        imgNhanVat.setVisibility(View.INVISIBLE);
        txtThoiGian = findViewById(R.id.txtThoiGian);

        listUser = new ArrayList<>();

        listChat = findViewById(R.id.listChat);
        listChat.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        list = new ArrayList<>();
        adapterChat = new CustomAdapterChat(this, R.layout.custom_chat, list);
        listChat.setAdapter(adapterChat);
        adapterChat.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listChat.setSelection(adapterChat.getCount()-1);
            }
        });

        btnSend = findViewById(R.id.btnSend);
        edtChat = findViewById(R.id.edtChat);


        linearLayoutChat = findViewById(R.id.lnrchat);
        linearLayoutChat.setVisibility(View.INVISIBLE);

        linearLayoutKhungChat = findViewById(R.id.lnrkhungchat);

        listUserMaSoi = new ArrayList<User>();
        listUserDanLang = new ArrayList<>();
        listNhanVat = new ArrayList<>();
        txtLuot = findViewById(R.id.txtLuot);
        listIdMaSoichon = new ArrayList<>();
        listAllChon = new ArrayList<>();
        hashMap = new HashMap<>();

        userRoomListDanThuong = new ArrayList<>();
        userRoomListSong = new ArrayList<>();

        txtThoiGian.setVisibility(View.INVISIBLE);

        linearLayoutListUser = findViewById(R.id.lnrlistUser);
        linearLayoutTreoCo = findViewById(R.id.lnrtreoco);
        btnGiet = findViewById(R.id.btngiet);
        btnKhongGiet = findViewById(R.id.btnkhonggiet);
        txtTreoCo = findViewById(R.id.txttreoco);
        imgTreoCo = findViewById(R.id.imgtreoco);
        linearLayoutTreoCo.setVisibility(View.INVISIBLE);
        listUserInGame = new ArrayList<>();
    }

    public void LamMoUser()
    {
        for (UserRoom userRoom : userRoomList)
        {
            userRoom.getUser().setAlpha(0.4f);
            userRoom.getUser().setEnabled(false);
        }
    }

    public void getlistUser(List<User> list) {
        for (User us : listUser) {
            list.add(us);
        }
    }

    public void capnhatlistChat() {
        reference = database.getReference();
        reference.child("Chat").child(StaticUser.user.getId()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Chat chat = dataSnapshot.getValue(Chat.class);
                if (!chat.getMesage().equals(" ")) {
                    list.add(chat);
                    adapterChat.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

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
        });
    }

    public void addDialoguser(final User user)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog_user,null);
        builder.setView(view);
        TextView txtuser = view.findViewById(R.id.txtuser);
        TextView txtid = view.findViewById(R.id.txtid);
        TextView txtlevel = view.findViewById(R.id.txtlevel);
        TextView txtwin = view.findViewById(R.id.txtwwin);
        TextView txtloss = view.findViewById(R.id.txtloss);

        txtid.setText(user.getId());
        txtuser.setText(user.getUser());

        Button btnkick = view.findViewById(R.id.btnkick);
        btnkick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference.child("Room").child(StaticUser.user.getId()).child("listUser").child(user.getId().toString()).removeValue();
                dialog.cancel();
            }
        });
        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        dialog = builder.create();
        dialog.show();
    }
    public void ghitextview(int id, User user) {
        reference = database.getReference();
        reference.child("Room").child(StaticUser.user.getId()).child("TextView").child(user.getId()).setValue(id);
    }

    public void AddUser(final User user) {
        for (UserRoom text : userRoomList) {
            if (text.isFlag() == false) {
                text.getTxtuser().setText(user.getUser());
                text.setFlag(true);
                text.setUseradd(user);
                text.getUser().setEnabled(true);
                text.getUser().setAlpha(1f);
                text.getUser().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addDialoguser(user);
                    }
                });
                break;
            }
        }
        hashMap.put(user.getUser().toString(), user.getId().toString());
    }

    public void RemoveUser(User user)
    {
        for (UserRoom text : userRoomList) {
            if (text.getUseradd().getId().toString().trim().equals(user.getId().toString())) {
                text.getTxtuser().setText("");
                text.setFlag(false);
                text.setUseradd(null);
                text.getUser().setEnabled(false);
                text.getUser().setAlpha(0.4f);
                break;
            }
        }
        hashMap.remove(user.getUser().toString());
    }

    public void OffTouchUser(List<UserRoom> list) {
        for (UserRoom text : list) {
            text.getUser().setEnabled(false);
            text.getUser().setAlpha(0.8f);
        }
    }

    public void OntouchUser(List<UserRoom> list) {
        for (UserRoom text : list) {
            text.getUser().setEnabled(true);
            text.getUser().setAlpha(1f);
        }
    }

    public void setDieUser(UserRoom text) {
        text.getUser().setEnabled(false);
        text.getUser().setImageResource(R.drawable.die);
        text.getUser().setAlpha(0.3f);
        //userRoomList.remove(text);
    }


    public void send(Chat chat) {
        reference = database.getReference();
        reference.child("Chat").child(StaticUser.user.getId()).push().setValue(chat);

    }

    public void laylistUser() {
        reference = database.getReference();
        reference.child("Room").child(StaticUser.user.getId()).child("listUser").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                User us = dataSnapshot.getValue(User.class);
                if (!us.getId().toString().equals(StaticUser.user.getId().toString())) {
                    AddUser(us);
                    listUser.add(us);
                    StaticUser.phong.setSonguoi(StaticUser.phong.getSonguoi()+1);
                    reference.child("Room").child(StaticUser.user.getId()).child("songuoi").setValue(StaticUser.phong.getSonguoi());
                    reference.child("Room").child(StaticUser.user.getId()).child("listUserReady").child(us.getId().toString()).setValue(false);
                    System.out.println("list UserActivity in lay list UserActivity" + listUser.size());
                } else {
                    listUser.add(us);
                }
                reference.child("Room").child(StaticUser.user.getId()).child("BangIdChon").child(us.getId().toString()).setValue("A");
                reference.child("Room").child(StaticUser.user.getId()).child("BangChonChucNang").child(us.getId().toString()).setValue("A");
                reference.child("Room").child(StaticUser.user.getId()).child("listUserSang").child(us.getId().toString()).setValue(false);
                reference.child("Room").child(StaticUser.user.getId()).child("BangBoPhieu").child(us.getId()).setValue(0);
                reference.child("Room").child(StaticUser.user.getId()).child("BangDie").child(us.getId()).setValue("A");


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                User us = dataSnapshot.getValue(User.class);
                RemoveUserList(us);
                Toast.makeText(HostActivity.this,"aaa"+listUser.size(),Toast.LENGTH_SHORT).show();
                RemoveUser(us);
                StaticUser.phong.setSonguoi(StaticUser.phong.getSonguoi()-1);
                reference.child("Room").child(StaticUser.user.getId()).child("songuoi").setValue(StaticUser.phong.getSonguoi());
                reference.child("Room").child(StaticUser.user.getId()).child("listUserReady").child(us.getId().toString()).removeValue();
                reference.child("Room").child(StaticUser.user.getId()).child("BangIdChon").child(us.getId().toString()).removeValue();
                reference.child("Room").child(StaticUser.user.getId()).child("listUserSang").child(us.getId().toString()).removeValue();
                reference.child("Room").child(StaticUser.user.getId()).child("BangBoPhieu").child(us.getId()).removeValue();
                reference.child("Room").child(StaticUser.user.getId()).child("BangDie").child(us.getId()).removeValue();
                reference.child("Room").child(StaticUser.user.getId()).child("BangChonChucNang").child(us.getId().toString()).removeValue();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void RemoveUserList(User user)
    {
        for (User us : listUser)
        {
            if (us.getId().toString().trim().equals(us.getId()))
            {
                listUser.remove(us);
                break;
            }
        }
    }
    public void taophong() {
        phong = new Phong();
        // System.out.println(StaticUser.UserActivity.getId());
        int soPhong = getIntent().getIntExtra("sophong",0);
        phong.setId(StaticUser.user.getId());
        phong.setSophong(soPhong);
        phong.setTenphong(StaticUser.user.getUser());
        phong.setSonguoi(1);
        StaticUser.phong = phong;
        txtTenPhong.setText(phong.getTenphong());
        txtSoPhong.setText(phong.getSophong()+"");
        reference.child("Room").child(phong.getId()).setValue(phong);
        reference.child("Room").child(phong.getId()).child("listUser").child(StaticUser.user.getId()).setValue(StaticUser.user);
        reference.child("Room").child(phong.getId()).child("listUserSang").child(StaticUser.user.getId()).setValue(false);
        reference.child("Room").child(phong.getId()).child("Luot").setValue(0);
        reference.child("Room").child(phong.getId()).child("AllChat").setValue(false);
        reference.child("Room").child(phong.getId()).child("AllManHinhChon").setValue(false);
        reference.child("Room").child(phong.getId()).child("BangIdChon").child(StaticUser.user.getId()).setValue("A");
        reference.child("Room").child(phong.getId()).child("IDBiBoPhieu").setValue("A");
        reference.child("Room").child(phong.getId()).child("BangBoPhieu").child(StaticUser.user.getId()).setValue(0);
        reference.child("Room").child(phong.getId()).child("BangDie").child(StaticUser.user.getId()).setValue("A");
        reference.child("Room").child(StaticUser.user.getId()).child("OK").setValue(false);
        Chat chat = new Chat();
        chat.setUsername(StaticUser.user.getUser());
        chat.setMesage(" ");
        reference.child("Chat").child(phong.getId()).push().setValue(chat);


    }

    int dem;
    boolean flagchat = false;

    public void DemGiay(int giay) {
        dem = giay;
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                dem--;
                handler.sendEmptyMessage(0);

            }
        }, 0, 1000);

    }

    public void setThoiGian() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                txtThoiGian.setText(dem + "");
                if (dem < 0) {
                    //handlerMaSoi.sendEmptyMessage(0);
                    if (flagchat == true) {
                        manv = 7;
                        handlerMaSoi.sendEmptyMessage(0);
                        flagchat = false;
                    }
                    if (flagxuli == true) {
                        reference.child("Room").child(StaticUser.user.getId()).child("listUserSang").child(IDBoPhieu).setValue(false);
                        //setLuotDB(7);
                        XuLyLuot(7, false);

                        if (die == false) {
                            if(StaticUser.user.getId().toString().trim().equals(IDBoPhieu)==false) {
                                btnKhongGiet.setVisibility(View.VISIBLE);
                                btnGiet.setVisibility(View.VISIBLE);
                            }
                        }
                        setLuotDB(7);
                        manv = 9;
                        flagxuli = false;
                    }
                    timer.cancel();
                    txtThoiGian.setVisibility(View.INVISIBLE);
                    //manv=8;

                }
            }
        };
    }

    public void LangNgheDie() {
        reference.child("Room").child(StaticUser.user.getId()).child("BangDie").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String id = dataSnapshot.getValue(String.class);
                if (!id.equals("A")) {
                    if (id.equals(StaticUser.user.getId())) {
                        die = true;
                    } else {
                        for (UserRoom text : userRoomList) {
                            if (text.getUseradd().getId().toString().equals(id)) {
                                setDieUser(text);
                                break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String id = dataSnapshot.getValue(String.class);
                if (!id.equals("A")) {
                    if (id.equals(StaticUser.user.getId())) {
                        die = true;
                    } else {
                        for (UserRoom text : userRoomList) {
                            if (text.getUseradd().getId().toString().equals(id)) {
                                setDieUser(text);
                                break;
                            }
                        }
                    }
                }

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
        });
    }


    public void RanDom() {
        List<User> listUserRandom = new ArrayList<>();
        getlistUser(listUserRandom);
        getlistUser(listUserInGame);
        System.out.println(listUserInGame.size() + "list UserActivity in game");
        System.out.println(listUser.size() + "list UserActivity");
        //listUserRandom.add(StaticUser.UserActivity);
        for (int i = 0; i < 10; i++) {
            // System.out.println(listUserRandom.size()+ "Lisuserrandom");
            NhanVat nv = new NhanVat();
            int k;
            if (listUserRandom.size() > 1) {
                k = (int) (Math.random() * listUserRandom.size());

            } else
                k = 0;
            if (i < 3) {
                listUserMaSoi.add(listUserRandom.get(k));
                nv.setManv(1);
                nv.setResource(R.drawable.imgmasoi);
            } else if (i < 6) {
                listUserDanLang.add(listUserRandom.get(k));
                nv.setManv(2);
                nv.setResource(R.drawable.imgdanlang);
            } else if (i == 6) {
                userThoSan = listUserRandom.get(k);
                listUserDanLang.add(listUserRandom.get(k));
                nv.setManv(3);
                nv.setResource(R.drawable.imgthosan);
            } else if (i == 7) {
                userBaoVe = listUserRandom.get(k);
                listUserDanLang.add(listUserRandom.get(k));
                nv.setManv(4);
                nv.setResource(R.drawable.imgbaove);
            } else if (i == 8) {
                userGiaLang = listUserRandom.get(k);
                listUserDanLang.add(listUserRandom.get(k));
                nv.setManv(5);
                nv.setResource(R.drawable.imggialang);
            } else if (i == 9) {
                userTienTri = listUserRandom.get(k);
                listUserDanLang.add(listUserRandom.get(k));
                nv.setManv(6);
                nv.setResource(R.drawable.imgtientri);


            }
            // System.out.println(k);
            nv.setId(listUserRandom.get(k).getId());
            listUserRandom.remove(k);
            //System.out.println(nv.getId());
            listNhanVat.add(nv);

        }
    }

    public void getListXuLy() {
        for (NhanVat nv : listNhanVat) {
            if (nv.getManv() != 1) {
                for (UserRoom text : userRoomList) {
                    if (text.getUseradd().getId().toString().equals(nv.getId().toString())) {
                        userRoomListDanThuong.add(text);
                        break;
                    }
                }
            }

        }
        // System.out.println(listNhanVat.size() + "list nhan vat");
        // System.out.println(userRoomListDanThuong.size() + "list dan thuong");
    }

    public void getTextViewAddList() {
        for (UserRoom text : userRoomList) {
            userRoomListSong.add(text);
        }
    }

    public void PushNhanVat() {
        for (NhanVat nv : listNhanVat) {
            reference.child("Room").child(StaticUser.user.getId()).child("BangNhanVat").child(nv.getId()).setValue(nv);
        }

    }

    public void setImageNhanVat(int ma) {
        // imgNhanVat.setAlpha(1f);
        switch (ma) {
            case 1:
                imgNhanVat.setImageResource(R.drawable.imgmasoi);
                break;
            case 2:
                imgNhanVat.setImageResource(R.drawable.imgdanlang);
                break;
            case 3:
                imgNhanVat.setImageResource(R.drawable.imgthosan);
                break;
            case 4:
                imgNhanVat.setImageResource(R.drawable.imgbaove);
                break;
            case 5:
                imgNhanVat.setImageResource(R.drawable.imggialang);
                break;
            case 6:
                imgNhanVat.setImageResource(R.drawable.imgtientri);
                break;
            default:
                break;
        }
        imgNhanVat.setVisibility(View.VISIBLE);
    }

    public void pushNgay() {
        // phong.setNgay(phong.getNgay() + 1);
        // reference.child("Room").child(StaticUser.UserActivity.getId()).child("ngay").setValue(phong.getNgay());
    }

    public void pushLuot(int t) {

        reference.child("Room").child(StaticUser.user.getId()).child("Luot").setValue(t);
    }

    public void getNhanVat() {
        reference.child("Room").child(StaticUser.user.getId()).child("BangNhanVat").child(StaticUser.user.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                nhanvat = dataSnapshot.getValue(NhanVat.class);
                setImageNhanVat(nhanvat.getManv());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        System.out.println(listUserInGame.size() + "list UserActivity in game trong get nhan vat");
        System.out.println(listUser.size() + "list UserActivity trong get nhan vat");
    }

    public void removelistUserInGameID(String id) {
        for (User us : listUserInGame) {
            if (us.getId().toString().equals(id)) {
                listUserInGame.remove(us);
                break;
            }
        }
    }


    public void HienThiLuot(int luot) {
        String to = "";
        switch (luot) {
            case 1:
                to = "Ma Soi Dang Chon";
                break;
            case 2:
                to = "Bao Ve Dang chon";
                break;
            case 3:
                to = "Tho San Dang Chon";
                break;
            case 4:
                to = "Tien Tri Dang Chon";
                break;
            case 5:
                to = "Dan Lang Bieu Quyet";
                break;
            case 6:
                to = "Nguoi Treo co giai trinh";
                break;
            case 7:
                to = "Bo Phieu Giet";
                break;
            default:
                break;
        }
        txtLuot.setText(to.toString().trim());
    }

    public void setLuotDB(int luot) {
        reference.child("Room").child(StaticUser.user.getId()).child("Luot").setValue(luot);
    }

    public void LangNgheLuotDB() {
        reference.child("Room").child(StaticUser.user.getId()).child("Luot").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int l = dataSnapshot.getValue(Integer.class);
                if (l != 0) {
                    if (l == 1) {
                        linearLayoutChat.setVisibility(View.INVISIBLE);
                        listChat.setVisibility(View.INVISIBLE);
                        linearLayoutKhungChat.setVisibility(View.INVISIBLE);

                    }
                    if (l == 7) {
                        if(die==false) {
                            if(StaticUser.user.getId().toString().trim().equals(IDBoPhieu)==false) {
                                System.out.println("toi luot 7");
                                btnGiet.setVisibility(View.VISIBLE);
                                btnKhongGiet.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                    HienThiLuot(l);
                } else {
                    txtLuot.setText("");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void XuLyChon() {
        handlerMaSoi = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                if (manv == 1) {
                    if (listUserMaSoi.size() == listIdMaSoichon.size()) {
                        setLuotDB(2);
                        //pushLuot(2);
                        XuLyLuot(1, false);
                        XuLyLuot(4, true);
                    }
                } else if (manv == 4) {
                    setLuotDB(3);
                    //pushLuot(3);
                    XuLyLuot(4, false);
                    XuLyLuot(3, true);
                } else if (manv == 3) {
                    setLuotDB(4);
                    //pushLuot(4);
                    XuLyLuot(3, false);
                    XuLyLuot(6, true);
                } else if (manv == 6) {

                    XuLyLuot(6, false);
                    XuLyLuot(7, true);
                } else if (manv == 7) {
                    XuLyLuot(7, false);
                    XuLyLuot(8, true);
                    setLuotDB(5);
                } else if (manv == 8) {
                    XuLyLuot(8, false);
                    IDBoPhieu = getIDBOPhieu();
                    reference.child("Room").child(StaticUser.user.getId()).child("IDBiBoPhieu").setValue(IDBoPhieu);
                    reference.child("Room").child(StaticUser.user.getId()).child("listUserSang").child(IDBoPhieu).setValue(true);
                    XuLiGiaiTrinh();
                } else if (manv == 9) {
                    if (giet == true) {
                        giet = false;
                        XoaNhanVat(IDBoPhieu);
                        XoaNhanVatChucNang(IDBoPhieu);
                        removelistUserInGameID(IDBoPhieu);
                        reference.child("Room").child(StaticUser.user.getId()).child("BangDie").child(IDBoPhieu).setValue(IDBoPhieu);
                    }

                    linearLayoutListUser.setVisibility(View.VISIBLE);
                    linearLayoutTreoCo.setVisibility(View.INVISIBLE);
                    linearLayoutChat.setVisibility(View.INVISIBLE);
                    XuLiCuoiNgay();

                }

            }
        };

    }

    public void XoaNhanVat(String id) {
        UserRoom userRoom = new UserRoom();
        for (UserRoom text : userRoomListSong) {
            if (text.getUseradd().getId().toString().equals(id)) {
                userRoom = text;
                userRoomListSong.remove(text);
                break;
            }
        }
        for (UserRoom text : userRoomListDanThuong) {
            if (text.getUseradd().getId().toString().equals(id)) {
                userRoom = text;
                userRoomListDanThuong.remove(text);
                break;
            }
        }
        for (User user : listUserMaSoi) {
            if (user.getId().toString().equals(id)) {
                listUserMaSoi.remove(user);
                break;
            }
        }
        for (User user : listUserDanLang) {
            if (user.getId().toString().equals(id)) {
                listUserDanLang.remove(user);
                break;
            }
        }
        if (!id.equals(StaticUser.user.getId())) {
            setDieUser(userRoom);
        } else {
            die = true;
        }

    }

    boolean flagxuli = false;

    public void XuLiGiaiTrinh() {

        //XuLyLuot(7,true);
        setLuotDB(6);
        flagchat = false;
        flagxuli = true;
        txtThoiGian.setVisibility(View.VISIBLE);
        DemGiay(20);

    }

    private int countYes = 0, countNo = 0;
    boolean giet = false;

    public void LangNgheKQBP() {
        reference.child("Room").child(StaticUser.user.getId()).child("BangBoPhieu").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                int t = dataSnapshot.getValue(Integer.class);
                if (t != 0) {
                    if (t == 1) {
                        countYes++;
                    } else {
                        countNo++;
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                int t = dataSnapshot.getValue(Integer.class);
                if (t != 0) {
                    if (t == 1) {
                        countYes++;
                    } else {
                        countNo++;
                    }
                }
                if (countNo + countYes == (listUserInGame.size()-1)){
                    if (countYes > countNo) {
                        giet = true;
                        countNo = 0;
                        countYes = 0;
                    }
                    manv = 9;
                    handlerMaSoi.sendEmptyMessage(0);
                }
                System.out.println(countYes + "Dong y");
                System.out.println(userRoomListSong.size() + "so nguoi con song");

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
        });
    }

    public void XuLyLuot(int luot, boolean flag) {
        if (flagGiaLang == true) {
            if (luot > 1 && luot < 7) {
                luot = 7;
            }
        } else {

            if (luot == 4) {
                if (flagBaoVe == true) {
                    luot = 6;
                }
            }
            if (luot == 3) {
                if (flagThoSan == true) {
                    luot = 4;
                }
            }
            if (luot == 6) {
                if (flagTienTri == true) {
                    luot = 7;
                }
            }
        }
        if (luot == 1) {
            pushLuot(1);
            if (listUserMaSoi.size() > 0) {
                reference.child("Room").child(StaticUser.user.getId()).child("listUserSang").child(listUserMaSoi.get(0).getId().toString()).setValue(flag);
            }
            if (listUserMaSoi.size() > 1) {
                reference.child("Room").child(StaticUser.user.getId()).child("listUserSang").child(listUserMaSoi.get(1).getId().toString()).setValue(flag);
            }
            if (listUserMaSoi.size() > 2) {
                reference.child("Room").child(StaticUser.user.getId()).child("listUserSang").child(listUserMaSoi.get(2).getId().toString()).setValue(flag);
            }
        } else if (luot == 3) {
            pushLuot(3);
            reference.child("Room").child(StaticUser.user.getId()).child("listUserSang").child(userThoSan.getId().toString()).setValue(flag);
        } else if (luot == 4) {
            pushLuot(2);
            reference.child("Room").child(StaticUser.user.getId()).child("listUserSang").child(userBaoVe.getId().toString()).setValue(flag);
        } else if (luot == 6) {
            pushLuot(4);
            reference.child("Room").child(StaticUser.user.getId()).child("listUserSang").child(userTienTri.getId().toString()).setValue(flag);
        } else if (luot == 7) {
            reference.child("Room").child(StaticUser.user.getId()).child("AllChat").setValue(flag);
        } else if (luot == 8) {
            reference.child("Room").child(StaticUser.user.getId()).child("AllManHinhChon").setValue(flag);
        }
    }

    public void ListenSuKien() {

        //Bao ve
            reference.child("Room").child(StaticUser.user.getId()).child("BangChonChucNang").child(userBaoVe.getId().toString()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String id = dataSnapshot.getValue(String.class);
                    if (!id.equals("A")) {
                        manv = 4;
                        idBaoVeChon = id;
                        handlerMaSoi.sendEmptyMessage(0);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        //Phu thuy
            reference.child("Room").child(StaticUser.user.getId()).child("BangChonChucNang").child(userTienTri.getId().toString()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String id = dataSnapshot.getValue(String.class);
                    if (!id.equals("A")) {
                        manv = 6;
                        idTienTriChon = id;
                        handlerMaSoi.sendEmptyMessage(0);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        //Tho San

            reference.child("Room").child(StaticUser.user.getId()).child("BangChonChucNang").child(userThoSan.getId().toString()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String id = dataSnapshot.getValue(String.class);
                    if (!id.equals("A")) {
                        manv = 3;
                        idThoSanChon = id;
                        handlerMaSoi.sendEmptyMessage(0);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        //Ma Soi
            reference.child("Room").child(StaticUser.user.getId()).child("BangChonChucNang").child(listUserMaSoi.get(0).getId().toString()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String id = dataSnapshot.getValue(String.class);
                    if (!id.equals("A")) {
                        manv = 1;
                        listIdMaSoichon.add(id);
                        handlerMaSoi.sendEmptyMessage(0);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            reference.child("Room").child(StaticUser.user.getId()).child("BangChonChucNang").child(listUserMaSoi.get(1).getId().toString()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String id = dataSnapshot.getValue(String.class);
                    if (!id.equals("A")) {
                        manv = 1;
                        listIdMaSoichon.add(id);
                        handlerMaSoi.sendEmptyMessage(0);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            reference.child("Room").child(StaticUser.user.getId()).child("BangChonChucNang").child(listUserMaSoi.get(2).getId().toString()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String id = dataSnapshot.getValue(String.class);
                    if (!id.equals("A")) {
                        manv = 1;
                        listIdMaSoichon.add(id);
                        handlerMaSoi.sendEmptyMessage(0);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

    }

    public void ListenIdBiGiet() {
        reference.child("Room").child(StaticUser.user.getId()).child("BangIdChon").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String st = dataSnapshot.getValue(String.class);
                if (!st.equals("A")) {
                    listAllChon.add(st);
                    if (listAllChon.size() == listUserInGame.size()) {
                        manv = 8;
                        handlerMaSoi.sendEmptyMessage(0);
                    }
                    System.out.println(listAllChon.size() + "list all chon");
                    System.out.println(listUserInGame.size() + "List UserActivity game");
                }
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
        });
    }

    //client
    public void LangNgheLuot() {
        reference.child("Room").child(StaticUser.user.getId()).child("listUserSang").child(StaticUser.user.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean flag = dataSnapshot.getValue(Boolean.class);
                if (flag == true) {
                    txtThoiGian.setVisibility(View.VISIBLE);
                    DemGiay(30);
                    AddClickUser("BangChonChucNang");
                    if (nhanvat.getManv() == 1) {

                        OntouchUser(userRoomListDanThuong);
                        //System.out.println("NhanVat ne");
                    } else {
                        OntouchUser(userRoomListSong);
                    }
                } else {
                    if (nhanvat.getManv() == 1 && flag == false) {
                        OffTouchUser(userRoomListDanThuong);
                        txtThoiGian.setVisibility(View.INVISIBLE);
                    } else {
                        OffTouchUser(userRoomListSong);
                        txtThoiGian.setVisibility(View.INVISIBLE);
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void LangNgheChat() {
        reference.child("Room").child(StaticUser.user.getId()).child("AllChat").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean flag = dataSnapshot.getValue(Boolean.class);
                if (flag == true&&die==false) {
                    linearLayoutChat.setVisibility(View.VISIBLE);
                    findViewById(R.id.lnrkhungchat).setVisibility(View.VISIBLE);
                    listChat.setVisibility(View.VISIBLE);
                    txtThoiGian.setVisibility(View.VISIBLE);
                    DemGiay(10);
                    flagchat = true;
                } else {
                    linearLayoutChat.setVisibility(View.INVISIBLE);
                    findViewById(R.id.lnrkhungchat).setVisibility(View.INVISIBLE);
                    listChat.setVisibility(View.INVISIBLE);
                    txtThoiGian.setVisibility(View.INVISIBLE);
                    flagchat = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void AddClickUser(final String st) {
        for (final UserRoom text : userRoomList) {
            text.getUser().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    timer.cancel();
                    if (nhanvat.getManv() == 6) {
                        for (NhanVat nv : listNhanVat) {
                            if (text.getUseradd().getId().toString().equals(nv.getId().toString())) {
                                if (nv.getManv() == 1) {
                                    Toast.makeText(HostActivity.this, "day la ma soi", Toast.LENGTH_SHORT).show();

                                } else {
                                    Toast.makeText(HostActivity.this, "day khong phai la soi la ma soi", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            }
                        }
                    }
                    reference.child("Room").child(StaticUser.user.getId()).child(st).child(StaticUser.user.getId()).setValue(hashMap.get(text.getTxtuser().getText().toString()));
                    OffTouchUser(userRoomListSong);
                }
            });
        }
    }

    public void LangNgheAllManHinh() {
        reference.child("Room").child(StaticUser.user.getId()).child("AllManHinhChon").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean flag = dataSnapshot.getValue(Boolean.class);
                if (flag == true) {
                    if (die == false) {
                        OntouchUser(userRoomListSong);
                        AddClickUser("BangIdChon");
                    }

                } else {
                    OffTouchUser(userRoomListSong);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public String getIdSoiChon() {
        String st = "";
        if (listIdMaSoichon.size() == 3) {
            if (listIdMaSoichon.get(0).toString().equals(listIdMaSoichon.get(1).toString())) {
                st = listIdMaSoichon.get(0);
            } else if (listIdMaSoichon.get(1).toString().equals(listIdMaSoichon.get(2).toString())) {
                st = listIdMaSoichon.get(1);
            } else {
                st = listIdMaSoichon.get(0);
            }
        }
        st = listIdMaSoichon.get(0);
        return st;
    }

    public String getIDBOPhieu() {
        String id = "";
        int max = 1, count = 0;
        {
            for (int i = 0; i < listAllChon.size(); i++) {
                count = 0;
                for (int j = i + 1; j < listAllChon.size(); j++) {
                    if (listAllChon.get(i).toString().equals(listAllChon.get(j).toString())) {
                        count++;
                    }
                }
                if (count > max) {
                    max = count;
                    id = listAllChon.get(i);
                }
            }
        }
        return id;

    }


    public void XuLiCuoiNgay() {
        String idMaSoiChon = getIdSoiChon();
        if (idMaSoiChon.equals(idBaoVeChon)) {
            return;
        } else if (idMaSoiChon.equals(IDBoPhieu))
            return;
        else if (idMaSoiChon.equals(userThoSan.getId().toString()) && flagGiaLang == false) {
            XoaNhanVat(idMaSoiChon);
            XoaNhanVat(idThoSanChon);
            XoaNhanVatChucNang(idMaSoiChon);
            XoaNhanVatChucNang(idThoSanChon);
            removelistUserInGameID(idMaSoiChon);
            removelistUserInGameID(idThoSanChon);
            if (!idMaSoiChon.equals(IDBoPhieu)) {
                reference.child("Room").child(StaticUser.user.getId()).child("BangDie").child(idMaSoiChon).setValue(idMaSoiChon);
                reference.child("Room").child(StaticUser.user.getId()).child("BangDie").child(idThoSanChon).setValue(idThoSanChon);
            }
        } else {

            XoaNhanVat(idMaSoiChon);
            XoaNhanVatChucNang(idMaSoiChon);
            removelistUserInGameID(idMaSoiChon);
            reference.child("Room").child(StaticUser.user.getId()).child("BangDie").child(idMaSoiChon).setValue(idMaSoiChon);
        }
        if (listUserMaSoi.size() < 1) {
            resetLaiGameMoi();
        } else if (listUserMaSoi.size() >= listUserDanLang.size()) {
            resetLaiGameMoi();
        }
        ResetLaiNgayMoi();
    }

    public void resetLaiGameMoi() {
        reference.child("Room").child(StaticUser.user.getId()).child("OK").setValue(false);
        listUserMaSoi.clear();
        listUserDanLang.clear();
        flagTienTri = false;
        flagBaoVe = false;
        flagThoSan = false;
        flagGiaLang = false;
        listIdMaSoichon.clear();
        listAllChon.clear();
        listNhanVat.clear();
        idBaoVeChon = "";
        idThoSanChon = "";
        idTienTriChon = "";
        resetAllBang();
        resetLaiBangDie();
        userRoomListDanThuong.clear();
        userRoomListSong.clear();
        list.clear();
        listUserInGame.clear();
        die = false;
        ResetAnhUser();

    }


    public void XoaNhanVatChucNang(String id) {

        if (flagGiaLang == false && userGiaLang.getId().toString().equals(id)) {
            flagGiaLang = true;
            flagBaoVe = true;
            flagTienTri = true;
            flagThoSan = true;
        }
        if (flagTienTri == false && userTienTri.getId().toString().equals(id)) {
            flagTienTri = true;
        }
        if (flagThoSan == false && userThoSan.getId().toString().equals(id)) {
            flagThoSan = true;
        }
        if (flagBaoVe == false && userBaoVe.getId().toString().equals(id)) {
            flagBaoVe = true;
        }
    }

    public void ResetLaiNgayMoi() {
        //pushNgay();
        XuLyLuot(1, true);
        System.out.println("Toi Xu li luot 1 true");
        listIdMaSoichon.clear();
        listAllChon.clear();
        idBaoVeChon = "";
        idThoSanChon = "";
        idTienTriChon = "";
        resetAllBang();

    }

    public void LangNgheBangIDChon() {
        reference.child("Room").child(StaticUser.user.getId()).child("IDBiBoPhieu").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String st = dataSnapshot.getValue(String.class);
                String name = "";
                if (st.equals(StaticUser.user.getId())) {
                    linearLayoutChat.setVisibility(View.VISIBLE);
                    findViewById(R.id.lnrkhungchat).setVisibility(View.VISIBLE);
                    listChat.setVisibility(View.VISIBLE);
                } else {
                    if (!st.equals("A")) {
                        findViewById(R.id.lnrkhungchat).setVisibility(View.INVISIBLE);
                        linearLayoutListUser.setVisibility(View.INVISIBLE);
                        btnKhongGiet.setVisibility(View.INVISIBLE);
                        btnGiet.setVisibility(View.INVISIBLE);
                        linearLayoutTreoCo.setVisibility(View.VISIBLE);
                        //.setVisibility(View.INVISIBLE);
                        // btnKhongGiet.setVisibility(View.INVISIBLE);
                        linearLayoutChat.setVisibility(View.VISIBLE);
                        listChat.setVisibility(View.VISIBLE);

                        for (User user : listUser) {
                            if (user.getId().equals(st)) {
                                txtTreoCo.setText(user.getUser());
                                break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void resetAllBang() {
        for (User us : listUser) {
            reference.child("Room").child(StaticUser.user.getId()).child("BangIdChon").child(us.getId().toString()).setValue("A");
            reference.child("Room").child(StaticUser.user.getId()).child("BangChonChucNang").child(us.getId().toString()).setValue("A");
            reference.child("Room").child(StaticUser.user.getId()).child("BangBoPhieu").child(us.getId()).setValue(0);
        }
        reference.child("Room").child(StaticUser.user.getId()).child("IDBiBoPhieu").setValue("A");
    }

    public void resetLaiBangDie()
    {
        for (User us : listUser)
        {
            reference.child("Room").child(StaticUser.user.getId()).child("BangDie").child(us.getId()).setValue("A");
        }
    }

    public  void ResetAnhUser()
    {
        for (UserRoom text : userRoomList)
        {
            text.getUser().setImageResource(R.drawable.image_user);
        }
    }
    //Chưa reset lai game moi
}
