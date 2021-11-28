package com.example.dynamicrecyclerview

import android.graphics.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dynamicrecyclerview.adapter.RecyclerAdapter
import com.example.dynamicrecyclerview.database.MemoDatabase
import com.example.dynamicrecyclerview.entity.Memo
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val TAG: String = "LOG"

    private var adapter: RecyclerAdapter? = null
    private val paint: Paint = Paint()
    private var db: MemoDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initSwipe()

        plus_btn.setOnClickListener {
            Log.d(TAG, "추가 버튼 클릭")
            // title 입력 다이얼로그를 호출한다.
            // title 입력하여 리사이클러뷰 addItem
            val editText = EditText(this)
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setTitle("Item 추가") // 다이얼로그 제목
            builder.setMessage("제목을 입력해 주세요.") //다이얼로그 메시지
            builder.setView(editText) //내가 입력할 다이얼로그 view 내용
            builder.setPositiveButton(
                "입력"
            ) { dialog, which ->
                Log.d(TAG, "입력 버튼 클릭")
                //제목 입력, DB추가
                if (!editText.text.toString().isEmpty()) {
                    Thread(Runnable {
                        db!!.memoDao().insert(Memo(null, editText.text.toString(), null))
                        Log.d(TAG, "memoDao.insert() 실행, memo.title = ${editText.text.toString()}")
                    }).start()
                }
            }
            builder.setNegativeButton(
                "취소"
            ) { dialog, which ->
                Log.d(TAG, "취소 버튼 클릭")
            }
            builder.show()
        }

        db = MemoDatabase.getInstance(this)
        rv_view.setHasFixedSize(true)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this) //list 형으로
        rv_view.layoutManager = layoutManager

        //UI 갱신 (라이브데이터 Observer 이용, 해당 디비값이 변화가생기면 실행됨)
        db!!.memoDao().getAll().observe(this, Observer{
            // update UI
            adapter = RecyclerAdapter(db!!,it)
            rv_view.adapter = adapter
        })
    }

    private fun initSwipe() {

        Log.d(TAG, "initSwipe () 호출")

        val simpleItemTouchCallback: ItemTouchHelper.SimpleCallback = object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT /* | ItemTouchHelper.RIGHT */) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                Log.d(TAG, "initSwipe () - onSwiped() 호출")

                val position = viewHolder.adapterPosition
                if (direction == ItemTouchHelper.LEFT) {
                    Thread{
                        adapter?.getItem()?.get(position)?.let { db!!.memoDao().delete(it) }
                    }.start()
                } else {
                    //오른쪽으로 밀었을때.
                }
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )

                Log.d(TAG, "initSwipe () - onChildDraw() 호출")

                var icon: Bitmap
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    val itemView: View = viewHolder.itemView
                    val height =
                        itemView.bottom.toFloat() - itemView.top.toFloat()
                    val width = height / 3
                    if (dX > 0) {
                        //오른쪽으로 밀었을 때
                    } else {
                        paint.color = Color.parseColor("#D32F2F")
                        val background = RectF(
                            itemView.right.toFloat() + dX,
                            itemView.top.toFloat(),
                            itemView.right.toFloat(),
                            itemView.bottom.toFloat()
                        )
                        c.drawRect(background, paint)
                        /*
                         * icon 추가할 수 있음.
                         */
                        //icon = BitmapFactory.decodeResource(getResources(), R.drawable.icon_png); //vector 불가!
                        // RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                        //c.drawBitmap(icon, null, icon_dest, p);
                    }
                }

            }

        }

        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(rv_view)

    }

}
