package jp.techacademy.hisakazu.matsuo.qa_app

import java.io.Serializable
import java.util.ArrayList

class Favorite(val title: String, val body: String, val name: String, val uid: String, val questionUid: String, val genre: Int, bytes: ByteArray, val answers: ArrayList<Answer>) : Serializable {
    val imageBytes: ByteArray

    init {
        imageBytes = bytes.clone()
    }
}