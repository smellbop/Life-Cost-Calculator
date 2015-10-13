package com.tigerprawnindustries.money.lifecostcalculator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.app.Fragment;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.DecimalFormat;

import static android.app.PendingIntent.getActivity;

public class Input extends AppCompatActivity {
    private EditText enterNumber ;
    private TextView result ;

    private String getLifeCost (Double cost){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        double grossSalary = Double.valueOf(sharedPref.getString("gross_salary",  "0.0"));
        double weeklyHours = Double.valueOf(sharedPref.getString("weekly_hours",  "0.0"));

        double lifeCost = cost/getHourlyRate(grossSalary,weeklyHours);
        return hoursMinutes(lifeCost);
    }

    /*
    Returns a formatted sting in hours and minutes when given the hours as a double
     */
    private String hoursMinutes(double lifeCost){
        String result ="";
        int hours = (int)Math.floor(lifeCost);
        int minutes = (int) Math.round((lifeCost - hours)*60);

        if (hours>0){
            result += hours + " hours and ";
        }
        result += minutes + " minutes";

        return result;
    }

    private double calculateNetSalary(double grossSalary,boolean studentLoan){
        int taxFree = 10600;
        int tax20 = 31785;
        int tax40 = 118215;
        double netSalary = 0;

        //TODO use correct values
        //TODO tidy up calculation
        //TODO add NI
        //TODO add student loan
        //TODO add pension
        //TODO add options for yanks

        if(grossSalary <= taxFree){
            netSalary += grossSalary;
        } else {
            grossSalary -= taxFree;
            netSalary += taxFree;
            if(grossSalary <= tax20){
                netSalary += 0.8*grossSalary;
            } else {
                grossSalary -= tax20;
                netSalary += 0.8*tax20;
                if(grossSalary <= tax40){
                    netSalary += 0.6*grossSalary;
                } else {
                    grossSalary -= tax40;
                    netSalary += 0.6*tax40;
                    netSalary += 0.55*grossSalary;
                }

            }
        }

        return netSalary;
    }

    private double getHourlyRate(double grossSalary,double weeklyHours){
        double netSalary=calculateNetSalary(grossSalary,false);
        return netSalary/(weeklyHours*52.17);
    }

    private final TextWatcher costWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            if(s.length()>0) {
                Double cost = Double.valueOf('0'+s.toString());
                result.setText("Life cost: "+ getLifeCost(cost));
            } else {
                result.setText("");
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
        setContentView(R.layout.activity_input);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //set class variables to point to fields
        enterNumber = (EditText)findViewById(R.id.enterNumber);
        result = (TextView)findViewById(R.id.result);
        //add listener so that we can update the life cost immediately
        enterNumber.addTextChangedListener(costWatcher);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_input, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
           return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
