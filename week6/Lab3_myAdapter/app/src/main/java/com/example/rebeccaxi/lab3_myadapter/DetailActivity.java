package com.example.rebeccaxi.lab3_myadapter;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
/**
 * Created by RebeccaXi on 2016/10/12.
 */

public class DetailActivity extends AppCompatActivity {
    private String Name,PhoneNumber,Address,BackColor;
    private TextView NameTextView,PhoneTextView,AddressTextView;
    private RelativeLayout relativeLayout;
    private Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();//获取数据
        Name = intent.getStringExtra("name");
        PhoneNumber = intent.getStringExtra("number");
        Address = intent.getStringExtra("address");
        BackColor = intent.getStringExtra("color");

        NameTextView = (TextView)findViewById(R.id.contact_name);
        PhoneTextView = (TextView)findViewById(R.id.phoneNumber);
        AddressTextView = (TextView)findViewById(R.id.contact_address);
        relativeLayout = (RelativeLayout) findViewById(R.id.relative);
        back = (Button) findViewById(R.id.back);

        NameTextView.setText(Name);
        PhoneTextView.setText(PhoneNumber);
        AddressTextView.setText(Address);
        relativeLayout.setBackgroundColor(Color.parseColor(BackColor));

        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                finish();
            }
        });

        View toggleStar = findViewById(R.id.toggle_star);
        toggleStar.setTag(false);
        toggleStar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ImageView toggleImage = (ImageView) v;
                boolean toggle = (Boolean) toggleImage.getTag();
                if(toggle){
                    toggleImage.setImageDrawable(getResources().getDrawable(R.mipmap.empty_star));
                }
                else {
                    toggleImage.setImageDrawable(getResources().getDrawable(R.mipmap.full_star));
                }
                toggleImage.setTag(!toggle);
            }
        });

        ListView optionListView = (ListView) findViewById(R.id.more_list);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                DetailActivity.this,R.layout.option_item,R.id.textview_option,getResources().getStringArray(R.array.more));
        optionListView.setAdapter(arrayAdapter);
    }

    /**
     * Created by RebeccaXi on 2016/10/12.
     */
    public static class Contacts {
        private String initial;
        private String name;
        private String address;
        private String number;
        private String color;

        public Contacts(String initial, String name, String address, String number, String color) {
            this.initial =  initial;
            this.name = name;
            this.address = address;
            this.number = number;
            this.color = color;
        }
        public String getBackground() {
            return color;
        }

        public void setBackground(String background) {
            this.color = background;
        }

        public String getPhoneNumber() {

            return number;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.number = phoneNumber;
        }

        public String getAddress() {

            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getFirstLetter() {

            return initial;
        }

        public void setFirstLetter(String firstLetter) {
            this.initial = firstLetter;
        }
    }
}

