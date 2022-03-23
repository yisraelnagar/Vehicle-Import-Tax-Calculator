package com.example.cartaxulator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DecimalFormat;


public class MainActivity extends AppCompatActivity {

    RequestQueue mQue;

    double[] currencies;
    String[] currencyNames;
    int USD = 0, EUR = 1, CAD = 2, ILS = 3;
    int currentCarType = 0;
    double[] lowerRateTax;
    double[] higherRateTax;

    int[] maxPriceOff;

    int currentPriceCurrency;
    int currentShippingCurrency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mQue = Volley.newRequestQueue(this);
        currencies = new double[4];
        currencyNames = new String[4];
        currencyNames[0] = "USD";
        currencyNames[1] = "EUR";
        currencyNames[2] = "CAD";
        currencyNames[3] = "ILS";
        currentPriceCurrency = 0;
        currentShippingCurrency = 0;
        currentCarType = 0;
        getExchangeRates();
        lowerRateTax = new double[4];
        higherRateTax = new double[4];
        maxPriceOff = new int[4];
        lowerRateTax[0] = 2.14;
        lowerRateTax[1] = 1.7;
        lowerRateTax[2] = 1.47;
        lowerRateTax[3] = 1.29;
        higherRateTax[0] = 2.29;
        higherRateTax[1] = 1.82;
        higherRateTax[2] = 1.57;
        higherRateTax[3] = 1.38;
        maxPriceOff[0] = 0;
        maxPriceOff[1] = 20000;
        maxPriceOff[2] = 60000;
        maxPriceOff[3] = 75000;

        final EditText price = findViewById(R.id.Price);
        final EditText shipping = findViewById(R.id.shippingPrice);
        final Button shippingCurrency = findViewById(R.id.shippingCurrency);
        final Button priceCurrency = findViewById(R.id.priceCurrency);

        Button taxit = findViewById(R.id.TAXIT);

