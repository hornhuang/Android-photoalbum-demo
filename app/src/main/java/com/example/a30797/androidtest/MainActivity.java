package com.example.a30797.androidtest;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //list表
    private List<Informations> informationsList01 = new ArrayList<>();
    //当前消息列表
    ListView list01 ;
    //消息发送栏
    EditText editText01 ;
    //存放图片
    ImageView imageView01;
    //消息发送按钮
    Button button01_send ;
    //记录数组长度
    int arr_num = 0;
    //定义一个数组
    String[] arr1 = new String[arr_num];
    //从相册获得图片
    Bitmap bitmap;
    //判断返回到的Activity
    private static final int IMAGE_REQUEST_CODE = 0;
    //图片路径
    private String path ;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if((Integer)msg.obj==0){
                imageView01.setImageBitmap(bitmap);
            }
            super.handleMessage(msg);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list01 = (ListView) findViewById(R.id.list1);
        editText01 = (EditText) findViewById(R.id.ifo_edit);
        imageView01 = (ImageView) findViewById(R.id.ifo_image);
        button01_send = (Button) findViewById(R.id.send);


        imageView01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(MainActivity.this,new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },1);
                }
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, IMAGE_REQUEST_CODE);
            }
        });

        button01_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((BitmapDrawable) ((ImageView) imageView01).getDrawable()).getBitmap() != null
                        || editText01.getText().toString() != null){
                    Informations xiaochouyu = new Informations(
                            ((BitmapDrawable) ((ImageView) imageView01).getDrawable()).getBitmap(),
                            editText01.getText().toString());
                    informationsList01.add(xiaochouyu);
                    EssayAdapter adapter = new EssayAdapter(MainActivity.this,
                            R.layout.array_list,informationsList01);
                    list01.setAdapter(adapter);
                    editText01.setText("");
                    imageView01.setImageBitmap(null);
                    imageView01.setImageResource(R.drawable.addphoto);
                }
            }
        });
    }

    /*定义一个Handler，定义延时执行的行为*/
    public  void chnage(){
        new Thread(){
            @Override
            public void run() {
                while ( bitmap == null ){
                    bitmap = BitmapFactory.decodeFile(path);
                    Log.v("qwe","123");
                }
                Message message = handler.obtainMessage();
                message.obj = 0;
                handler.sendMessage(message);
            }
        }.start();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        //在相册里面选择好相片之后调回到现在的这个activity中
        switch (requestCode) {
            case IMAGE_REQUEST_CODE://这里的requestCode是我自己设置的，就是确定返回到那个Activity的标志
                if (resultCode == RESULT_OK) {//resultcode是setResult里面设置的code值
                    try {
                        Uri selectedImage = data.getData(); //获取系统返回的照片的Uri
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        Cursor cursor = getContentResolver().query(selectedImage,
                                filePathColumn, null, null, null);//从系统表中查询指定Uri对应的照片
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        path = cursor.getString(columnIndex);  //获取照片路径
                        cursor.close();

                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 1;
                        bitmap = BitmapFactory.decodeFile(path,options);
                        imageView01.setImageBitmap(bitmap);
                        chnage();
                        Toast.makeText(MainActivity.this,path,Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        // TODO Auto-generatedcatch block
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    @TargetApi(19)
    private void handleImageOmKitKat(Intent data){
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this,uri)){
            //如果document类型是U日，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id = docId.split(":")[1];//解析出数字格式id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                imagePath = getImagePath(contentUri,null);
            }
        }else if ("content".equalsIgnoreCase(uri.getScheme())){
            //如果是普通类型 用普通方法处理
            imagePath = getImagePath(uri,null);
        }else if ("file".equalsIgnoreCase(uri.getScheme())){
            //如果file类型位uri直街获取图片路径即可
            imagePath = uri.getPath();
        }
        displayImage(imagePath);
    }

    private  void handleImageBeforeKitKat(Intent data){
        Uri uri = data.getData();
        String imagePath = getImagePath(uri,null);
        displayImage(imagePath);
    }

    private String getImagePath(Uri uri, String selection){
        String path = null;
        //通过Uri和selection来获取真实图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null){
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
            imageView01.setImageBitmap(bitmap);
        }else {
            Toast.makeText(MainActivity.this,"fail to get image",Toast.LENGTH_SHORT).show();
        }
    }
}
