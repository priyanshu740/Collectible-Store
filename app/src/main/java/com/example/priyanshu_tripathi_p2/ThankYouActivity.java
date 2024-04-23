package com.example.priyanshu_tripathi_p2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ThankYouActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.thank_you);


        Button letsGoButton = findViewById(R.id.redirectButton);
        letsGoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ThankYouActivity.this, ProductListActivity.class);
                intent.putExtra("clearCart", true);
                startActivity(intent);
            }
        });
    }
}