        shippingCurrency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentShippingCurrency++;
                if (currentShippingCurrency > 3) {
                    currentShippingCurrency = 0;
                }
                shippingCurrency.setText(currencyNames[currentShippingCurrency]);
            }
        });
        priceCurrency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPriceCurrency++;
                if (currentPriceCurrency > 3) {
                    currentPriceCurrency = 0;
                }
                priceCurrency.setText(currencyNames[currentPriceCurrency]);
            }
        });

        taxit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                double sPrice = ((Double.parseDouble(shipping.getText().toString()) / currencies[currentShippingCurrency])) * currencies[ILS];
                double cPrice = ((Double.parseDouble(price.getText().toString()) / currencies[currentPriceCurrency])) * currencies[ILS];

                System.out.println(sPrice);
                System.out.println(cPrice);

                int CalculatedPriceOrigin = (int) ((sPrice + cPrice) * lowerRateTax[currentCarType]);
                int CalculatedPriceNotOrigin = (int) ((sPrice + cPrice) * higherRateTax[currentCarType]);

                if(CalculatedPriceOrigin > 300000){

                    int sum1 = CalculatedPriceOrigin - 300000;
                    double twentySum = ((double)CalculatedPriceOrigin)*0.2;

                    CalculatedPriceOrigin += (int)((sum1*twentySum)/CalculatedPriceOrigin);
                }
                if(CalculatedPriceNotOrigin > 300000){

                    int sum1 = CalculatedPriceNotOrigin - 300000;
                    double twentySum = CalculatedPriceNotOrigin*0.2;

                    CalculatedPriceNotOrigin = (int)((sum1*twentySum)/CalculatedPriceNotOrigin) + CalculatedPriceNotOrigin;
                }

                TextView result = findViewById(R.id.resultPrice);
                DecimalFormat formatter = new DecimalFormat("#,###");

                System.out.println();
                String newLine = System.getProperty("line.separator");
                if (currentCarType == 0) {
                    System.out.println(currencies[USD]);
                    result.setText("Lower Rate Tax - " + formatter.format(CalculatedPriceOrigin) + " ILS" +
                            newLine + "Higher Rate Tax - " + formatter.format(CalculatedPriceNotOrigin) + " ILS");
                } else {
                    int resultFullPriceOrigin = (int) ((sPrice + cPrice) * lowerRateTax[0]) - maxPriceOff[currentCarType];
                    int resultFullPriceNotOrigin = (int) ((sPrice + cPrice) * higherRateTax[0]) - maxPriceOff[currentCarType];

                    if(resultFullPriceOrigin > 300000){

                        int sum1 = resultFullPriceOrigin - 300000;
                        double twentySum = resultFullPriceOrigin*0.2;

                        resultFullPriceOrigin = (int)((sum1*twentySum)/resultFullPriceOrigin) + resultFullPriceOrigin;
                    }
                    if(resultFullPriceNotOrigin > 300000){

                        int sum1 = resultFullPriceNotOrigin - 300000;
                        double twentySum = resultFullPriceNotOrigin*0.2;

                        resultFullPriceNotOrigin = (int)((sum1*twentySum)/resultFullPriceNotOrigin) + resultFullPriceNotOrigin;
                    }

                    if (resultFullPriceOrigin < CalculatedPriceOrigin) {
                        resultFullPriceOrigin = CalculatedPriceOrigin;
                    }
                    if (resultFullPriceNotOrigin < CalculatedPriceNotOrigin) {
                        resultFullPriceNotOrigin = CalculatedPriceNotOrigin;
                    }
                    result.setText("Lower Rate Tax - " + formatter.format(resultFullPriceOrigin) + " ILS" +
                            newLine + "Higher Rate Tax - " + formatter.format(resultFullPriceNotOrigin) + " ILS");
                }
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        });

        setTicks();

        Button usdToIls = findViewById(R.id.usdToIls);
        usdToIls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView dollarWorth = findViewById(R.id.dollarWorth);
                double worth = (currencies[ILS]/currencies[USD]);
                System.out.println(worth);
                dollarWorth.setText("USD in ILS: " + Double.toString(worth));
            }
        });


    }


    public void getExchangeRates() {
        String api = "https://api.ratesapi.io/api/latest";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, api, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject object = response.getJSONObject("rates");
                            currencies[USD] = Double.parseDouble(object.getString("USD"));
                            currencies[CAD] = Double.parseDouble(object.getString("CAD"));
                            currencies[ILS] = Double.parseDouble(object.getString("ILS"));
                            currencies[EUR] = 1;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Erorrr");
            }
        });
        mQue.add(request);
    }


    public void setTicks() {
        System.out.println("Click");
        final Button gas = findViewById(R.id.gas);
        final Button hybrid = findViewById(R.id.hybrid);
        final Button plugin = findViewById(R.id.plugin);
        final Button electric = findViewById(R.id.electric);
        gas.setBackgroundColor(Color.WHITE);
        hybrid.setBackgroundColor(Color.LTGRAY);
        plugin.setBackgroundColor(Color.LTGRAY);
        electric.setBackgroundColor(Color.LTGRAY);
        gas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentCarType != 0) {
                    gas.setBackgroundColor(Color.WHITE);
                    hybrid.setBackgroundColor(Color.LTGRAY);
                    plugin.setBackgroundColor(Color.LTGRAY);
                    electric.setBackgroundColor(Color.LTGRAY);
                    currentCarType = 0;
                }
            }
        });
        hybrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentCarType != 1) {
                    hybrid.setBackgroundColor(Color.WHITE);
                    gas.setBackgroundColor(Color.LTGRAY);
                    plugin.setBackgroundColor(Color.LTGRAY);
                    electric.setBackgroundColor(Color.LTGRAY);
                    currentCarType = 1;
                }
            }
        });
        plugin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentCarType != 2) {
                    plugin.setBackgroundColor(Color.WHITE);
                    hybrid.setBackgroundColor(Color.LTGRAY);
                    gas.setBackgroundColor(Color.LTGRAY);
                    electric.setBackgroundColor(Color.LTGRAY);
                    currentCarType = 2;
                }
            }
        });
        electric.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentCarType != 3) {
                    electric.setBackgroundColor(Color.WHITE);
                    hybrid.setBackgroundColor(Color.LTGRAY);
                    plugin.setBackgroundColor(Color.LTGRAY);
                    gas.setBackgroundColor(Color.LTGRAY);
                    currentCarType = 3;
                }
            }
        });

    }

}
