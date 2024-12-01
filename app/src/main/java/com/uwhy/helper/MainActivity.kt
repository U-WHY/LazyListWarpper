package com.uwhy.helper

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2

class MainActivity : AppCompatActivity() {
    private val viewPager by lazy { findViewById<ViewPager>(R.id.view_pager) }
    private val viewPager2 by lazy { findViewById<ViewPager2>(R.id.view_pager2) }
    private val viewPager2Wrapper by lazy { findViewById<ViewPager2>(R.id.view_pager2_wrapper) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        init()
    }

    private fun init() {
        viewPager.adapter = object : FragmentStatePagerAdapter(supportFragmentManager) {
            val fragments = Array<Fragment>(5) {
                ItemFragment.newInstance(it, true, false)
            }

            override fun getCount(): Int = fragments.size

            override fun getItem(position: Int): Fragment = fragments[position]
        }

        viewPager2.adapter = object : FragmentStateAdapter(this) {
            val fragments = Array<Fragment>(5) {
                ItemFragment.newInstance(it, true, false)
            }

            override fun getItemCount(): Int = fragments.size

            override fun createFragment(position: Int): Fragment = fragments[position]
        }

        viewPager2Wrapper.adapter = object : FragmentStateAdapter(this) {
            val fragments = Array<Fragment>(5) {
                ItemFragment.newInstance(it, true, true)
            }

            override fun getItemCount(): Int = fragments.size

            override fun createFragment(position: Int): Fragment = fragments[position]
        }
    }
}