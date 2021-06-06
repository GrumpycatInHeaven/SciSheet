package com.example.scincepaper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.AsyncTaskLoader;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.annotation.Documented;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

// --------------------------------
// *just text*
//    public Elements content;
//    public ArrayList<String> titleList = new ArrayList<String>();
//    private ArrayAdapter<String> adapter;
//    private ListView lv;
// --------------------------------


    // -----------------------------------------------
// *hrefs + text
    private ListView listView;
    private TextView textView;


    // ------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.ListView1);
        textView = (TextView) findViewById(R.id.TextView);

        ParseTitle parseTitle = new ParseTitle();
        parseTitle.execute();

        try {


            HashMap<String, String> hashMap = parseTitle.get();
            ArrayList<String> arrayList = new ArrayList<>();

            for (Map.Entry entry : hashMap.entrySet()) {
                arrayList.add(entry.getKey().toString());
            }

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this
                    , android.R.layout.simple_list_item_1, arrayList);

            listView.setAdapter(arrayAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    ParseText parseText = new ParseText();
                    parseText.execute(hashMap.get(arrayList.get(position)));

                    try {
                        listView.setVisibility(View.GONE);
                        textView.setText(parseText.get());
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });


        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        listView.setVisibility(View.VISIBLE);
        textView.setVisibility(View.GONE);
    }

    class ParseText extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String str = " ";

            try {
                Document doc = Jsoup.connect(params[0]).get();
                Element element = doc.select(".article__body").last();
                str = element.text();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return str;
        }
    }
// -----------------------------------------------
// *hrefs + text

    class ParseTitle extends AsyncTask<Void, Void, HashMap<String, String>> {

        @Override
        protected HashMap<String, String> doInBackground(Void... voids) {
            HashMap<String, String> hashMap = new HashMap<>();

            try {
                Document doc = Jsoup.connect("https://www.sciencemag.org/").get();
                Elements elements = doc.select(".media__headline");
                for (Element element : elements) {
                    Element element1 = element.select("a[href]").last();
                    hashMap.put(element.text(), element1.attr("abs:href"));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return hashMap;
        }
    }


// ------------------------------------------------


// --------------------------------
// *just text*
//        lv = (ListView) findViewById(R.id.ListView1);
//        new NewThread().execute();
//        adapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.pro_item, titleList);
// ----------------------------------


// --------------------------------
// *just text*
//    public class NewThread extends AsyncTask<String, Void, String> {
//        @Override
//        protected String doInBackground(String... arg) {
//            Document doc;
//            try{
//                doc = Jsoup.connect("https://cyberleninka.ru/article/c/mathematics").get();
//                content = doc.select(".title");
//                titleList.clear();
//                for (Element contents: content){
//                    titleList.add(contents.text());
//                }
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(String result){
//            lv.setAdapter(adapter);
//        }
// ---------------------------------------------------------------------------
}
