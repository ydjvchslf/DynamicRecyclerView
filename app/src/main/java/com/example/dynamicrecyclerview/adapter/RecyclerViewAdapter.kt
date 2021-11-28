package com.example.dynamicrecyclerview.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.dynamicrecyclerview.R
import com.example.dynamicrecyclerview.database.MemoDatabase
import com.example.dynamicrecyclerview.entity.Memo
import kotlinx.android.synthetic.main.recyclerview_item.view.*

class RecyclerAdapter(val db: MemoDatabase, var items: List<Memo>?)
    : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>(){

    val TAG: String = "LOG"

    lateinit var mContext: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview_item, parent,false)
            mContext = parent.context
            return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items!!.get(position),position)
    }

    override fun getItemCount(): Int {
        return items!!.size
    }
    fun getItem(): List<Memo>?{
        return items
    }


    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var index: Int? = null
        fun bind(memo: Memo, position: Int) {

            Log.d(TAG, "[RecyclerViewAdapter] : bind() 호출 ")

            index = position

            itemView.title.text = memo.title
            Log.d(TAG, "[RecyclerViewAdapter] : Recycler title 세팅 => ${memo.title} ")

            itemView.content.setText(memo.content)
            Log.d(TAG, "[RecyclerViewAdapter] : 입력받은 content 를 memo.content 로 세팅 => ${memo.content} ")

            //itemView.content.text = memo.content // memo에 title만 세팅 되어있는 상태라 error

            itemView.save_btn.setOnClickListener {
                Log.d(TAG, "[RecyclerViewAdapter] : 저장 버튼 클릭")
                editData(itemView.content.text.toString()) // db udpate 하기 // Q. getText, setText 둘다 하나로 통일? ㅇㅇ!!
                Log.d(TAG, "[RecyclerViewAdapter] : db 저장 성공 : memo.title => ${memo.title} memo.content => ${memo.content}" +
                        "memo.id (나올지 모르겠네) => ${memo.id}")
            }
        }

        // save_btn 클릭시, data 수정 , main Thread통해 db접근, dao 통해 data 수정
        fun editData(content: String){
            Thread {

                Log.d(TAG, "[RecyclerViewAdapter] : editData() 호출, db 업뎃! \n " +
                                "받은 content => $content")

                index?.let { items!!.get(it).content = content };
                index?.let { items!!.get(it) }?.let { db.memoDao().update(it) };
            }.start()
            Toast.makeText(mContext,"저장 완료", Toast.LENGTH_LONG).show()
        }

    }
}