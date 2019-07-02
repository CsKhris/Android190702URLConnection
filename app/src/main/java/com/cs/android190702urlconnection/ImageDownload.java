package com.cs.android190702urlconnection;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageDownload extends AppCompatActivity {

    ImageView imgView;

    // Bitmap Data를 받아서 ImageView에 출력하는 Handler
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            Bitmap image = (Bitmap)msg.obj;
            imgView.setImageBitmap(image);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_download);

        imgView = (ImageView)findViewById(R.id.imgview);

        Button imgdisp = (Button)findViewById(R.id.imgdisp);
        Button imgdw = (Button)findViewById(R.id.imgdw);

        // Button을 눌렀을 때 Image를 바로 출력하기
        imgdisp.setOnClickListener(view -> {
            Thread th = new Thread(){
                public void run(){
                    try{
                        // Image File의 URL 만들기
                        URL url = new URL("https://gif.fmkorea.com/files/attach/new/20161024/3655109/8381717/491094758/5bb08bb5d6f2b1501cb62dd4069127d5.GIF");

                        // Stream 만들기
                        InputStream is = url.openStream();

                        // Bitmap 만들기
                        Bitmap bitmap = BitmapFactory.decodeStream(is);

                        // 만든 Bitmap을 Message로 담아서 Handler에게 전송
                        Message message = new Message();
                        message.obj = bitmap;
                        handler.sendMessage(message);


                        is.close();

                    }catch (Exception e){
                        Log.e("Fail", e.getMessage());
                    }
                }
            };
            th.start();
        });

        // Image가 App 안에 존재하면 App 안에 Image를 출력하고,
        // Image가 없으면 App 안에 File로 저장하고 출력하기
        imgdw.setOnClickListener(view -> {
            Log.e("Btn Click", "Clear.");
            // Image File의 경로
            String addr = "https://gif.fmkorea.com/files/attach/new/20161024/3655109/8381717/491094758/5bb08bb5d6f2b1501cb62dd4069127d5.GIF";

            // File 이름 만들기 - 마지막 / 다음의 문자열
            int idx = addr.lastIndexOf("/");
            String imgName = addr.substring(idx + 1);

            // 위의 File이 App에 존재하는지 확인
            // App 내의 File 경로 만들기
            String path = Environment.getDataDirectory().getAbsolutePath();
            path += "/data/com.cs.android190702urlconnection/files/" + imgName;
            Log.e("path", path);

            // File의 경로를 가지고 File 객체 생성 - File의 존재 여부 확인을 휘애서
            File file = new File(path);
            if(file.exists()){
                Toast.makeText(ImageDownload.this,"File is Already Exists", Toast.LENGTH_LONG).show();

                // 존재하는 File을 가지고 Bitmap을 만들어서 handler에게 전송
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                Message msg = new Message();
                msg.obj = bitmap;
                handler.sendMessage(msg);
            }else {
                Toast.makeText(ImageDownload.this,"File is Not Exists", Toast.LENGTH_LONG).show();

                // Thread를 이용하여 Download 받은 후 File을 만들고 출력
                Thread th = new Thread(){
                    public void run(){
                        try{
                            // Image File의 경로
                            String addr = "https://gif.fmkorea.com/files/attach/new/20161024/3655109/8381717/491094758/5bb08bb5d6f2b1501cb62dd4069127d5.GIF";

                            // File 이름 만들기 - 마지막 / 다음의 문자열
                            int idx = addr.lastIndexOf("/");
                            String imgName = addr.substring(idx + 1);

                            // 위의 File이 App에 존재하는지 확인
                            // App 내의 File 경로 만들기
                            String path = Environment.getDataDirectory().getAbsolutePath();
                            path += "/data/com.cs.android190702urlconnection/files/" + imgName;
                            Log.e("File Save", path);

                            // Image File 경로와 연결
                            URL url = new URL(addr);
                            HttpURLConnection con = (HttpURLConnection)url.openConnection();

                            // Download 받는 크기
                            int len = con.getContentLength();

                            // 저장할 Byte 배열 만들기
                            byte [] laster = new byte[len];

                            // Image를 읽을 Stream과 File에 기록할 Stream 생성
                            InputStream is = con.getInputStream();
                            FileOutputStream fos = openFileOutput(imgName, 0);

                            // is로 읽은 내용을 fos에 기록
                            while (true){
                                int read = is.read(laster);
                                if(read < 0)
                                    break;
                                    fos.write(laster, 0, read);
                            }

                            is.close();
                            fos.close();
                            con.disconnect();

                            Message msg = new Message();
                            msg.obj = BitmapFactory.decodeFile(path);
                            handler.sendMessage(msg);

                        }catch (Exception e){
                            Log.e("Download Exception", e.getMessage());
                        }
                    }
                };
                th.start();
            }
        });
    }
}
