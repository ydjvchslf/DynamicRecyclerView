package com.example.dynamicrecyclerview.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.example.dynamicrecyclerview.adapter.RecyclerAdapter
import com.example.dynamicrecyclerview.database.MemoDatabase
import com.example.dynamicrecyclerview.databinding.ActivityMainBinding

class MainViewModel(application: Application) : AndroidViewModel(application), LifecycleOwner {

    companion object{
        const val TAG: String = "LOG"
    }

    private var adapter: RecyclerAdapter? = null
    private var db: MemoDatabase? = null

    // 전역 변수로 바인딩 객체 선언
    private var mBinding: ActivityMainBinding? = null
    //매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mBinding!!

    fun updateUi(){

    db = MemoDatabase.getInstance(this)

    //UI 갱신 (라이브데이터 Observer 이용, 해당 디비값이 변화가생기면 실행됨) // owner: lifecycle주관하는 -> 여기선 MainActivity
        db!!.memoDao().getAll().observe(this, Observer{
            // update UI
            adapter = RecyclerAdapter(db!!,it)
            binding.rvView.adapter = adapter
        })
    }

    override fun getLifecycle(): Lifecycle {
        TODO("Not yet implemented")
    }

}