package jp.techacademy.hisakazu.matsuo.qa_app

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.ListView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_question_detail.*

import java.util.HashMap
import kotlin.math.log

class QuestionDetailActivity : AppCompatActivity() {

    private lateinit var mQuestion: Question
    private lateinit var mAdapter: QuestionDetailListAdapter
    private lateinit var mAnswerRef: DatabaseReference
    private lateinit var mFavoritesRef: DatabaseReference

    private val mEventListener = object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
            val map = dataSnapshot.value as Map<String, String>

            val answerUid = dataSnapshot.key ?: ""

            for (answer in mQuestion.answers) {
                // 同じAnswerUidのものが存在しているときは何もしない
                if (answerUid == answer.answerUid) {
                    return
                }
            }

            val body = map["body"] ?: ""
            val name = map["name"] ?: ""
            val uid = map["uid"] ?: ""

            val answer = Answer(body, name, uid, answerUid)
            mQuestion.answers.add(answer)
            mAdapter.notifyDataSetChanged()
        }

        override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {

        }

        override fun onChildRemoved(dataSnapshot: DataSnapshot) {

        }

        override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {

        }

        override fun onCancelled(databaseError: DatabaseError) {

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question_detail)

        // 渡ってきたQuestionのオブジェクトを保持する
        val extras = intent.extras
        mQuestion = extras.get("question") as Question

        title = mQuestion.title

        // ListViewの準備
        mAdapter = QuestionDetailListAdapter(this, mQuestion)
        listView.adapter = mAdapter
        mAdapter.notifyDataSetChanged()

        fab.setOnClickListener {
            // ログイン済みのユーザーを取得する
            val user = FirebaseAuth.getInstance().currentUser

            if (user == null) {
                // ログインしていなければログイン画面に遷移させる
                val intent = Intent(applicationContext, LoginActivity::class.java)
                startActivity(intent)
            } else {
                // Questionを渡して回答作成画面を起動する
                val intent = Intent(applicationContext, AnswerSendActivity::class.java)
                intent.putExtra("question", mQuestion)
                startActivity(intent)
            }
        }

        val dataBaseReference = FirebaseDatabase.getInstance().reference
        mAnswerRef = dataBaseReference.child(ContentsPATH).child(mQuestion.genre.toString())
            .child(mQuestion.questionUid).child(AnswersPATH)
        mAnswerRef.addChildEventListener(mEventListener)
    }

    //お気に入りボタンの処理
    override fun onResume() {
        super.onResume()
        val user = FirebaseAuth.getInstance().currentUser

        if (user == null) {
            // ログインしていなければボタンを表示しない
            button3.visibility = View.INVISIBLE
        } else {
            button3.visibility = View.VISIBLE
        }


        val dataBaseReference = FirebaseDatabase.getInstance().reference
        mFavoritesRef = dataBaseReference.child(ContentsPATH).child(mQuestion.genre.toString())
            .child(mQuestion.questionUid).child(AnswersPATH)
        mFavoritesRef.addChildEventListener(mEventListener)


        lateinit var mfavoriteListener: OnCompleteListener<AuthResult>
//        mfavoriteListener = OnCompleteListener { task ->
//            if (task.isSuccessful) {
        lateinit var mDataBaseReference: DatabaseReference
        mDataBaseReference = FirebaseDatabase.getInstance().reference
        val FavoriteRef =
            mDataBaseReference.child(FavoritesPATH).child(user!!.uid).child(mQuestion.questionUid)

 //       FavoriteRef.addListenerForSingleValueEvent(object : ValueEventListener {
 //           override fun onDataChange(snapshot: DataSnapshot) {
                // お気に入り削除
 //               val data = snapshot.value as Map<*, *>?

                //FavoriteRef.setValue("")
 //               button3.text = "お気に入り削除"
 //           }

 //           override fun onCancelled(firebaseError: DatabaseError) {}
 //       })


                button3.setOnClickListener() {

            if (button3.text=="お気に入り登録"){
                //お気に入りに登録
                lateinit var mFavoriteRef: DatabaseReference
                mFavoriteRef = FirebaseDatabase.getInstance().reference
                val FavoriteRef = mFavoriteRef.child(FavoritesPATH).child(user!!.uid).child(mQuestion.questionUid)
                val data = HashMap<String, String>()
                data["question"] = mQuestion.questionUid
                FavoriteRef.setValue(data)
                button3.text = "お気に入り削除"

            }else{
                FavoriteRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        // お気に入り削除
                        FavoriteRef.setValue("")
                        button3.text = "お気に入り登録"
                }

                override fun onCancelled(firebaseError: DatabaseError) {}
            })
            }
        }
    }
}













