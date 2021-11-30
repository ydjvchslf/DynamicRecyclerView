package com.example.dynamicrecyclerview.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.dynamicrecyclerview.MainActivity
import com.example.dynamicrecyclerview.database.MemoDatabase
import com.example.dynamicrecyclerview.entity.Memo

// 데이터의 변경
// 뷰모델은 데이터의 변경사항을 알려주는 라이브 데이터를 가지고 있
class MainViewModel : ViewModel() {

    companion object{
        const val TAG: String = "LOG"
    }

    private var db: MemoDatabase? = null

    // 뮤터블 라이브 데이터 - 수정 가능한 녀석
    // 라이브 데이터 - 읽기전용, 수정 불가

    //초기값 설정
    init {
        Log.d(TAG, "[MainViewModel] - 생성자 호출")
    }

    //db get instance
    fun initialize(context: Context){
        db = MemoDatabase.getInstance(context as MainActivity)
    }

    fun insertMemo(editText: String){

        Thread(Runnable { // view model 로직 분리 // view쪽 앤데.. 변수 받아서 넘겨줘야 하나?
            db!!.memoDao().insert(Memo(null, editText, null))

            Log.d(TAG, "[MainViewModel] - insertMemo() 호출, memo.title = $editText")
        }).start()

    }

    fun deleteMemo(memo: Memo){
        db?.memoDao()?.delete(memo)
    }

    // ui update -> 어떻게 적용할까
    fun updateUi(){
        db?.memoDao()?.getAll()
    }

}