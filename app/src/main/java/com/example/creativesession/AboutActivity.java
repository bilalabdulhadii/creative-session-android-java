package com.example.creativesession;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AboutActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton goBackImgBtn;
    private TextView shareTextView, reportTextView, linkedinTextView, githubTextView, emailTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        goBackImgBtn = findViewById(R.id.goBack_ImgBtn);
        shareTextView = findViewById(R.id.share_TextView);
        reportTextView = findViewById(R.id.report_TextView);
        linkedinTextView = findViewById(R.id.linkedin_TextView);
        githubTextView = findViewById(R.id.github_TextView);
        emailTextView = findViewById(R.id.email_TextView);

        goBackImgBtn.setOnClickListener(this);
        shareTextView.setOnClickListener(this);
        reportTextView.setOnClickListener(this);
        linkedinTextView.setOnClickListener(this);
        githubTextView.setOnClickListener(this);
        emailTextView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.goBack_ImgBtn) {
            finish();
        } else if (id == R.id.share_TextView) {

        } else if (id == R.id.report_TextView) {
            String email = "bilalabdulhadipro@gmail.com";
            String subject = "Report Bug - Creative Session";
            Intent mail = new Intent(Intent.ACTION_SENDTO);
            mail.setData(Uri.parse("mailto:" + email));
            mail.putExtra(Intent.EXTRA_SUBJECT,subject);
            try {
                startActivity(mail);
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(AboutActivity.this, "No email app found", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.linkedin_TextView) {
            String githubLink = "https://www.linkedin.com/in/bilalabdulhadii";
            Intent link = new Intent(Intent.ACTION_VIEW);
            link.setData(Uri.parse(githubLink));
            startActivity(link);
        } else if (id == R.id.github_TextView) {
            String githubLink = "https://github.com/bilalabdulhadii";
            Intent link = new Intent(Intent.ACTION_VIEW);
            link.setData(Uri.parse(githubLink));
            startActivity(link);
        } else if (id == R.id.email_TextView) {
            String email = "bilalabdulhadipro@gmail.com";
            String subject = "Creative Session";
            Intent mail = new Intent(Intent.ACTION_SENDTO);
            mail.setData(Uri.parse("mailto:" + email));
            mail.putExtra(Intent.EXTRA_SUBJECT,subject);
            try {
                startActivity(mail);
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(AboutActivity.this, "No email app found", Toast.LENGTH_SHORT).show();
            }
        }

    }
}