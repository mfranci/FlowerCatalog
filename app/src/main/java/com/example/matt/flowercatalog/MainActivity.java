package com.example.matt.flowercatalog;

import android.app.ListActivity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
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


public class MainActivity extends ListActivity {
   TextView output;
   ProgressBar pb;
   List<MyTask> tasks;

   List<Flower> flowerList;

   public static final String PHOTOS_BASE_URL = "http://services.hanselandpetal.com/photos/";

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      pb = (ProgressBar) findViewById(R.id.progressBar1);
      pb.setVisibility(View.INVISIBLE);

      tasks = new ArrayList<>();
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      getMenuInflater().inflate(R.menu.main, menu);
      return true;
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      if (item.getItemId() == R.id.action_get_data) {
         if (isOnline()) {
            requestData("http://services.hanselandpetal.com/secure/flowers.json");
            //requestData("http://services.hanselandpetal.com/feeds/flowers.json");
            //requestData("http://services.hanselandpetal.com/feeds/flowers.xml");
         } else {
            Toast.makeText(this, "Network isn't available", Toast.LENGTH_LONG).show();
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
      com.example.matt.flowercatalog.FlowerAdapter adapter = new com.example.matt.flowercatalog.FlowerAdapter(this, R.layout.item_flower, flowerList);
      setListAdapter(adapter);
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

   /**
    * Param 1 -> input type
    * Param 2 -> progress information
    * Param 3 -> return data type
    */
   private class MyTask extends AsyncTask<String, String, List<Flower>>{

      @Override
      protected void onPreExecute() {
         //updateDisplay("Starting task");

         if (tasks.size() == 0){
            pb.setVisibility(View.VISIBLE);
         }
         tasks.add(this);
      }

      @Override
      protected List<Flower> doInBackground(String... params) {
         String content = HttpManager.getData(params[0], "feeduser", "feedpassword");
         flowerList = FlowerJSONParser.parseFeed(content);

         return flowerList;
      }

      protected void onPostExecute(List<Flower> result) {

         tasks.remove(this); //para que permanezca el progress.
         if (tasks.size() == 0) {
            pb.setVisibility(View.INVISIBLE);
         }

         if (result == null) {
            Toast.makeText(MainActivity.this, "Web service not available", Toast.LENGTH_LONG).show();
            return;
         }

         flowerList = result;
         updateDisplay();
      }

      @Override
      protected void onProgressUpdate(String... values) {
         //updateDisplay(values[0]);
      }
   }
}