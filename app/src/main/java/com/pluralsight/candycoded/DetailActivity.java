package com.pluralsight.candycoded;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.pluralsight.candycoded.DB.CandyContract;
import com.pluralsight.candycoded.DB.CandyContract.CandyEntry;
import com.pluralsight.candycoded.DB.CandyDbHelper;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DetailActivity extends AppCompatActivity {

    public static final String SHARE_DESCRIPTION = "Look at this delicious candy from Candy Coded - ";
    public static final String HASHTAG_CANDYCODED = " #candycoded";
    public static final String CHOOSE_APP = "Choose App";
    String mCandyImageUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = DetailActivity.this.getIntent();

        if (intent != null && intent.hasExtra("position")) {
            int position = intent.getIntExtra("position", 0);

            CandyDbHelper dbHelper = new CandyDbHelper(this);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM candy", null);
            cursor.moveToPosition(position);

            String candyName = cursor.getString(cursor.getColumnIndexOrThrow(
                    CandyContract.CandyEntry.COLUMN_NAME_NAME));
            String candyPrice = cursor.getString(cursor.getColumnIndexOrThrow(
                    CandyEntry.COLUMN_NAME_PRICE));
            mCandyImageUrl = cursor.getString(cursor.getColumnIndexOrThrow(
                    CandyEntry.COLUMN_NAME_IMAGE));
            String candyDesc = cursor.getString(cursor.getColumnIndexOrThrow(
                    CandyEntry.COLUMN_NAME_DESC));


            TextView textView = (TextView) this.findViewById(R.id.text_view_name);
            textView.setText(candyName);

            TextView textViewPrice = (TextView) this.findViewById(R.id.text_view_price);
            textViewPrice.setText(candyPrice);

            TextView textViewDesc = (TextView) this.findViewById(R.id.text_view_desc);
            textViewDesc.setText(candyDesc);

            ImageView imageView = (ImageView) this.findViewById(
                    R.id.image_view_candy);
            Picasso.with(this).load(mCandyImageUrl).into(imageView);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail, menu);
        return true;
    }

    // ***
    // TODO - Task 4 - Share the Current Candy with an Intent
    // ***

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemID = item.getItemId();
        //Share the Current Candy with an Intent
        if(itemID == R.id.share_detail){
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT,  SHARE_DESCRIPTION  + mCandyImageUrl + HASHTAG_CANDYCODED);
            shareIntent.setType("text/plain");
            launchActivity(getApplicationContext(), shareIntent);
        }
        return super.onOptionsItemSelected(item);
    }


    //Launches an activity using an implicit intent
    private void launchActivity(Context context, Intent intent){
        PackageManager packageManager = context.getPackageManager();
        //Query for the number of activities that can handle this intent
        List<ResolveInfo> activitiesList = packageManager.queryIntentActivities(intent, PackageManager.MATCH_ALL);
        if (activitiesList.size() > 1) {
            Intent chooser = Intent.createChooser(intent, CHOOSE_APP);
            context.startActivity(chooser);
        }
        else if(intent.resolveActivity(packageManager) != null){
            //Launch default app or activity if there's only single app that can handle intent
            context.startActivity(intent);
        }
    }
}
