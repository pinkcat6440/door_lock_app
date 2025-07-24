package com.yourname.ch20_firebase.activities // 실제 패키지명에 맞게 수정해주세요

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback // 이 import를 추가
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import com.yourname.ch20_firebase.R
import com.yourname.ch20_firebase.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        drawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView

        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            binding.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)

        // ===== 변경된 부분: OnBackPressedDispatcher 사용 =====
        val callback = object : OnBackPressedCallback(true) { // true: 이 콜백이 뒤로 가기 이벤트를 활성화함
            override fun handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(navView)) { // navView가 바인딩 되어 있어야 함
                    drawerLayout.closeDrawer(navView)
                } else {
                    isEnabled = false // 콜백 비활성화 후 기본 뒤로 가기 동작 수행
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
        // ===============================================
    }

    // 기존 onBackPressed()는 제거하거나, 위 OnBackPressedCallback을 사용할 경우 더 이상 필요 없음.
    // override fun onBackPressed() {
    //     if (drawerLayout.isDrawerOpen(binding.navView)) {
    //         drawerLayout.closeDrawer(binding.navView)
    //     } else {
    //         super.onBackPressed()
    //     }
    // }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Log.d("MainActivity", "Navigation Item Clicked: ${item.title}")

        when (item.itemId) {
            // TODO: menu_navigation.xml 에 정의된 각 item의 ID에 따라 동작을 추가하세요.
            // R.id.menu_mode_change -> {
            //     Log.d("MainActivity", "모드 변경 메뉴 클릭")
            // }
            // R.id.menu_log_management -> {
            //     Log.d("MainActivity", "로그 관리 메뉴 클릭")
            // }
            // R.id.menu_beacon_location -> {
            //     Log.d("MainActivity", "비콘 위치 메뉴 클릭")
            // }
            // R.id.menu_notification_settings -> {
            //     Log.d("MainActivity", "알림 설정 메뉴 클릭")
            // }
            else -> {
                // 기타 메뉴 아이템 처리
            }
        }

        drawerLayout.closeDrawer(binding.navView)
        return true
    }
}