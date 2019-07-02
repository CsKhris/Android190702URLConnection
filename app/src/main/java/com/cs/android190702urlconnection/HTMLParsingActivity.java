package com.cs.android190702urlconnection;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class HTMLParsingActivity extends AppCompatActivity {

    ListView listView;
    ArrayAdapter<String> adapter;
    ArrayList<String> list;

    // ListView를 재출력하는 Handler
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            // Adapter가 ListView에 Data의 변경이 발생하였으니
            // Data를 다시 출력하라고 Message를 전송
            adapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_htmlparsing);

        // List View 출력
        listView = (ListView)findViewById(R.id.htmlpslistview);
        list = new ArrayList<>();
        adapter = new ArrayAdapter<>(HTMLParsingActivity.this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);

        Button htmlpsbtn= (Button)findViewById(R.id.htmlpsbtn);
        htmlpsbtn.setOnClickListener(view -> {
            Thread th = new Thread(){
                @Override
                public void run(){
                    String html = null;
                    try{
                        // Download 받을 주소
                        String addr = "https://finance.naver.com/";

                        // URL로 생성
                        URL url = new URL(addr);

                        // URL 연결
                        HttpURLConnection con = (HttpURLConnection)url.openConnection();

                        // 문자열로 읽을 Stream 생성
                        // UFT-8이 아니기 때문에 Encoding 설정을 해주지 않으면 한글이 깨집니다.
                        // EUC-KR로 읽어오도록 설정 합니다.
                        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "EUC-KR"));

                        // 문자열을 저장할 객체 생성
                        StringBuilder sb = new StringBuilder();

                        // 문자열을 줄 단위로 읽어서 sb에 저장
                        while (true){
                            String line = br.readLine();
                            if(line == null)
                                break;
                            sb.append(line + "\n");
                        }
                        br.close();
                        con.disconnect();

                        // 옴기기
                        html = sb.toString();
                        //Log.e("html", html);

                    }catch (Exception e){
                        Log.e("Download Exception", e.getMessage());
                    }

                    // HTML Parsing
                    try{
                        // HTML을 DOM 객체로 펼쳐내기
                        Document doc = Jsoup.parse(html);
                        //Log.e("doc", doc.toString());

                        // 원하는 선택자와 Data 찾아오기
                        Elements elements = doc.select("span > a");
                        //Log.e("elements", elements.toString());

                        // 선택된 Data 순회
                        for(Element element : elements){
                            //Log.e("element", element.toString().trim());
                            // list.add(element.attr("href"));
                            list.add(element.attr("href").trim());
                            // list.add(element.text().trim());

                        }

                        // Handler에게 ListView 출력을 다시 하라고 Message 전송
                        handler.sendEmptyMessage(0);

                    }catch (Exception e){
                        Log.e("HTML Parsing Exception", e.getMessage());
                    }
                }
            };
            th.start();
        });
    }
}
