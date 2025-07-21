package com.example.ch20_firebase.fragments

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.ch20_firebase.R
import com.example.ch20_firebase.databinding.ActivityDashboardBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.example.ch20_firebase.MyApplication
import android.content.Intent // Intent 사용을 위해 추가
import android.widget.Button
import com.example.ch20_firebase.activities.AuthActivity // 로그아웃 후 AuthActivity로 이동하기 위해 추가 (MyApplication과 동일 패키지가 아닐 경우)
import com.example.ch20_firebase.activities.IntroActivity

class DashboardActivity : AppCompatActivity() {

    lateinit var toggle: ActionBarDrawerToggle

    class MyFragmentPagerAdapter(activity: FragmentActivity): FragmentStateAdapter(activity){
        val fragments: List<Fragment>
        init {
            fragments= listOf(OneFragment(), TwoFragment(), ThreeFragment())
        }
        override fun getItemCount(): Int = fragments.size
        override fun createFragment(position: Int): Fragment = fragments[position]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //add......................................
        setSupportActionBar(binding.toolbar)
        toggle = ActionBarDrawerToggle(
            this, binding.drawer, R.string.drawer_opened,
            R.string.drawer_closed
        )
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toggle.syncState()

        val adapter = MyFragmentPagerAdapter(this)
        binding.viewpager.adapter = adapter

        val tabTitles = listOf("보안모드변경", "도어락로그", "비콘거리")
        TabLayoutMediator(binding.tabs, binding.viewpager) { tab, position ->
            // position에 따라 미리 정의된 이름 리스트에서 텍스트를 가져옵니다.
            tab.text = tabTitles[position]
        }.attach()
        /*TabLayoutMediator(binding.tabs, binding.viewpager) { tab, position ->
            tab.text = "Tab${(position + 1)}"
        }.attach()*/

        // NavigationView의 헤더 뷰를 찾고 TextView에 사용자 이메일 설정
        val headerView = binding.mainDrawerView.getHeaderView(0) // 0은 첫 번째 헤더 뷰를 의미
        val userEmailTextView = headerView.findViewById<TextView>(R.id.header_user_email)
        userEmailTextView.text = "${MyApplication.email} 님" // MyApplication.email 값 사용

        // TODO: 여기부터 로그아웃 버튼 로직을 추가합니다
        val logoutHeaderButton = headerView.findViewById<Button>(R.id.header_logout_btn)
        logoutHeaderButton.setOnClickListener {
            // 1. Firebase 로그아웃 처리
            MyApplication.auth.signOut()

            // 2. 앱 내 저장된 사용자 이메일 정보 초기화
            MyApplication.email = null

            // 3. IntroActivity로 이동하고 이전 액티비티 스택 모두 제거
            val intent = Intent(this, IntroActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)

            // 4. 현재 DashboardActivity 종료
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //이벤트가 toggle 버튼에서 제공된거라면..
        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }


}