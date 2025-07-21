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
    private var _binding: FragmentOneBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOneBinding.inflate(inflater, container, false)
        val view = binding.root // 바인딩의 루트 뷰를 가져옵니다.

        val datas = mutableListOf<String>()
        for(i in 1..20){
            datas.add("Item $i")
        }

        val layoutManager = LinearLayoutManager(activity)
        val adapter= MyAdapter(datas)

        //return binding.root
        return view // 생성된 뷰를 반환합니다.
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}