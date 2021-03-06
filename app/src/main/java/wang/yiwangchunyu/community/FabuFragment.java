package wang.yiwangchunyu.community;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;
import wang.yiwangchunyu.community.Task.Bimp;
import wang.yiwangchunyu.community.Task.FileUtils;
import wang.yiwangchunyu.community.constant.UrlConstance;
import wang.yiwangchunyu.community.dataStructures.TaskPublishingInfo;
import wang.yiwangchunyu.community.utils.Utils;
import wang.yiwangchunyu.community.dataStructures.TasksResponse;
import wang.yiwangchunyu.community.webService.androidAsyncHttp.MyCLient;

import static android.app.Activity.RESULT_OK;

/**
 * Created by XinyuJiang on 2018/3/11.
 */

public class FabuFragment extends Fragment implements  OnClickListener{

    private static final String TAG = "FabuFragment";
    private TaskPublishingInfo taskPublishingInfo;//任务实例
    private GridView noScrollgridview;
    private GridAdapter adapter;
    private TextView activity_selectimg_send;
    private String localTempImgDir = "tempDir";
    private File f;

    private EditText item_titile;
    private EditText item_restriction;
    private EditText item_content;
    private EditText item_commission;
    private Button Release;
    private ImageView add_photo;
    private ImageView picture;
    private View view;
    private Uri imageUri;
    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;
    private Bitmap tempbitmap;
    private PopupWindows popupWindows;

    /*private String title;
    private String content;
    private int commission;
    private String restriction;
    private int viewed;
    private int liked;
    private Date publish_time;
*/


