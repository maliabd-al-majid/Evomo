package com.example.mohamednadeem.dsdv;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


/**
 * Created by ahmed on 22/04/2016.
 */
public class IPandPort extends Activity {
    EditText ipTxt,portTxt;
    Button doneBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ip);
        ipTxt = (EditText)findViewById(R.id.ipText);
        portTxt = (EditText)findViewById(R.id.portText);
        doneBtn = (Button)findViewById(R.id.button);

        ipTxt.setText(Nums.IP);
        portTxt.setText(Nums.PORT);

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Nums.IP = ipTxt.getText().toString();
                Nums.PORT = portTxt.getText().toString();
                //DownloadImageTask d = new DownloadImageTask(null);
                //d.execute("http://"+Nums.IP+":"+Nums.PORT+"/image/get.php?id=1");
                /*if(!DownloadImageTask.isWorking){
                    Toast.makeText(getApplicationContext(),"your IP or Port is not working...", Toast.LENGTH_SHORT).show();
                }else{*/
                    Intent intent = new Intent(IPandPort.this,MainActivity.class);
                    startActivity(intent);
                //}
            }
        });
    }


}
