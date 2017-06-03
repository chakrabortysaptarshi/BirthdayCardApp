package com.example.ssapt.udacityuserinterface;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.pes.androidmaterialcolorpickerdialog.ColorPicker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {

    private String m_Text = "";
    private static int IMAGE_REQUEST_CODE = 1;
    TextView textMessage;
    ImageView imageMessage;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                actionOnWhatsAppButtonClick();
                return true;

            case R.id.action_upload_image:
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, IMAGE_REQUEST_CODE);
                return true;

            case R.id.action_textMesaage:
                createDialogPopup();
                return true;

            case R.id.action_change_color:
                createColorPicker();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        textMessage = (TextView) findViewById(R.id.BirthdayGreeting);
        imageMessage = (ImageView) findViewById(R.id.imageBirthday);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null ) {
            Uri selectedImage = data.getData();
            String[] columns = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,columns,null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(columns[0]);
            String imagePath = cursor.getString(columnIndex);
            cursor.close();

            ImageView imageView = (ImageView) findViewById(R.id.imageBirthday);
            imageView.setImageBitmap(BitmapFactory.decodeFile(imagePath));
        }
    }

    void createColorPicker() {
        final ColorPicker cp = new ColorPicker(MainActivity.this);
        cp.show();

    /* On Click listener for the dialog, when the user select the color */
        Button okColor = (Button)cp.findViewById(R.id.okColorButton);

        okColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textMessage.setTextColor(cp.getColor());
                cp.dismiss();
            }
        });
    }

    void actionOnWhatsAppButtonClick() {
        File filepath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        textMessage.buildDrawingCache();
        Bitmap mainBitmap = ((BitmapDrawable)imageMessage.getDrawable()).getBitmap().copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(mainBitmap);
        canvas.drawBitmap(textMessage.getDrawingCache(), 0, 0, null);

        String filePath = filepath.getAbsolutePath()+"/test32.jpg";
        try {
            mainBitmap.compress(Bitmap.CompressFormat.JPEG, 95, new FileOutputStream(filePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        File file = new File(filePath);

        if(file.exists()) {
            Toast t1 = Toast.makeText(getApplicationContext(),"File Exists !!", Toast.LENGTH_SHORT);
            t1.show();
        } else {
            Toast t2= Toast.makeText(getApplicationContext(),"No File  !!", Toast.LENGTH_SHORT);
            t2.show();
        }

        Uri uri = Uri.fromFile(new File(filePath));
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.setPackage("com.whatsapp");
        sendIntent.setType("image/jpg");
        sendIntent.putExtra(Intent.EXTRA_STREAM,uri);
        if(sendIntent.resolveActivity(getPackageManager()) != null)
            startActivity(sendIntent);
        else
            System.out.println("No whatsapp");
    }

    void createDialogPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create Message....");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_Text = input.getText().toString();
                textMessage.setText(m_Text.toString());
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }
}
