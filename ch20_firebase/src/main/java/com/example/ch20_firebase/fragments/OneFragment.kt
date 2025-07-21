package com.example.ch20_firebase.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ch20_firebase.databinding.FragmentOneBinding

class OneFragment : Fragment() {
    // _binding 변수를 nullable로 선언하여 메모리 누수를 방지하고,
    // 뷰가 생성된 시점에만 접근할 수 있도록 합니다.
    private var _binding: FragmentOneBinding? = null
    // onCreateView에서 초기화된 _binding에 안전하게 접근하기 위한 getter 속성입니다.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //val binding = FragmentOneBinding.inflate(inflater, container, false)
        _binding = FragmentOneBinding.inflate(inflater, container, false)
        val view = binding.root // 바인딩의 루트 뷰를 가져옵니다.

        val datas = mutableListOf<String>()
        for(i in 1..20){
            datas.add("Item $i")
        }

        val layoutManager = LinearLayoutManager(activity)
        //binding.recyclerview.layoutManager=layoutManager
        val adapter= MyAdapter(datas)
        //binding.recyclerview.adapter=adapter
        //binding.recyclerview.addItemDecoration(MyDecoration(activity as Context))

        //return binding.root
        return view // 생성된 뷰를 반환합니다.
    }
    // 🌟🌟🌟 2. Fragment 뷰가 소멸될 때 바인딩 객체를 null로 설정하여 메모리 누수 방지 🌟🌟🌟
    // Fragment의 뷰 계층이 파괴될 때 바인딩 객체를 해제하여 메모리 누수를 방지합니다.
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}