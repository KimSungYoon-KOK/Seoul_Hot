package com.inseoul.search

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.inseoul.R
import com.inseoul.SearchDetails.SearchDetail
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.activity_search.recyclerView
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class SearchActivity : AppCompatActivity() {
    var hNUM: ArrayList<Int> = ArrayList()
    var succ = false
    var upNUM: ArrayList<Int> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        setSupportActionBar(toolbar_search)

        //toolbar 커스텀 코드
        val mtoolbar = findViewById(R.id.toolbar_search) as Toolbar
        setSupportActionBar(mtoolbar)
        // Get the ActionBar here to configure the way it behaves.
        val actionBar = supportActionBar
        actionBar!!.setDisplayShowCustomEnabled(true) //커스터마이징 하기 위해 필요
        actionBar.setDisplayShowTitleEnabled(false)

        actionBar.setDisplayHomeAsUpEnabled(true) // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김
        actionBar.setHomeAsUpIndicator(R.drawable.back_arrow) //뒤로가기 버튼을 본인이 만든 아이콘으로 하기 위해 필요

        searchView.performClick()
        searchView.requestFocus()
        searchView.isSubmitButtonEnabled = true

        // Search View EventListener
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            // Text Change
            override fun onQueryTextChange(p0: String?): Boolean {
                //Log.d("Text Change",p0)

                //////////////////////// DB Connect & Query ////////////////////////

                /////////////////////////////////////////////////////////////////////

                return false
            }

            // Submit
            override fun onQueryTextSubmit(p0: String?): Boolean {

                //Log.d("Submit",p0)

                //////////////////////// DB Connect & Query ////////////////////////

                val responseListener = Response.Listener<String> { response ->
                    try {
                        Log.d("dd", response)
                        val jsonResponse = JSONObject(response)
                        val success = jsonResponse.getJSONArray("response")
                        var count = 0
                        while (count < success.length()) {
                            val `object` = success.getJSONObject(count)
                            ////////////////////////
                            `object`.getInt("H") //Hash값
                            hNUM.add(`object`.getInt("H"))

                            count++

                        }
                        if (success.length() >= 1) {
                            succ = true
                            val responseListener2 = Response.Listener<String> { response ->
                                Log.d("dd", succ.toString())
                                if (succ) {
                                    try {
                                        Log.d("dd", response)
                                        val jsonResponse = JSONObject(response)
                                        val success = jsonResponse.getJSONArray("response")
                                        var count = 0

                                        while (count < success.length()) {
                                            val `object` = success.getJSONObject(count)

                                            val s = `object`.getString("H")
                                                .split(",")// 업소번호의 출력 형태가 int,int,int,... 식이어서 split(',') 필요
                                            for (i in s) {
                                                if (i == "") continue
                                                if (upNUM.contains(i.toInt())) continue
                                                upNUM.add(i.toInt()) /// 업소 번호를 저장
                                            }
                                            count++
                                        }

                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }

                                }
                            }
                            if (succ) {
                                for (j in hNUM.indices) {
                                    val idnumrequest2 = ConnectRequest(hNUM.get(j).toString(), responseListener2)
                                    var queue = Volley.newRequestQueue(this@SearchActivity)
                                    queue.add(idnumrequest2)
                                }
                                val responseListener3 = Response.Listener<String> { response ->
                                    readFile()
                                    initRecyclerView()
                                }
                                val idnumrequest3 = ConnectRequest("oooooo", responseListener3)
                                var queue = Volley.newRequestQueue(this@SearchActivity)
                                queue.add(idnumrequest3)
                            }

                        } else {
                            Toast.makeText(this@SearchActivity,"핫플레이스가 아니에요 ㅠㅠ",Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                val idnumrequest = SearchRequest(p0, responseListener)
                var queue = Volley.newRequestQueue(this@SearchActivity)
                queue.add(idnumrequest)
                queue.start()
                Log.d("dd", "queue")


                /////////////////////////////////////////////////////////////////////

                return false
            }
        })
    }

    fun readFile() {
        val scan = Scanner(resources.openRawResource(R.raw.inseoul_upso_data))
        var result = ""
        while (scan.hasNextLine()) {
            val line = scan.nextLine()
            result += line
        }
        parsingGson(result)
    }

    fun parsingGson(result: String) {
        val json = JSONObject(result)
        val array = json.getJSONArray("data")

        Log.d("alert_length",array.length().toString())

        if(array.length() == 0){
            placeList.add(SearchItem("핫플레이스가 아니에요 ㅠㅠ", R.drawable.ic_add_a_photo_black_24dp,0))
        }else{
            for (i in 0 until array.length()) {
                if (upNUM.contains(array.getJSONObject(i).getInt("Id_Num"))) {
                    val lng = array.getJSONObject(i).getString("Upso_nm")
                    val lat = array.getJSONObject(i).getString("class")

                    var flag: Int? = null
                    when (lat) {
                        "명소" -> {
                            flag = R.drawable.ic_add_a_photo_black_24dp
                        }
                        "맛집" -> {
                            flag = R.drawable.ic_add_a_photo_black_24dp
                        }
                        "쇼핑" -> {
                            flag = R.drawable.ic_add_a_photo_black_24dp
                        }
                    }
                    placeList.add(SearchItem(lng, flag!!,array.getJSONObject(i).getInt("Id_Num")))
                    //Log.d("Log", "$lat, $lng")
                }
            }
        }

    }


    ////////////////// Recycler View //////////////////
    private val placeList = ArrayList<SearchItem>()
    var layoutManager: RecyclerView.LayoutManager? = null
    var adapter: SearchAdapter? = null


    fun initRecyclerView() {
        layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView.layoutManager = layoutManager
        val listener = object : SearchAdapter.RecyclerViewAdapterEventListener {
            override fun onClick(view: View, position: Int) {

                //intent로 SearchItem 전달
                val detailsIntent = Intent(this@SearchActivity, SearchDetail::class.java)
                detailsIntent.putExtra("search_title", placeList[position].placeNm)
                detailsIntent.putExtra("search_id", placeList[position].id)
                startActivity(detailsIntent)
            }
        }

        adapter = SearchAdapter(this, listener, placeList)
        recyclerView.adapter = adapter
        //recyclerView.addItemDecoration(DividerItemDecoration(this, 1))
    }


    //toolbar에서 back 버튼
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
