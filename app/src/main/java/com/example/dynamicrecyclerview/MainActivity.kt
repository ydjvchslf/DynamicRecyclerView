package com.example.dynamicrecyclerview

import android.graphics.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dynamicrecyclerview.adapter.RecyclerAdapter
import com.example.dynamicrecyclerview.database.MemoDatabase
import com.example.dynamicrecyclerview.databinding.ActivityMainBinding
import com.example.dynamicrecyclerview.entity.Memo
import com.example.dynamicrecyclerview.viewModel.MainViewModel
import android.content.Context as contecx

class MainActivity : AppCompatActivity() {

    val TAG: String = "LOG"

    private var adapter: RecyclerAdapter? = null
    private val paint: Paint = Paint()

    // 전역 변수로 바인딩 객체 선언
    private var mBinding: ActivityMainBinding? = null
    //매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mBinding!!

    //view model 가져오기
    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    //    setContentView(R.layout.activity_main)

        // 자동 생성된 뷰 바인딩 클래스에서의 inflate라는 메서드를 활용
        // 액티비티에서 사용할 바인딩 클래스의 인스턴스 생성
        mBinding = ActivityMainBinding.inflate(layoutInflater)

        // getRoot 메서드로 레이아웃 내부의 최상위 위치 뷰의
        // 인스턴스를 활용하여 생성된 뷰를 액티비티에 표시 합니다.
        setContentView(binding.root)

        //view model 가져오기
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        //context 넣어서 view model로 넘겨주기
        viewModel.initialize(context = this)

        initSwipe()

        // binding 바인딩 변수를 활용하여 마음 껏 xml 파일 내의 뷰 id 접근이 가능
        // 뷰 id도 파스칼케이스 + 카멜케이스의 네이밍규칙 적용으로 인해서 plus_btn -> plusBtn 로 자동 변환
        binding.plusBtn.setOnClickListener {
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

                    Log.d(TAG, "[MainActivity] - viewModel 연결")

                    viewModel.insertMemo(editText.text.toString())
                }
            }
            builder.setNegativeButton(
                "취소"
            ) { dialog, which ->
                Log.d(TAG, "취소 버튼 클릭")
            }
            builder.show()

            binding.rvView.setHasFixedSize(true)
            val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this) //list 형으로
            binding.rvView.layoutManager = layoutManager


            //UI 갱신 (라이브데이터 Observer 이용, 해당 디비값이 변화가생기면 실행됨) // owner: lifecycle주관하는 -> 여기선 MainActivity
            //view model 어떻게 적용할까

            var db: MemoDatabase? = null
            viewModel.initialize(context = this)

            db?.memoDao()?.getAll()?.observe(this, Observer {

                Log.d(TAG, "[MainActivity] - adapter 장착 부분인가")

                // update UI
                adapter = RecyclerAdapter(db, it)
                binding.rvView.adapter = adapter
            })

        }
    }

    // 액티비티가 파괴될 때
    override fun onDestroy() {
        super.onDestroy()
        // onDestroy 에서 binding class 인스턴스 참조를 정리해주어야 한다.
        mBinding = null
        super.onDestroy()
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
                    Thread{                                     // View Model 로직 분리
                        adapter?.getItem()?.get(position)?.let { viewModel.deleteMemo(it) }
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
                    }
                }

            }

        }

        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(binding.rvView)

    }

}
