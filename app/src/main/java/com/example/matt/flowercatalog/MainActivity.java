package com.example.matt.flowercatalog;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.matt.flowercatalog.model.Flower;
import com.example.matt.flowercatalog.parsers.FlowerJSONParser;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {
   TextView output;
   ProgressBar pb;
   List<MyTask> tasks;

   List<Flower> flowerList;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      output = (TextView) findViewById(R.id.textView);
      output.setMovementMethod(new ScrollingMovementMethod());

      pb = (ProgressBar) findViewById(R.id.progressBar);
      pb.setVisibility(View.INVISIBLE);

      tasks = new ArrayList<MyTask>();
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      getMenuInflater().inflate(R.menu.main, menu);
      return true;
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      if (item.getItemId() == R.id.action_do_task) {
         if(isOnline()){
            requestData("http://services.hanselandpetal.com/secure/flowers.json");
            //requestData("http://services.hanselandpetal.com/feeds/flowers.json");
            //requestData("http://services.hanselandpetal.com/feeds/flowers.xml");
         }else{
            Toast.makeText(this, "Network error", Toast.LENGTH_LONG).show();
         }
      }
      return false;
   }

   private void requestData(String uri) {
      MyTask task = new MyTask();
      //task.execute("Param 1", "Param 2", "Param 3"); //proceso serial
      //task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"Param 1", "Param 2", "Param 3"); //proceso en paralelo
      task.execute(uri); //proceso serial
   }

   protected void updateDisplay() {
      if (flowerList != null){
         for (Flower flower : flowerList){
            output.append(flower.getName()  + "\n");
         }
      }
   }

   /**
    * Verifico si está online
    * @return
    */
   protected boolean isOnline(){
      ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
      NetworkInfo netInfo = cm.getActiveNetworkInfo();
      if( netInfo != null && netInfo.isConnectedOrConnecting()){
         return true;
      }else{
         return false;
      }
   }
   private class MyTask extends AsyncTask<String, String, String>{

      @Override
      protected void onPreExecute() {
         //updateDisplay("Starting task");

         if (tasks.size() == 0){
            pb.setVisibility(View.VISIBLE);
         }
         tasks.add(this);
      }

      @Override
      protected String doInBackground(String... params) {
         /*for (int i = 0; i < params.length; i++){
            publishProgress("Working with " + params[i]);

            try {
               Thread.sleep(500);
            } catch (InterruptedException e) {
               e.printStackTrace();
            }
         }*/

         String content = HttpManager.getData(params[0], "feeduser", "feedpassword");
         return content;
      }

      @Override
      protected void onPostExecute(String s) {
         tasks.remove(this); //para que permanezca el progress.
         if (tasks.size() == 0){
            pb.setVisibility(View.INVISIBLE);
         }

         if(s == null){
            Toast.makeText(MainActivity.this, "Can´t connect to webservice", Toast.LENGTH_SHORT).show();
            return;
         }
         //flowerList = FlowerXMLParser.parseFeed(s);
         flowerList = FlowerJSONParser.parseFeed(s);

         updateDisplay();


      }

      @Override
      protected void onProgressUpdate(String... values) {
         //updateDisplay(values[0]);
      }
   }
}