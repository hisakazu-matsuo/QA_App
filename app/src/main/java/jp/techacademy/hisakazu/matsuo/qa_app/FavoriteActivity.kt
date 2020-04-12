package jp.techacademy.hisakazu.matsuo.qa_app

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.FirebaseDatabase

class FavoriteActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabaseReference: DatabaseReference
    private lateinit var mListView: ListView
    private lateinit var mQuestionArrayList: ArrayList<Question>
    private lateinit var mAdapter: QuestionsListAdapter

    val user = mAuth.currentUser
    val dataBaseReference = FirebaseDatabase.getInstance().reference

    val favRef = dataBaseReference.child(FavoritesPATH).child(user!!.uid)

    private val mFavoriteListener = object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
            val map = dataSnapshot.value as Map<String, String>
            val genre = map["genre"] ?: ""
            val questionUid = map["question"] ?: ""

            //Firebaseでデータを取ってくる処理をいれる
            val questionRef =
                dataBaseReference.child(ContentsPATH).child(genre.toString()).child(questionUid)
            questionRef.addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(dataSnaphot: DataSnapshot) {
                        //質問の中身を取ってくる
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

        favRef.addChildEventListener(mFavoriteListener)

        // Firebase
        mDatabaseReference = FirebaseDatabase.getInstance().reference

        // ListViewの準備
        mListView = findViewById(R.id.listView)
        mAdapter = QuestionsListAdapter(this)
        mQuestionArrayList = ArrayList<Question>()
        mAdapter.notifyDataSetChanged()
        // --- ここまで追加する ---

        mListView.setOnItemClickListener { parent, view, position, id ->
            // Questionのインスタンスを渡して質問一覧画面を起動する
            val intent = Intent(applicationContext, QuestionDetailActivity::class.java)
            intent.putExtra("question", mQuestionArrayList[position])
            startActivity(intent)
        }
    }

}

