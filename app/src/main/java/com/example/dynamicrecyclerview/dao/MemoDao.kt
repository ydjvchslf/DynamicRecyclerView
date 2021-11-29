package com.example.dynamicrecyclerview.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.dynamicrecyclerview.entity.Memo

@Dao
interface MemoDao{
    @Query("SELECT * FROM memo ORDER BY id DESC") //내림차순
    fun getAll(): LiveData<List<Memo>> //Observer로 변화를 감지할 수 있습니다. 전체 데이터에 변화가 생길때 LiveData Callback을 실행하여 UI를 업데이트

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(memo: Memo)

    @Update
    fun update(memo: Memo)

    @Delete
    fun delete(memo: Memo)

    @Query("DELETE FROM memo")
    fun deleteAll()
}