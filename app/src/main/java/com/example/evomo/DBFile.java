package com.example.evomo;

import android.content.Context;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;


import static android.content.Context.MODE_APPEND;
import static android.content.Context.MODE_PRIVATE;

public class DBFile {
    private static final String FILE_NAME = "RecordingLog.txt";// File Name to Be Changed

    public String Load(Context context) {
        FileInputStream fis = null;
        StringBuilder sb = new StringBuilder();
        try {
            fis = context.openFileInput(FILE_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String text;

            while ((text = br.readLine()) != null) {
                sb.append(text).append("\n");
            }

            return sb.toString();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

    public boolean Save(Context context, String text) {

        FileOutputStream fos = null;

        try {
            fos = context.openFileOutput(FILE_NAME, MODE_APPEND);
            fos.write(text.getBytes());

            return true;
            //   Toast.makeText(this, "Saved to " + getFilesDir() + "/" + FILE_NAME,
            //       Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public boolean Clear(Context context) {



        try {
            context.deleteFile(FILE_NAME);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
