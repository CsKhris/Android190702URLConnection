package com.cs.android190702urlconnection;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    EditText url;
    Button receive;
    TextView result;

    // 화면 출력을 위한 Handler
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            // 전송받은 Message를 String으로 변환하여 TextView에 출력
            String html = (String)msg.obj;
            result.setText(html);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        url = (EditText)findViewById(R.id.url);
        receive = (Button)findViewById(R.id.receive);
        result = (TextView)findViewById(R.id.result);

        receive.setOnClickListener(view->{
            // Keyborad를 화면에서 제거
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

            // Keyboard를 소유하고 있는 객체의 Focus 제거
            imm.hideSoftInputFromWindow(url.getWindowToken(), 0);

            //Log.e("Btn Click", "Message");
            // Data를 Download 받아서 Handler에게 전송할 Thread를 생성하여 시작
            Thread th = new Thread(){
                @Override
                public void run(){
                    //Log.e("Thread", "Start");
                    try {
                        // URL 만들기
                        URL addr = new URL(url.getText().toString().trim());
                        Log.e("addr", addr.toString());

                        // 연결
                        HttpURLConnection con = (HttpURLConnection)addr.openConnection();
                        Log.e("con", con.toString());

                        // Option 설정
                        con.setConnectTimeout(30000);
                        con.setUseCaches(false);
                        con.setDoInput(true);
                        con.setDoOutput(true);

                        Log.e("Response Code", con.getResponseCode() + "");
                        // Data를 가져오기 위한 Stream 생성
                        if(con.getResponseCode() == 200){
                            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));

                            // 문자열 읽기
                            StringBuilder sb = new StringBuilder();

                            // 한 줄씩 읽어서 msg에 추가하고 읽은 것이 없다면 중단
                            while(true){
                                String line = br.readLine();
                                if(line == null)
                                    break;
                                sb.append(line);
                            }

                            // Data를 가져오는 것은 Thread가 담당하고,
                            // 출력은 Handler가 담당 하도록 설정
                            Message msg =new Message();
                            msg.obj = sb.toString();
                            handler.sendMessage(msg);

                            br.close();
                            con.disconnect();
                        }
                    }catch (Exception e){
                        Log.e("Download Exception", e.getMessage());
                    }
                }
            };
            th.start();
        });
    }
}