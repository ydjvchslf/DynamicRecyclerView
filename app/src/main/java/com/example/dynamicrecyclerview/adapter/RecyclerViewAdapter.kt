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
import com.example.dynamicrecyclerview.databinding.RecyclerviewItemBinding
import com.example.dynamicrecyclerview.entity.Memo
import kotlinx.coroutines.NonDisposableHandle.parent

class RecyclerAdapter(val db: MemoDatabase, var items: List<Memo>?)
    : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>(){

    val TAG: String = "LOG"

    lateinit var mContext: Context

    //view binding 적용하여 수정
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.d(TAG, "RecyclerAdapter - onCreateViewHolder() 호출")
//            val v: View = LayoutInflater.from(parent.context)
//            .inflate(R.layout.recyclerview_item, parent,false)
            mContext = parent.context
//            return ViewHolder(v)

        val binding = RecyclerviewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items!![position],position)
    }

    override fun getItemCount(): Int {
        return items!!.size
    }
    fun getItem(): List<Memo>?{
        return items
    }

    //view binding 적용하여 수정
    inner class ViewHolder(var binding: RecyclerviewItemBinding): RecyclerView.ViewHolder(binding.root){

        var index: Int? = null
        fun bind(memo: Memo, position: Int) {

            Log.d(TAG, "[RecyclerViewAdapter] : bind() 호출 ")

            index = position

            //itemView.title.setText(memo.title)
            //itemView 도 필요 없음
            binding.title.text = memo.title

            Log.d(TAG, "[RecyclerViewAdapter] : Recycler title 세팅 => ${memo.title} ")

            binding.content.setText(memo.content)
            Log.d(TAG, "[RecyclerViewAdapter] : 입력받은 content 를 memo.content 로 세팅 => ${memo.content} ")

            //itemView.content.text = memo.content // memo.content가 null이어서 error라 생각했는데, nono, EditText는 getText, setText 다름

            binding.saveBtn.setOnClickListener {
                Log.d(TAG, "[RecyclerViewAdapter] : 저장 버튼 클릭")
                editData(binding.content.text.toString()) // db udpate 하기 // Q. getText, setText 둘다 하나로 통일? ㅇㅇ!!
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