    public FabuFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_fabu, container, false);
        taskPublishingInfo = new TaskPublishingInfo();
        Init();

        return view;
    }

    public void Init(){

        item_titile = (EditText) view.findViewById(R.id.item_title);
        item_restriction = (EditText) view.findViewById(R.id.item_restriction);
        item_content = (EditText) view.findViewById(R.id.item_content);
        item_commission = (EditText) view.findViewById(R.id.item_commision);
        Release = (Button) view.findViewById(R.id.activity_selectimg_send);
        add_photo = (ImageView) view.findViewById(R.id.add_photo);
        add_photo.setOnClickListener(this);
        Release.setOnClickListener(this);
        picture = (ImageView) view.findViewById(R.id.picture);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.activity_selectimg_send:
                //传递数据
            {
                String jgnbr = item_commission.getText().toString();
                if(Utils.isNumeric(jgnbr)){
                    taskPublishingInfo.setUserId(ItLanBaoApplication.getInstance().getBaseUser().getUserid());
                    taskPublishingInfo.setCategory("所有");
                    taskPublishingInfo.setTitle(item_titile.getText().toString());
                    taskPublishingInfo.setRestriction(item_restriction.getText().toString());
                    taskPublishingInfo.setContent(item_content.getText().toString());
                    taskPublishingInfo.setCommission(Integer.parseInt(item_commission.getText().toString()));
                    if(tempbitmap!=null){
                        ArrayList<Bitmap> images = new ArrayList<Bitmap>();
                        images.add(tempbitmap);
                        taskPublishingInfo.setImages(images);
                    }
                    Intent intent_Fabu_to_Two = new Intent(getActivity(),MainActivity.class) ;    //切换Login Activity至User Activity
                    startActivity(intent_Fabu_to_Two);
                    getActivity().finish();
                    upload(taskPublishingInfo, TasksResponse.class, this);}
                else
                    {Toast.makeText(view.getContext(),"薪资必须为数字！",Toast.LENGTH_SHORT).show();}

                break;
            }

            case R.id.add_photo:
            { // 一个自定义的布局，作为显示的内容
                popupWindows = new PopupWindows(view.getContext());
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (popupWindows!= null && popupWindows.isShowing())
            popupWindows.dismiss();
        popupWindows = null;
    }

    public class PopupWindows extends PopupWindow {

        public PopupWindows(Context mContext) {

            View view = View
                    .inflate(mContext, R.layout.item_popupwindows, null);
            view.startAnimation(AnimationUtils.loadAnimation(mContext,
                    R.anim.fade_ins));
            LinearLayout ll_popup = (LinearLayout) view
                    .findViewById(R.id.ll_popup);
            ll_popup.startAnimation(AnimationUtils.loadAnimation(mContext,
                    R.anim.push_bottom_in_2));

            setWidth(LayoutParams.FILL_PARENT);
            setHeight(LayoutParams.FILL_PARENT);
            setBackgroundDrawable(new BitmapDrawable());
            setFocusable(true);
            setOutsideTouchable(true);
            setContentView(view);
            showAsDropDown(view);
            update();

            Button bt1 = (Button) view
                    .findViewById(R.id.item_popupwindows_camera);
            Button bt2 = (Button) view
                    .findViewById(R.id.item_popupwindows_Photo);
            Button bt3 = (Button) view
                    .findViewById(R.id.item_popupwindows_cancel);
            bt1.setOnClickListener(new OnClickListener() {//拍照
                public void onClick(View v) {
                    photo();
                    dismiss();
                }
            });
            bt2.setOnClickListener(new OnClickListener() {//相册
                public void onClick(View v) {

                    if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(getActivity(),new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE },1);
                    }else {
                        openAlbum();
                    }

                    dismiss();
                }
            });
            bt3.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    dismiss();
                }
            });

        }
    }


    /*public void Init() {
        noScrollgridview = (GridView) getActivity().findViewById(R.id.noScrollgridview);
        noScrollgridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
        adapter = new GridAdapter(this.getActivity());
        adapter.update();
        noScrollgridview.setAdapter(adapter);
        noScrollgridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                if (arg2 == Bimp.bmp.size()) {
                    new PopupWindows(getActivity(), noScrollgridview);
                } else {
                    Intent intent = new Intent(getActivity(),
                            PhotoActivity.class);
                    intent.putExtra("ID", arg2);
                    startActivity(intent);
                }
            }
        });
        activity_selectimg_send = (TextView) getActivity().findViewById(R.id.activity_selectimg_send);
        activity_selectimg_send.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                List<String> list = new ArrayList<String>();
                for (int i = 0; i < Bimp.drr.size(); i++) {
                    String Str = Bimp.drr.get(i).substring(
                            Bimp.drr.get(i).lastIndexOf("/") + 1,
                            Bimp.drr.get(i).lastIndexOf("."));
                    list.add(FileUtils.SDPATH+Str+".JPEG");
                    File fii=new File(list.get(i).toString());
                    Log.d("Pu", "str=="+fii.getName());
                    Log.d("Pu", "str=="+list.get(i).toString());
                }
                // 高清的压缩图片全部就在  list 路径里面了
                // 高清的压缩过的 bmp 对象  都在 Bim.bmp里面
                // 完成上传服务器后 .........
                FileUtils.deleteDir();
            }
        });
    }*/

    public class GridAdapter extends BaseAdapter {
        private LayoutInflater inflater; // 视图容器
        private int selectedPosition = -1;// 选中的位置
        private boolean shape;

        public boolean isShape() {
            return shape;
        }

        public void setShape(boolean shape) {
            this.shape = shape;
        }

        public GridAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        public void update() {
            loading();
        }

        public int getCount() {
            return (Bimp.bmp.size() + 1);
        }

        public Object getItem(int arg0) {

            return null;
        }

        public long getItemId(int arg0) {

            return 0;
        }

        public void setSelectedPosition(int position) {
            selectedPosition = position;
        }

        public int getSelectedPosition() {
            return selectedPosition;
        }

        /**
         *
         * ListView Item设置
         */
        public View getView(int position, View convertView, ViewGroup parent) {
            final int coord = position;
            ViewHolder holder = null;
            if (convertView == null) {

                convertView = inflater.inflate(R.layout.item_published_grida,
                        parent, false);
                holder = new ViewHolder();
                holder.image = (ImageView) convertView
                        .findViewById(R.id.item_grida_image);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (position == Bimp.bmp.size()) {
                holder.image.setImageBitmap(BitmapFactory.decodeResource(
                        getResources(), R.drawable.icon_addpic_unfocused));
                if (position == 9) {
                    holder.image.setVisibility(View.GONE);
                }
            } else {
                holder.image.setImageBitmap(Bimp.bmp.get(position));
            }

            return convertView;
        }

        public class ViewHolder {
            public ImageView image;
        }

        Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        adapter.notifyDataSetChanged();
                        break;
                }
                super.handleMessage(msg);
            }
        };

        public void loading() {
            new Thread(new Runnable() {
                public void run() {
                    while (true) {
                        if (Bimp.max == Bimp.drr.size()) {
                            Message message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);
                            break;
                        } else {
                            try {
                                String path = Bimp.drr.get(Bimp.max);
                                System.out.println(path);
                                Bitmap bm = Bimp.revitionImageSize(path);
                                Bimp.bmp.add(bm);
                                String newStr = path.substring(
                                        path.lastIndexOf("/") + 1,
                                        path.lastIndexOf("."));
                                FileUtils.saveBitmap(bm, "" + newStr);
                                Bimp.max += 1;
                                Message message = new Message();
                                message.what = 1;
                                handler.sendMessage(message);
                            } catch (IOException e) {

                                e.printStackTrace();
                            }
                        }
                    }
                }
            }).start();
        }
    }

    public String getString(String s) {
        String path = null;
        if (s == null)
            return "";
        for (int i = s.length() - 1; i > 0; i++) {
            s.charAt(i);
        }
        return path;
    }


    private static final int TAKE_PICTURE = 0x000000;
    private String path = "";

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case TAKE_PHOTO:
                if(resultCode == RESULT_OK){
                    try{
                        Bitmap bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(imageUri));
                        tempbitmap = bitmap;
                        picture.setImageBitmap(bitmap);
                    }catch(FileNotFoundException e){
                        Toast.makeText(this.getActivity(), "拍照不成功", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK){
                    //判断手机系统版本号
                    if (Build.VERSION.SDK_INT >= 19){
                        //4.4及以上系统使用这个方法处理图片
                        handleImageOnKitKat(data);
                    }else {
                        //4.4以下系统使用这个方法处理图片
                        handleImageBeforeKitKat(data);
                    }
                }
            default:
                break;
        }
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data){
        String imagePath = null;
        Uri uri = data.getData();
        if(DocumentsContract.isDocumentUri(getContext(),uri)){
            //如果是document类型的Uri,则通过document id 处理
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id = docId.split(":")[1];//解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        }else if ("content".equalsIgnoreCase(uri.getScheme())){
            //如果是content类型的Uri,则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())){
            //如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        displayImage(imagePath);//根据图片路径显示图片
    }

    private void handleImageBeforeKitKat(Intent data){
        Uri uri = data.getData();
        String imagePath = getImagePath(uri,null);
        displayImage(imagePath);
    }

    private String getImagePath(Uri uri, String selection){
        String path = null;
        //通过Uri和selection来获取真实的图片路径
        Cursor cursor = getActivity().getContentResolver().query(uri,null,selection,null,null);
        if(cursor != null){
            if (cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath){
        if (imagePath != null){
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            tempbitmap = bitmap;
            picture.setImageBitmap(bitmap);
        }else {
            Toast.makeText(getContext(),"failed to get image",Toast.LENGTH_SHORT).show();
        }
    }

    public void photo() {//拍照
//		Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//		File file = new File(Environment.getExternalStorageDirectory()
//				+ "/myimage/", String.valueOf(System.currentTimeMillis())
//				+ ".jpg");
//		path = file.getPath();
//		Uri imageUri = Uri.fromFile(file);
//		openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//		startActivityForResult(openCameraIntent, TAKE_PICTURE);

        File dir = new File(getActivity().getExternalCacheDir(),"output_image.jpg");
        try{
            if(dir.exists()){
                dir.delete();
            }
            dir.createNewFile();
        }catch (IOException e){
            e.printStackTrace();
        }

        if(Build.VERSION.SDK_INT >= 24){
            imageUri = FileProvider.getUriForFile(getContext(),"wang.yiwangchunyu.community.fileprovider",dir);
        }else{
            imageUri = Uri.fromFile(dir);
        }
        //启动相机程序
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, TAKE_PHOTO);

    }


    private void openAlbum(){
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,CHOOSE_PHOTO);//打开相册

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openAlbum();
                } else {
                    Toast.makeText(getActivity() , "You denied the permission",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }

    }
    //TODO:发布调用接口
    public void upload(TaskPublishingInfo task, Class<TasksResponse> clazz, FabuFragment callback){

        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("user_id", task.getUserId());
        parameter.put("title", task.getTitle());
        parameter.put("restriction", task.getRestriction());
        parameter.put("content", task.getContent());
        parameter.put("category",task.getCategory());
        parameter.put("commission", String.valueOf(task.getCommission()));

        RequestParams params = new RequestParams(parameter);

        int filecount=0;
        if(task.getImages()!=null){
            for(Bitmap bitmap: task.getImages()){
                filecount++;
                params.add("image"+filecount, Utils.bitmap2String(bitmap));
                Log.d(TAG, "upload: "+"image"+filecount+"->"+ Utils.bitmap2String(bitmap));
            }
        }

        params.add("filecount",String.valueOf(filecount));

        MyCLient.post(UrlConstance.KEY_PUBLISH_INFO, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d(TAG, "publish post onSuccess statusCode: "+"\n"+statusCode);
                Log.d(TAG, "headers: "+headers);
                Log.d(TAG, "responseBody: "+ responseBody);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d(TAG, "publish post onFailure statusCode: "+"\n"+statusCode);
                Log.d(TAG, "headers: "+headers);
                Log.d(TAG, "responseBody: "+ responseBody);
            }
        });

    }
}
