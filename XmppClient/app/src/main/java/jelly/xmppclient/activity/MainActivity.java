package jelly.xmppclient.activity;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import jelly.xmppclient.R;
import jelly.xmppclient.adaptor.MyFragmentPagerAdapter;
import jelly.xmppclient.fragment.ContactFragment;
import jelly.xmppclient.fragment.DiscoverFragment;
import jelly.xmppclient.fragment.MessageFragment;
import jelly.xmppclient.service.LoginService;
import jelly.xmppclient.util.ActivityUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageView toolbarUserImg, tabMessageImg, tabContactImg, tabDiscoverImg;
    private LinearLayout tabMessage, tabContact, tabDiscover, drawer_friend, drawer_inport, drawer_export, drawer_help, drawer_feedback, drawerSetting, drawerLogOut;
    private TextView toolbarTitle, tabMessageText, tabContactText, tabDiscoverText, drawer_username;

    private Fragment messageFragment;
    private Fragment contactFragment;
    private Fragment discoverFragment;
    private DrawerLayout drawerLayout;
    private ViewPager mViewPager;
    private ArrayList<Fragment> fragments;

    private static final int CONTACT_FRAG_ID = 0;
    private static final int MESSAGE_FRAG_ID = 1;
    private static final int DISCOVER_FRAG_ID = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        drawer_username = (TextView) findViewById( R.id.drawer_user_name );
        drawer_friend =(LinearLayout)findViewById( R.id.drawer_item_friend );
        drawer_inport =(LinearLayout)findViewById( R.id.drawer_item_inport);
        drawer_export =(LinearLayout)findViewById( R.id.drawer_item_export );
        drawer_help =(LinearLayout)findViewById( R.id.drawer_item_help );
        drawer_feedback =(LinearLayout)findViewById( R.id.drawer_item_feedback );
        drawerSetting =(LinearLayout)findViewById( R.id.drawer_item_settings );
        drawerLogOut =(LinearLayout)findViewById( R.id.drawer_item_logOut );
        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        tabMessageText = (TextView) findViewById(R.id.tab_message_text);
        tabContactText = (TextView) findViewById(R.id.tab_contact_text);
        tabDiscoverText = (TextView) findViewById(R.id.tab_discover_text);
        tabMessageImg = (ImageView) findViewById(R.id.tab_message_icon);
        tabContactImg = (ImageView) findViewById(R.id.tab_contact_icon);
        tabDiscoverImg = (ImageView) findViewById(R.id.tab_discover_icon);
        tabMessage = (LinearLayout) findViewById(R.id.tab_message);
        tabContact = (LinearLayout) findViewById(R.id.tab_contact);
        tabDiscover = (LinearLayout) findViewById(R.id.tab_discover);
        toolbarUserImg=(ImageView) findViewById( R.id.toolbar_usr_img );
        mViewPager = (ViewPager) findViewById(R.id.mainViewPager);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        fragments = new ArrayList<>();
        messageFragment = new MessageFragment();
        contactFragment = new ContactFragment();
        discoverFragment = new DiscoverFragment();
        fragments.add(contactFragment);
        fragments.add(messageFragment);
        fragments.add(discoverFragment);

        drawer_username.setText( "你好" );
        mViewPager.setAdapter( new MyFragmentPagerAdapter(getSupportFragmentManager(), fragments));
        mViewPager.setOnPageChangeListener( new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == MESSAGE_FRAG_ID) {
                    setTabImagAndTitle(MESSAGE_FRAG_ID);
                } else if (position == CONTACT_FRAG_ID) {
                    setTabImagAndTitle(CONTACT_FRAG_ID);
                } else if (position == DISCOVER_FRAG_ID) {
                    setTabImagAndTitle(DISCOVER_FRAG_ID);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        } );

        mViewPager.setCurrentItem( 0 );
        toolbarTitle.setText( "好友" );
        tabContactImg.setSelected( true );
        tabContactText.setTextColor( getResources().getColor( R.color.text_color ) );
        tabContact.setOnClickListener(this);
        tabMessage.setOnClickListener(this);
        tabDiscover.setOnClickListener(this);
        toolbarUserImg.setOnClickListener( this );
        drawer_friend.setOnClickListener( this );
        drawer_inport.setOnClickListener( this );
        drawer_export.setOnClickListener( this );
        drawer_help.setOnClickListener( this );
        drawer_feedback.setOnClickListener( this );
        drawerSetting.setOnClickListener( this );
        drawerLogOut.setOnClickListener( this );
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tab_message:
                setTabImagAndTitle( MESSAGE_FRAG_ID );
                break;
            case R.id.tab_contact:
                setTabImagAndTitle( CONTACT_FRAG_ID );
                break;
            case R.id.tab_discover:
                setTabImagAndTitle( DISCOVER_FRAG_ID );
                break;
            case R.id.toolbar_usr_img:
                if (drawerLayout.isDrawerOpen( Gravity.LEFT)) {
                    drawerLayout.closeDrawer(Gravity.LEFT);
                } else {
                    drawerLayout.openDrawer(Gravity.LEFT);
                }
                break;
            case R.id.drawer_item_friend:
                ActivityUtils.startActivity(this, AddfriendActivity.class);
                break;
            case R.id.drawer_item_export:
                ActivityUtils.startActivity(this, AddfriendActivity.class);
                break;
            case R.id.drawer_item_inport:
                ActivityUtils.startActivity(this, AddfriendActivity.class);
                break;
            case R.id.drawer_item_help:
                ActivityUtils.startActivity(this, AddfriendActivity.class);
                break;
            case R.id.drawer_item_feedback:
                ActivityUtils.startActivity(this, AddfriendActivity.class);
                break;
            case R.id.drawer_item_settings:
                ActivityUtils.startActivity(this, AddfriendActivity.class);
                break;
            case R.id.drawer_item_logOut:
                drawerLayout.closeDrawer(Gravity.LEFT);
                LoginService.getInstance().stopSelf();
                ActivityUtils.startActivity(MainActivity.this, LoginActivity.class, true);
                break;

        }
    }
    private void resetTabImge() {
        tabMessageImg.setSelected(false);
        tabContactImg.setSelected(false);
        tabDiscoverImg.setSelected(false);
    }

    private void resetTabText() {
        tabMessageText.setTextColor(getResources().getColor(R.color.gray_600));
        tabContactText.setTextColor(getResources().getColor(R.color.gray_600));
        tabDiscoverText.setTextColor(getResources().getColor(R.color.gray_600));
    }
    private void setTabImagAndTitle(int id){
        switch (id) {
            case CONTACT_FRAG_ID:
                resetTabImge();
                resetTabText();
                tabContactImg.setSelected(true);
                tabContactText.setTextColor(getResources().getColor(R.color.text_color));
                toolbarTitle.setText("好友");
                mViewPager.setCurrentItem(0);
                break;
            case MESSAGE_FRAG_ID:
                resetTabImge();
                resetTabText();
                tabMessageImg.setSelected(true);
                tabMessageText.setTextColor(getResources().getColor(R.color.text_color));
                toolbarTitle.setText("订阅");
                mViewPager.setCurrentItem(1);
                break;
            case DISCOVER_FRAG_ID:
                resetTabImge();
                resetTabText();
                tabDiscoverImg.setSelected(true);
                tabDiscoverText.setTextColor(getResources().getColor(R.color.text_color));
                toolbarTitle.setText("发现");
                mViewPager.setCurrentItem(2);
                break;
        }
    }
}
