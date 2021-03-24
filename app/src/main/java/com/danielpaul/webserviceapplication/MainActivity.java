package com.danielpaul.webserviceapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.danielpaul.webserviceapplication.models.CarteClashRoyale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    String token = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiIsImtpZCI6IjI4YTMxOGY3LTAwMDAtYTFlYi03ZmExLTJjNzQzM2M2Y2NhNSJ9.eyJpc3MiOiJzdXBlcmNlbGwiLCJhdWQiOiJzdXBlcmNlbGw6Z2FtZWFwaSIsImp0aSI6IjViZTcxZTk0LWFiZDItNDc5NS04ZTNmLTU2YTc0OGNiNTUzNyIsImlhdCI6MTYxNjQ3NDY5Nywic3ViIjoiZGV2ZWxvcGVyLzY1YjU0YWRhLTI4ZjMtYzIxZS01ZjI2LTY2Y2FhMWM0ZWQxNCIsInNjb3BlcyI6WyJyb3lhbGUiXSwibGltaXRzIjpbeyJ0aWVyIjoiZGV2ZWxvcGVyL3NpbHZlciIsInR5cGUiOiJ0aHJvdHRsaW5nIn0seyJjaWRycyI6WyI4Ni4yMDIuMTI2LjExMSJdLCJ0eXBlIjoiY2xpZW50In1dfQ.DalNkVt2zzuvmACw5l0EAh-wa5Rp9EsgU5hg-1jnghRMMkbXo_k1gFrfITyy6GIRbVYoqyj7QjYts49IZoixmg";
    private ListView liste_cartes;

    ArrayList<HashMap<String, String>> cartes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Cartes clash royale");

        liste_cartes = findViewById(R.id.liste_cartes);
        new RecupererCartes().execute();
    }

    private class RecupererCartes extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... arg0) {
            // Création du client HTTP
            OkHttpClient client = new OkHttpClient();

            // Création de notre requête contenant header + url
            Request request = new Request
                    .Builder()
                    .addHeader("Authorization", token)
                    .url("https://api.clashroyale.com/v1/cards")
                    .build();

            // Appel à l'API
            try (Response reponse = client.newCall(request).execute()) {
                String reponseAPI = reponse.body().string();
                JSONObject jsonObject = new JSONObject(reponseAPI);
                JSONArray jsonArray = jsonObject.getJSONArray("items");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject c = jsonArray.getJSONObject(i);

                    HashMap<String, String> carte = new HashMap<>();
                    carte.put("name", c.getString("name"));
                    carte.put("medium", c.getJSONObject("iconUrls").getString("medium"));

                    cartes.add(carte);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            ListAdapter adapter = new ClashRoayleAdapter(MainActivity.this, cartes, R.layout.carte_item, new String[]{"medium", "name"}, new int[]{R.id.medium, R.id.name});
            liste_cartes.setAdapter(adapter);
        }
    }

    public class ClashRoayleAdapter extends SimpleAdapter {
        private Context mContext;
        public LayoutInflater inflater = null;

        public ClashRoayleAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);
            mContext = context;
            inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View vi = convertView;
            if (convertView == null)
                vi = inflater.inflate(R.layout.carte_item, null);

            HashMap<String, Object> data = (HashMap<String, Object>) getItem(position);
            new TelechargerImage(vi.findViewById(R.id.medium)).execute((String) data.get("medium"));
            TextView tv = vi.findViewById(R.id.name);
            tv.setText(data.get("name").toString());
            return vi;
        }
    }

    public class TelechargerImage extends AsyncTask<String, Void, Boolean> {
        ImageView v;
        String url;
        Bitmap bm;

        public TelechargerImage(ImageView v) {
            this.v = v;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            url = params[0];
            bm = loadBitmap(url);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            v.setImageBitmap(bm);
        }

        public Bitmap loadBitmap(String url) {
            try {
                URL newurl = new URL(url);
                Bitmap b = BitmapFactory.decodeStream(newurl.openConnection().getInputStream());
                return b;
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }
    }
}
