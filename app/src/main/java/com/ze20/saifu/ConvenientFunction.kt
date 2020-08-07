package com.ze20.saifu

import android.app.AlertDialog
import android.app.Dialog
import android.content.ClipboardManager
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Locale

internal class ConvenientFunction {

    // 色んな所で使う関数をまとめておきます

    fun editToInt(editText: EditText): Int? {
        // editText を 数値に変換 空文字列ならNullを返す
        return editText.text.toString().toIntOrNull()
    }

    fun getBinaryFromBitmap(bitmap: Bitmap): ByteArray {
        // 画像をByteArray型に変換するやつです
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }

    fun getClip(applicationContext: Context): String? {
        // クリップボードを返す
        val mManager: ClipboardManager =
            applicationContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        return mManager.primaryClip?.getItemAt(0)?.text.toString()
    }

    fun hideKeyboard(appCompatActivity: AppCompatActivity, view: View?) {
        // キーボードを探してあれば消します
        view.also {
            val manager =
                appCompatActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            manager.hideSoftInputFromWindow(view!!.windowToken, 0)
        }
    }

    fun quickInsert(context: Context?, price: Int, name: String = "", category: Int = 0): Boolean {
        try {
            val dbHelper = SQLiteDB(context!!, "SaifuDB", null, 1)
            val database = dbHelper.writableDatabase // 書き込み可能

            // log表
            // inputDate primary key,payDate,name,price,category,splitCount,picture

            val inputDate = java.util.Date()
            // INSERTするのに必要なデータをvalueにまとめる
            val values = ContentValues()
            values.run {
                put(
                    "inputDate",
                    SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.JAPANESE).format(
                        inputDate
                    )
                )
                put(
                    "payDate",
                    SimpleDateFormat("yyyy-MM-dd 00:00:00", Locale.JAPANESE).format(
                        inputDate
                    )
                )
                put("name", name)
                put("price", price)
                put("category", category)
                put("splitCount", 1)
                put("sign", 0)
            }
            // DBに登録する できなければエラーを返す
            database.insertOrThrow("log", null, values)
            return true
        } catch (exception: Exception) {
            Log.e("insertData", exception.toString()) // エラーをログに出力
            return false
        }
    }
}

class okCancelDialogFragment : DialogFragment() {

    // OKとキャンセルを表示するダイアログです

    // https://qiita.com/suzukihr/items/8973527ebb8bb35f6bb8

    // https://qiita.com/kumas/items/739f410438d182098b31

    var title = "タイトル"
    var message = "メッセージ"
    var okText = "OK"
    var onOkClickListener: DialogInterface.OnClickListener? = null
    var cancelText = "Cancel"
    var onCancelClickListener: DialogInterface.OnClickListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(getActivity()).run {
            setTitle(title)
            setMessage(message)
            setPositiveButton(okText, onOkClickListener)
            setNegativeButton(cancelText, onCancelClickListener)
            create()
        }
    }

    override fun onPause() {
        super.onPause()

        // onPause でダイアログを閉じる場合
        dismiss()
    }
}
