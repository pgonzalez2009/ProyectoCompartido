package es.cice.practicapedrogonzalez;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ErrorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);

        String errorMsg = getIntent().getExtras().getString("errorMsg");
        TextView tvError = (TextView)findViewById(R.id.tvError);
        tvError.setText(errorMsg);
    }
}
