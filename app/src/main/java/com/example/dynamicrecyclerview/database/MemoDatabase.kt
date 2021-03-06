package com.example.dynamicrecyclerview.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.dynamicrecyclerview.MainActivity
import com.example.dynamicrecyclerview.dao.MemoDao
import com.example.dynamicrecyclerview.entity.Memo
import com.example.dynamicrecyclerview.viewModel.MainViewModel

@Database(entities = arrayOf(Memo::class), version = 1, exportSchema = false)
abstract class MemoDatabase: RoomDatabase(){
    abstract fun memoDao(): MemoDao

    companion object{
        private var INSTANCE: MemoDatabase? = null
        fun getInstance(context: MainActivity): MemoDatabase{
            if (INSTANCE == null){
                INSTANCE = Room.databaseBuilder(
                    context,
                    MemoDatabase::class.java,
                    "memo_database")
                    .build()
            }
            return INSTANCE as MemoDatabase
        }
    }

    private val sRoomDatabaseCallback = object : RoomDatabase.Callback() {
        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            Thread {
                val dao: MemoDao? = INSTANCE?.memoDao()
                dao?.deleteAll()
                //데이터 채우기
            }.start()
        }
    }

}