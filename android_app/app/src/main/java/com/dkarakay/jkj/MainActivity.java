package com.dkarakay.jkj;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    public Button getBtn,langButton,addressButton;
    public TextView result,txt;
    MediaPlayer mp,mp2,mp3,mp4,mp5;
    int val = 0;
    float multip = 0;
    boolean speakIsOn = true,isTurkish = true;
    String line;
    String[] lines = {"no","no","no"};
    String[] listItems = {"English", "Türkçe"};
    String link ="";

    int lxr;
    float multiplier;
    float leftorright, leftDes,rightDes;
    String label;
    TextToSpeech t1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        result = (TextView) findViewById(R.id.hello);
        txt = (TextView) findViewById(R.id.txt);
        int volume = 100;
        final int durationMs = 1000;


        /**
         * Dil Seçim Ekranı için Dialog ekranı
         */
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Dil seçin");
        int checkedItem = 0;
        builder.setSingleChoiceItems(listItems, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0){
                    mp2= MediaPlayer.create(getApplicationContext(), R.raw.person);
                    mp3= MediaPlayer.create(getApplicationContext(), R.raw.chair_en);
                    mp4= MediaPlayer.create(getApplicationContext(), R.raw.laptop_en);
                    mp5= MediaPlayer.create(getApplicationContext(), R.raw.table_en);
                    isTurkish = false;
                    getBtn.setText("Start");
                    addressButton.setText("Address");
                    langButton.setText("Object");
                    result.setText("Object: " + "\nLocation: " + "\nMultiplier: " );
                    txt.setText("Data from host");


                }else{
                    isTurkish = true;
                    mp2= MediaPlayer.create(getApplicationContext(), R.raw.insan);
                    mp3= MediaPlayer.create(getApplicationContext(), R.raw.chair_tr);
                    mp4= MediaPlayer.create(getApplicationContext(), R.raw.laptop_tr);
                    mp5= MediaPlayer.create(getApplicationContext(), R.raw.table_tr);
                    //isTurkish = false;
                    getBtn.setText("Başla");
                    addressButton.setText("Adres");
                    langButton.setText("Nesne");
                    result.setText("Nesne: " + "\nKonum: " + "\nUzaklık Çarpanı: " );
                    txt.setText("Bilgisayarda İşlenen Veriler");

                }
                dialog.dismiss();

            }
        });
        builder.setCancelable(false);

        AlertDialog dialog = builder.create();
        dialog.show();




        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor editor = preferences.edit();


        addressButton = (Button) findViewById(R.id.button_address);
        addressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Build an AlertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.custom_dialog,null);

                // Specify alert dialog is not cancelable/not ignorable
                builder.setCancelable(false);

                // Set the custom layout as alert dialog view
                builder.setView(dialogView);

                // Get the custom alert dialog view widgets reference
                Button btn_positive = (Button) dialogView.findViewById(R.id.dialog_positive_btn);
                TextView dialog_title = (TextView) dialogView.findViewById(R.id.dialog_title);
                final EditText et_name = (EditText) dialogView.findViewById(R.id.et_name);
                if(isTurkish){
                    btn_positive.setText("Tamam");
                    dialog_title.setText("Adres girin");
                    et_name.setHint("Örnek:192.168.xx.xx:xx:xx");
                }else{
                    btn_positive.setText("OK");
                    dialog_title.setText("Enter an address");
                    et_name.setHint("Example:192.168.xx.xx:xx:xx");
                }
                // Create the alert dialog
                final AlertDialog dialog = builder.create();

                // Set positive/yes button click listener
                btn_positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Dismiss the alert dialog
                        dialog.cancel();
                        String name = et_name.getText().toString();
                        link = name.trim();
                        editor.putString("link",link);
                        editor.apply();
                    }
                });



                // Display the custom alert dialog on interface
                dialog.show();
            }
        });


        /**
         * Mod Değiştirme Butonu
         */
        langButton = (Button) findViewById(R.id.btn_lang);
        langButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(speakIsOn){
                    speakIsOn = false;
                    if(isTurkish) {
                        langButton.setText("Hikaye");
                    }else{
                        langButton.setText("Story");
                    }
                }
                else{
                    speakIsOn = true;
                    if(isTurkish) {
                        langButton.setText("Nesne");
                    }else{
                        langButton.setText("Object");
                    }
                }

            }
        });

        /**
         * Ses yükleme
         */
        mp = MediaPlayer.create(getApplicationContext(), R.raw.cen);
        mp.setLooping(false);

        //TextToSpeech
      /*  t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    // t1.setLanguage(Locale.FRENCH);

                }
            }
        });*/


        /**
         * Başla Butonu
         */
        getBtn = (Button) findViewById(R.id.getBtn);
        getBtn.setText("Başla");
        getBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new WebTest().execute();
            }
            });


        if(isTurkish){
            addressButton.setText("Adres");
            getBtn.setText("Başla");
        }else{
            addressButton.setText("Address");
            getBtn.setText("Start");
        }

        }

    /**
     * Arka Plandan Veri Çekme İşlemi için düzenlediğim AsyncTask sınıfı
      */
    private class WebTest extends AsyncTask<Void,Void,Void>{
            @Override
            protected  void onPreExecute(){

            }
            @Override
            protected Void doInBackground(Void... params) { //Accessing WebSite and fetch data
                try {
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    String name = preferences.getString("link", "");
                    StringBuilder builder = new StringBuilder();
                    //link = "http://172.20.10.7:6060";
                   Document doc = Jsoup.connect(name).get(); //Local URL
                    builder.append(doc.body().text());
                    line = builder.toString();
                    //line="person -0.8 2";
                    if(line.startsWith("#") || line.isEmpty()){ //If no object founds
                        line = "?";
                        lines[0] = "?";
                        lines[1] = "0";
                        lines[2] = "0";
                    }else{
                        lines = line.split(" ");
                        Log.i("OBJECT: ", lines[0]);
                        Log.i("RIGHT: ", lines[1]);
                        Log.i("LEFT: ", lines[2]);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
            @Override
            protected void onPostExecute(Void avoid)// After fetching data
            {
                /**
                 * Bilgisayardan gelen sonucu ekrana yazdır
                 */

            /*

                label = lines[0];
                leftorright = Float.parseFloat(lines[1]);
                multiplier = Float.parseFloat(lines[2]);

                String lr = "Sol";

                if(leftorright > 0f){
                    lr = "Sol";
                }else  if (leftorright < 0f){
                    lr = "Sağ";
                }else{
                    lr = "Ortada";
                }

                String str_obj = "Nesne: " + lines[0];
                String str_loc = "\nKonum: " + lr;
                String str_multiplier = "\nUzaklık Çarpanı: " + multiplier;

                result.setText(str_obj+str_loc+str_multiplier);
*/
            try {
                label = lines[0];
                rightDes = Float.parseFloat(lines[1]);
                leftDes = Float.parseFloat(lines[2]);

                String lr = "Sol";

                if (rightDes > leftDes) {
                    if (isTurkish) {
                        lr = "Sağ";
                    } else {
                        lr = "Right";
                    }
                    multiplier = rightDes;
                } else if (leftDes > rightDes) {
                    if (isTurkish) {
                        lr = "Sol";
                    } else {
                        lr = "Left";
                    }
                    multiplier = leftDes;
                } else {
                    if (isTurkish) {
                        lr = "Ortada";
                    } else {
                        lr = "Middle";
                    }
                    multiplier = leftDes;
                }

                String str_obj, str_loc, str_multiplier;
                if (isTurkish) {
                    str_obj = "Nesne: " + lines[0];
                    str_loc = "\nKonum: " + lr;
                    str_multiplier = "\nUzaklık Çarpanı: " + multiplier;
                } else {
                    str_obj = "Object: " + lines[0];
                    str_loc = "\nLocation: " + lr;
                    str_multiplier = "\nMultiplier: " + multiplier;
                }
                result.setText(str_obj + str_loc + str_multiplier);


                /**
                 * Sol 1 Sağ 0
                 * Eğer nesne modundaysak
                 */
                if (speakIsOn) {
                    //TTS integration
                    //   t1.speak(label, TextToSpeech.QUEUE_FLUSH, null);
                    /**
                     * Nesne merkezdeyse
                     */
                    /*if(leftorright == 1 ||leftorright == -1){
                        mp2.setVolume(1f*leftorright, 1f*leftorright);
                        mp3.setVolume(1f*leftorright, 1f*leftorright);
                        mp4.setVolume(1f*leftorright, 1f*leftorright);
                        mp5.setVolume(1f*leftorright, 1f*leftorright);
                    }
                    /**
                     * Nesne soldaysa
                     */
                   /* else if(leftorright >= 0){
                        mp2.setVolume(1f*leftorright, 0f);
                        mp3.setVolume(1f*leftorright, 0f);
                        mp4.setVolume(1f*leftorright, 0f);
                        mp5.setVolume(1f*leftorright, 0f);
                    }
                    /**
                     * Nesne sağdaysa
                     */
                   /* else if(leftorright <= 0){
                        mp2.setVolume(0, Math.abs(leftorright*1f));
                        mp3.setVolume(0, Math.abs(leftorright*1f));
                        mp4.setVolume(0, Math.abs(leftorright*1f));
                        mp5.setVolume(0, Math.abs(leftorright*1f));
                    }

                    */
                    mp2.setVolume(1f * leftDes, 1f * rightDes);
                    mp3.setVolume(1f * leftDes, 1f * rightDes);
                    mp4.setVolume(1f * leftDes, 1f * rightDes);
                    mp5.setVolume(1f * leftDes, 1f * rightDes);


                    /**
                     * Birden Fazla Dil Desteği için düzenleme
                     */
                    switch (label) {
                        case "person":
                            mp2.start();
                            break;
                        case "chair":
                            mp3.start();
                            break;
                        case "laptop":
                            mp4.start();
                            break;
                        case "table":
                            mp5.start();
                            break;

                    }
                    /**
                     * Eğer sadece ses modu açıksa
                     */
                }/*else{
                    if(leftorright == 1 ||leftorright == -1){
                        mp.setVolume(1f*leftorright, 1f*leftorright);
                    }else if(leftorright >= 0){
                        mp.setVolume(1f*leftorright, 0f);
                    }else if(leftorright <= 0){
                        mp.setVolume(0, Math.abs(leftorright*1f));
                    }
                    mp.start();
                }*/
            }catch (Exception e){
                if(isTurkish) {
                    Toast.makeText(getApplicationContext(), "Geçerli bir adres girin", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(), "Enter a proper address", Toast.LENGTH_SHORT).show();
                }
            }

                /**
                 * Verileri yeniden çek ve sistemi yeniden çalıştır.
                 */
                try {
                    //set time in mili
                    Thread.sleep(200);

                }catch (Exception e){
                    e.printStackTrace();
                }

                new WebTest().execute();
            }
        }
    }
