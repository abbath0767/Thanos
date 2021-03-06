package com.ng.thanoslayout

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.ng.thanos.custom.Thanos
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val thanos = Thanos(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button_to_snap.setOnClickListener {
            thanos.snap()
        }
    }
}
