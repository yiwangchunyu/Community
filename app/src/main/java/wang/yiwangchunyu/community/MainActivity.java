package wang.yiwangchunyu.community;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView title, item_weixin, item_tongxunlu, item_faxian, item_me, item_fabu;
    private ViewPager vp;
    private OneFragment twoFragment;
    private TwoFragment oneFragment;
    private ThreeFragment threeFragment;
    private FouthFragment fouthFragmen;
    private FabuFragment fabuFragment;
    private ArrayList<Fragment> mFragmentList = new ArrayList<Fragment>();
    private FragmentAdapter mFragmentAdapter;
    private String view_blue = "#4876FF";

    String[] titles = new String[]{"任务", "通知", "发布","发现", "我的"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去除工具栏
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        initViews();

        mFragmentAdapter = new FragmentAdapter(this.getSupportFragmentManager(), mFragmentList);
        vp.setOffscreenPageLimit(5);//ViewPager的缓存为4帧
        vp.setAdapter(mFragmentAdapter);
        vp.setCurrentItem(0);//初始设置ViewPager选中第一帧
        item_weixin.setTextColor(Color.parseColor(view_blue));

        //ViewPager的监听事件
        vp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                /*此方法在页面被选中时调用*/
                title.setText(titles[position]);
                changeTextColor(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                /*此方法是在状态改变的时候调用，其中arg0这个参数有三种状态（0，1，2）。
                arg0 ==1的时辰默示正在滑动，
                arg0==2的时辰默示滑动完毕了，
                arg0==0的时辰默示什么都没做。*/
            }
        });
    }

    /**
     * 初始化布局View
     */

    public void toTaskDetails()
    {
        Intent intent = new Intent(MainActivity.this, TaskDetails.class);
        startActivity(intent);
    }

    private void initViews() {
        title = (TextView) findViewById(R.id.title);
        item_weixin = (TextView) findViewById(R.id.item_weixin);
        item_tongxunlu = (TextView) findViewById(R.id.item_tongxunlu);
        item_fabu = (TextView)findViewById(R.id.item_fabu);
        item_faxian = (TextView) findViewById(R.id.item_faxian);
        item_me = (TextView) findViewById(R.id.item_me);

        item_weixin.setOnClickListener(this);
        item_tongxunlu.setOnClickListener(this);
        item_faxian.setOnClickListener(this);
        item_me.setOnClickListener(this);
        item_fabu.setOnClickListener(this);

        vp = (ViewPager) findViewById(R.id.mainViewPager);
        oneFragment = new TwoFragment();
        twoFragment = new OneFragment();
        threeFragment = new ThreeFragment();
        fouthFragmen = new FouthFragment();
        fabuFragment = new FabuFragment();

        //给FragmentList添加数据
        mFragmentList.add(oneFragment);
        mFragmentList.add(twoFragment);
        mFragmentList.add(fabuFragment);
        mFragmentList.add(threeFragment);
        mFragmentList.add(fouthFragmen);
    }

    /**
     * 点击底部Text 动态修改ViewPager的内容
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_weixin:
                vp.setCurrentItem(0, true);
                break;
            case R.id.item_tongxunlu:
                vp.setCurrentItem(1, true);
                break;
            case R.id.item_faxian:
                vp.setCurrentItem(3, true);
                break;
            case R.id.item_me:
                vp.setCurrentItem(4, true);
                break;
            case R.id.item_fabu:
                vp.setCurrentItem(2,true);
        }
    }


    public class FragmentAdapter extends FragmentPagerAdapter {

        List<Fragment> fragmentList = new ArrayList<Fragment>();

        public FragmentAdapter(FragmentManager fm, List<Fragment> fragmentList) {
            super(fm);
            this.fragmentList = fragmentList;
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

    }

    /*
     *由ViewPager的滑动修改底部导航Text的颜色
     */
    private void changeTextColor(int position) {
        if (position == 0) {
            item_weixin.setTextColor(Color.parseColor(view_blue));
            item_tongxunlu.setTextColor(Color.parseColor("#000000"));
            item_faxian.setTextColor(Color.parseColor("#000000"));
            item_me.setTextColor(Color.parseColor("#000000"));
            item_fabu.setTextColor(Color.parseColor("#000000"));
        } else if (position == 1) {
            item_tongxunlu.setTextColor(Color.parseColor(view_blue));
            item_weixin.setTextColor(Color.parseColor("#000000"));
            item_faxian.setTextColor(Color.parseColor("#000000"));
            item_me.setTextColor(Color.parseColor("#000000"));
            item_fabu.setTextColor(Color.parseColor("#000000"));

        } else if (position == 2) {
            item_fabu.setTextColor(Color.parseColor(view_blue));
            item_weixin.setTextColor(Color.parseColor("#000000"));
            item_tongxunlu.setTextColor(Color.parseColor("#000000"));
            item_faxian.setTextColor(Color.parseColor("#000000"));
            item_me.setTextColor(Color.parseColor("#000000"));

        }else if (position == 3) {
            item_faxian.setTextColor(Color.parseColor(view_blue));
            item_weixin.setTextColor(Color.parseColor("#000000"));
            item_tongxunlu.setTextColor(Color.parseColor("#000000"));
            item_me.setTextColor(Color.parseColor("#000000"));
            item_fabu.setTextColor(Color.parseColor("#000000"));

        } else if (position == 4) {
            item_me.setTextColor(Color.parseColor(view_blue));
            item_weixin.setTextColor(Color.parseColor("#000000"));
            item_tongxunlu.setTextColor(Color.parseColor("#000000"));
            item_faxian.setTextColor(Color.parseColor("#000000"));
            item_fabu.setTextColor(Color.parseColor("#000000"));

        }

    }
}