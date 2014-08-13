package com.example.matt.flowercatalog;

import android.net.http.AndroidHttpClient;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

/**
 * Created by matt on 12/08/2014.
 */
public class HttpManager {
   public static String getData(String uri){
      AndroidHttpClient client = AndroidHttpClient.newInstance("AndroidAgent");

      //defino el target del request
      HttpGet request = new HttpGet(uri);
      //instancio objeto response
      HttpResponse response;

      try {
         response = client.execute(request);
         return EntityUtils.toString(response.getEntity());
      }catch (Exception e){
         e.printStackTrace();
         return null;
      }finally {
         client.close();
      }
   }
}
