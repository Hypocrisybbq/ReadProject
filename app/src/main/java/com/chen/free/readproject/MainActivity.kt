package com.chen.free.readproject

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.chen.free.readmodule.ReadActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        into?.setOnClickListener {
            val intent = Intent()
            intent.setClass(this,ReadActivity::class.java)
            startActivity(intent)
        }
    }
}