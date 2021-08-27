package com.dreamdev.testtask.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dreamdev.testtask.R
import com.dreamdev.testtask.base.BaseActivity

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}