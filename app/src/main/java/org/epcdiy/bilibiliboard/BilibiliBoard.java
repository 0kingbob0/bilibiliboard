package org.epcdiy.bilibiliboard;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import org.epcdiy.bilibiliboard.adapter.AvContent;
import org.epcdiy.bilibiliboard.adapter.AvListViewAdapter;
import org.epcdiy.bilibiliboard.adapter.FansAndAvContent;
import org.w3c.dom.Text;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class BilibiliBoard extends AppCompatActivity {
    private final boolean isPadSpecial=true;
    private String reqUrl;
    private ListView avListView;
    private ListView avListViewTitle;
    private TextView fans;
    private TextView fansup;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=this;
        //去除标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //去除状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.
                FLAG_KEEP_SCREEN_ON);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // 设置页面全屏显示
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(lp);
        }

//        // 设置页面全屏显示
//        final View decorView = getWindow().getDecorView();
//        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        setContentView(R.layout.activity_bilibili_board);
        hideSystemNavigationBar();
        initUI();
        handler.sendEmptyMessage(EVENT_ENUM.BILIBILIDATA);
    }

    private void initUI()
    {
        avListView = findViewById(R.id.lv_avlist);
        avListView.setAdapter(new AvListViewAdapter(new ArrayList<AvContent>(), this));
        avListViewTitle=findViewById(R.id.lv_avlistTitle);
        avListViewTitle.setAdapter(new AvListViewAdapter(new ArrayList<AvContent>(), this));
        fans=findViewById(R.id.fansnum);
        fansup=findViewById(R.id.fansup);
        if(isPadSpecial)
        {
            reqUrl="http://192.168.0.223:23333/getBilibiliAvFans";
        }
        else
        {
            reqUrl="http://home.epcdiy.org:23333/getBilibiliAvFans";
        }
    }
    public interface EVENT_ENUM
    {
        Integer BILIBILIDATA = 1;
        Integer BILIBILIDATATOUI = 2;
    }
    // 获取网页的html源代码
    public static String getHtml(String path) throws Exception {
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5000);
        conn.setRequestMethod("GET");
        if (conn.getResponseCode() == 200) {
            InputStream in = conn.getInputStream();
            byte[] data = StreamTool.read(in);
            String html = new String(data, "UTF-8");
            return html;
        }
        return null;
    }

    public Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg)
        {
            if (msg.what == EVENT_ENUM.BILIBILIDATA)
            {
                Thread thread = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try  {
                            Message newmsg=new Message();
                            Bundle bundle=new Bundle();
                            try {
                                String jsondata=getHtml(reqUrl);
                                bundle.putString("json",jsondata);
                            }
                            catch (Exception e)
                            {
                                bundle.putString("json","");
                                e.printStackTrace();
                            }
                            newmsg.setData(bundle);
                            newmsg.what=(EVENT_ENUM.BILIBILIDATATOUI);
                            handler.sendMessage(newmsg);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
                Message newmsg=new Message();
                newmsg.what=(EVENT_ENUM.BILIBILIDATA);
                handler.sendMessageDelayed(newmsg,30000);
            }
            else if(msg.what==EVENT_ENUM.BILIBILIDATATOUI)
            {
                try {
                    Bundle bundle = msg.getData();
                    String json = bundle.getString("json");
                    Gson gson = new Gson();
                    FansAndAvContent fansAndAvContent = gson.fromJson(json, FansAndAvContent.class);
                    fans.setText(String.valueOf(fansAndAvContent.fans.fansnum));
                    fansup.setText(String.valueOf(fansAndAvContent.fans.fansnumup));
                    AvContent firstContent=new AvContent();
                    firstContent.title="名称";
                    firstContent.comment="评论";
                    firstContent.commentup="↑";
                    firstContent.favorites="收藏";
                    firstContent.favoritesup="↑";
                    firstContent.play="播放";
                    firstContent.playup="↑";
                    firstContent.video_review="弹幕";
                    firstContent.video_reviewup="↑";
                    List<AvContent> avData=new ArrayList<>();
                    avData.add(firstContent);
                    avListViewTitle.setAdapter(new AvListViewAdapter(avData, context));
                    avListView.setAdapter(new AvListViewAdapter(fansAndAvContent.avData, context));
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    };
    @Override
    protected void onResume()
    {
        super.onResume();
        hideSystemNavigationBar();
//        AvContent avContent=new AvContent();
//        avContent.title="程序员教你如何不加班";
//        avContent.play=99999;
//        avContent.playup=9999;
//        avContent.video_review=1500;
//        avContent.video_reviewup=1500;
//        avContent.comment=99999;
//        avContent.commentup=9999;
//        avContent.favorites=99999;
//        avContent.favoritesup=9999;
//        ArrayList testlist=new ArrayList<AvContent>();
//        testlist.add(avContent);
//        avListView.setAdapter(new AvListViewAdapter(testlist, this));
    }
    private void hideSystemNavigationBar() {
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) {
            View view = this.getWindow().getDecorView();
            view.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }
}
