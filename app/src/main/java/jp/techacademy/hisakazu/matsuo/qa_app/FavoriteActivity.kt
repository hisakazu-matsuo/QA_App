package jp.techacademy.hisakazu.matsuo.qa_app

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.util.Log
import android.view.MenuItem
import android.widget.ListView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.FirebaseDatabase

class FavoriteActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabaseReference: DatabaseReference
    private lateinit var mListView: ListView
    private lateinit var mQuestionArrayList: ArrayList<Question>
    private lateinit var mAdapter: QuestionsListAdapter

    private val mFavoriteListener = object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
            val map = dataSnapshot.value as Map<String, String>
            val genre = map["genre"] ?: ""
            val questionUid = map["question"] ?: ""
            Log.d("matt3", dataSnapshot.key!!)
            Log.d("matt4", dataSnapshot.toString())

            val mdataBaseReference = FirebaseDatabase.getInstance().reference

            //Firebaseでデータを取ってくる処理をいれる
            val questionRef =
                mdataBaseReference.child(ContentsPATH).child(genre.toString()).child(questionUid)
            questionRef.addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val data = dataSnapshot.value as Map<String, String>
                        //質問の中身を取ってくる
                        Log.d("matt5", dataSnapshot.toString())

                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                })

        }

        override fun onCancelled(p0: DatabaseError) {
        }

        override fun onChildMoved(p0: DataSnapshot, p1: String?) {
        }

        override fun onChildChanged(p0: DataSnapshot, p1: String?) {
        }

        override fun onChildRemoved(p0: DataSnapshot) {
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d("matt", "onCreate")

        // FirebaseAuthのオブジェクトを取得する
        mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser
        val dataBaseReference = FirebaseDatabase.getInstance().reference
        val favRef = dataBaseReference.child(FavoritesPATH).child(user!!.uid)
        favRef.addChildEventListener(mFavoriteListener)

        Log.d("matt", "FirebaseAuthのオブジェクトを取得する")

        // ListViewの準備
        mListView = findViewById(R.id.listView)
        mAdapter = QuestionsListAdapter(this)
        mQuestionArrayList = ArrayList<Question>()

        val question = Question("","","","","","","","")

        mQuestionArrayList.add(question) //配列に追加

        mAdapter.notifyDataSetChanged() //画面表示


        // --- ここまで追加する ---

        Log.d("matt", "ListViewの準備")

        mListView.setOnItemClickListener { parent, view, position, id ->
            // Questionのインスタンスを渡して質問詳細画面を起動する
            val intent = Intent(applicationContext, QuestionDetailActivity::class.java)
            intent.putExtra("question", mQuestionArrayList[position])
            startActivity(intent)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
// 質問のリストをクリアしてから再度Adapterにセットし、AdapterをListViewにセットし直す
        mQuestionArrayList.clear()
        mAdapter.setQuestionArrayList(mQuestionArrayList)
        mListView.adapter = mAdapter

        return true
    }
}
