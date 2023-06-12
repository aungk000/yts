package me.ako.yts

import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import dagger.hilt.android.AndroidEntryPoint
import me.ako.yts.databinding.ActivityMainBinding
import me.ako.yts.domain.util.Utils
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var utils: Utils

    @Inject
    lateinit var prefs: SharedPreferences
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        utils.setTheme(prefs.getString("theme", "system"))

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}