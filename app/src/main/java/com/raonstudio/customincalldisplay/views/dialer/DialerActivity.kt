package com.raonstudio.customincalldisplay.views.dialer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.raonstudio.customincalldisplay.R
import com.raonstudio.customincalldisplay.databinding.ActivityDialerBinding

class DialerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDialerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dialer)
    }
}