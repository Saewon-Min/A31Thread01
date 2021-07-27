package com.kosmo.a31thread01;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "KOSMO123";

    TextView textView1;
    Button button1;
    ProgressHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView1 = findViewById(R.id.textView1);
        button1 = findViewById(R.id.button1);

        // 앱이 실행되면 ProgressHandler 객체를 생성한다.
        handler = new ProgressHandler();


    }


    /*
    버튼을 눌렀을때 실행되는 메소드
     */
    public void onBtn1Clicked(View v){
        // 버튼을 누르면 두번 누를수 없도록 비활성화 시킨다.
        button1.setEnabled(false);
        /*
        쓰레드의 메인메소드는 run()이지만, start()를 통해
        간접적으로 호출해야 쓰레드가 생성된다. 만약 run()을
        호출하면 단순한 실행만될뿐 쓰레드는 생성되지 않는다.
         */
        RequestThread thread = new RequestThread();
        thread.start(); // 쓰레드 시작
    }

    class RequestThread extends Thread{

        @Override
        public void run() {
            for(int i=0; i<20; i++){
                Log.d(TAG, "Request Thread "+i);

                /*
                쓰레드에서 메인쓰레드의  UI(위젯)으로 접근은 불가능하다.
                    : 해당 쓰레드에서 메인 쓰레드의 UI를 접근하면 ANR이
                    발생된다. 이떄 앱은 강제적으로 종료된다. 외부 쓰레드의
                    접근이 메인쓰레드의 동작에 영향을 미칠수 있으므로 접근을
                    원천적으로 제한하기 때문이다.

                ANR이란?
                    ANR은 Application Not Responding의 약자로 그대로 해석해보면 의미를
                    쉽게 파악할 수 있다. '애플리케이션이 응답하지 않는다.' 인 것이다.
                    이 에러의 원인은 Main Thread(UI Thread)가 일정 시간
                    어떤 Task에 잡혀 있으면 발생하게 된다.

                 */
                // 아래 코드는 예외 발생됨
                //textView1.setText("Request Thread "+i);

                // 핸들러 객체로 전달할 메세지(데이터) 작성
                Message msg = handler.obtainMessage();

                // 핸들러로 전달할 메세지는 번들 객체를 사용하여 저장한다.
                // Bundle은 Map컬렉션과 비슷함
                Bundle bundle = new Bundle();
                bundle.putString("data1", "Request Thread"+i);
                bundle.putString("data2", String.valueOf(i));
                // 저장한 데이터를 세팅한 후 핸들러로 전송한다.
                msg.setData(bundle);
                handler.sendMessage(msg);

                // 쓰레드의 반복을 1초씩 Block상태로 만들어준다.
                try{
                    Thread.sleep(1000);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }

            }
        }
    }//// RequestThread


    /*
    핸들러 클래스 생성
        : 메인쓰레드에서 생성한 UI(위젯)는 다른 쓰레드에서 접근할 수 없으므로
        핸들러 객체를 사용해서 간접적으로 접근한다. 메세지를 전달하여
        해당 위젯에 접근하게 된다.
     */
    class ProgressHandler extends Handler{

        @Override
        public void handleMessage(Message msg) {

            // Bundle 객체를 통해 전달된 메세지(데이터)를 확인한다.
            Bundle bundle = msg.getData();
            String data1 = bundle.getString("data1");
            String data2 = bundle.getString("data2");

            // 핸들러 객체내에서 UI를 접근하여 텍스트를 설정한다.
            textView1.setText(data1);

            /*
            20번 반복한 후 버튼과 텍스트뷰를 원상태로 설정한다.
            쓰레드의 동작이 끝나기 전에는 버튼을 누를수 없도록
            비활성화 상태를 유지한다.
             */
            if(data2.equals("19")){
                textView1.setText("쓰레드 테스트");
                button1.setEnabled(true); // 활성화
            }else{
                button1.setEnabled(false); // 비활성화
            }


        }
    }//// ProgressHandler


}