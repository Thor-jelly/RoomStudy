package com.ddongwu.roomstudy

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.ddongwu.roomstudy.database.AppDatabaseUtils
import com.ddongwu.roomstudy.database.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val rv: RecyclerView by lazy { findViewById<RecyclerView>(R.id.rv) }
    private val optionRv: RecyclerView by lazy { findViewById<RecyclerView>(R.id.option_rv) }
    private val queryEd: EditText by lazy { findViewById<EditText>(R.id.query_ed) }

    private val mShowList by lazy {
        arrayListOf<User>()
    }

    private val mOptionList by lazy {
        arrayListOf(
            "插入用户",
            "批量插入用户",
            "更新用户",
            "更新用户id",
            "删除用户",
            "查询用户",
            "查询用户id"
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initRv()
    }

    private fun initRv() {
        initShowRv()
        initOptionRv()
    }

    private fun initOptionRv() {
        optionRv.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): RecyclerView.ViewHolder {
                val view =
                    LayoutInflater.from(parent.context).inflate(R.layout.item_option, parent, false)
                return MViewHolder(view)
            }

            override fun getItemCount(): Int {
                return mOptionList.size
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                val model = mOptionList[position]
                holder.itemView.findViewById<TextView>(R.id.option_tv).apply {
                    text = model
                    setOnClickListener {
                        when (model) {
                            "插入用户" -> {
                                val id = queryEd.text.toString()
                                val addModel = User(
                                    id.toString(),
                                    "f$id",
                                    "l$id",
                                    //Book(id, "书名$id")
                                )
                                lifecycleScope.launch(Dispatchers.IO) {
                                    AppDatabaseUtils.getInstance().userDao.insertAll(
                                        addModel
                                    )
                                }

                                mShowList.add(addModel)
                            }

                            "批量插入用户" -> {
                                for (i in 0..5) {
                                    val id = queryEd.text.toString()
                                    val addModel = User(
                                        id,
                                        "f$id",
                                        "l$id",
                                        //Book(id, "书名$id")
                                    )
                                    lifecycleScope.launch(Dispatchers.IO) {
                                        val insertNumber =
                                            AppDatabaseUtils.getInstance().userDao.insertAll(
                                                addModel
                                            )
                                        if (insertNumber == null) {
                                            Log.d("123===", "插入失败")
                                        } else {
                                            for (i in insertNumber) {
                                                Log.d("123===", "=====$i")
                                            }
                                        }
                                    }
                                    mShowList.add(addModel)
                                }
                            }

                            "删除用户" -> {
                                val queryStr = queryEd.text.toString()
                                lifecycleScope.launch(Dispatchers.IO) {
                                    val resultI = if (queryStr.isNullOrEmpty()) {
                                        val delModel = mShowList.last()
                                        AppDatabaseUtils.getInstance().userDao.delete(delModel)
                                    } else {
                                        AppDatabaseUtils.getInstance().userDao.delete(queryStr)
                                    }
                                    Log.d("123===", "=====$resultI")
                                }
                            }

                            "查询用户" -> {
                                lifecycleScope.launch(Dispatchers.IO) {
                                    val list = AppDatabaseUtils.getInstance().userDao.getAll()
                                    Log.d("123===", "=====${list?.size}")
                                    mShowList.clear()
                                    list?.let { it1 -> mShowList.addAll(it1) }
                                }
                            }

                            "查询用户id" -> {
                                lifecycleScope.launch(Dispatchers.IO) {
                                    val i = AppDatabaseUtils.getInstance().userDao.findByNameFuzzy(
                                        queryEd.text.toString()
                                    )
                                    Log.d("123===", "=====$i")
                                    mShowList.clear()
                                    i?.let { it1 -> mShowList.add(it1) }
                                }
                            }

                            "更新用户" -> {
                                lifecycleScope.launch(Dispatchers.IO) {
                                    val updateUid = queryEd.text.toString()
                                    val newModel = User(
                                        updateUid,
                                        "f$updateUid f",
                                        "l$updateUid l"
                                    )
                                    val i =
                                        AppDatabaseUtils.getInstance().userDao.updateUser(newModel)
                                    Log.d("123===", "=====$i")
                                }
                            }

                            "更新用户id" -> {
                                lifecycleScope.launch(Dispatchers.IO) {
                                    val updateUid = queryEd.text.toString()
                                    val i =
                                        AppDatabaseUtils.getInstance().userDao.updateUser(updateUid)
                                    Log.d("123===", "=====$i")
                                }
                            }
                        }
                        rv.adapter?.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    private fun initShowRv() {
        rv.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): RecyclerView.ViewHolder {
                val view =
                    LayoutInflater.from(parent.context).inflate(R.layout.item_rv, parent, false)
                return MViewHolder(view)
            }

            override fun getItemCount(): Int {
                return mShowList.size
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                val model = mShowList[position]
                holder.itemView.findViewById<TextView>(R.id.tv).apply {
                    text = "${model.firstName}~~~~~~${model.lastName}}"
                    //text = "${model.firstName}~~~~~~${model.lastName} \n 书名${model.favoriteBook?.bookName}"
                }
            }
        }
    }

    private class MViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}