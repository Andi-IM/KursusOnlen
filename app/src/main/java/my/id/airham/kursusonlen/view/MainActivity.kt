package my.id.airham.kursusonlen.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import my.id.airham.kursusonlen.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContentView(R.layout.activity_main)

        val btnRegister = findViewById<Button>(R.id.to_register)
        btnRegister.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    RegisterUpdateActivity::class.java
                )
            )
        }

        val btnListParticipant = findViewById<Button>(R.id.to_list)
        btnListParticipant.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    ListParticipantActivity::class.java
                )
            )
        }
    }
}