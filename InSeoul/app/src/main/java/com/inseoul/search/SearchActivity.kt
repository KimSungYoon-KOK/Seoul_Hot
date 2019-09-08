package com.inseoul.search


import android.app.Activity
import android.content.Intent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.inseoul.R
import com.inseoul.add_place.AddPlaceActivity
import com.inseoul.api_manager.RetrofitService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_add_place.*
import kotlinx.android.synthetic.main.activity_add_place.recyclerView
import kotlinx.android.synthetic.main.activity_register_review_page.*
import kotlinx.android.synthetic.main.activity_review.*
import kotlinx.android.synthetic.main.activity_search.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import kotlin.collections.ArrayList

class SearchActivity : AppCompatActivity() {
    var hNUM: ArrayList<Int> = ArrayList()
    var succ = false
    var upNUM: ArrayList<Int> = ArrayList()

    lateinit var category: ArrayList<ArrayList<Search_Item>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        setSupportActionBar(toolbar_search)

        init()
        initViewPager()

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

                searchKeyword(p0!!)
//                initData(p0)
                initViewPager()
                /////////////////////////////////////////////////////////////////////
                return false
            }
        })
    }

    ////////////////////// Tour API //////////////////////
    fun searchKeyword(keyword:String){
        val MobileOS = "AND"
        val MobileApp = "InSeuol"
//        val contentType = 12
        val areaCode = 1
        val _type = "json"
        // ContentType
        // 관광지 12

        // 문화시설 14
        // 행사/공연/축제 15
        // 레포츠 28

        // 숙박 32

        // 음식점 39
        val retrofit = Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
//            .client(createOkHttpClient())
            .baseUrl("http://api.visitkorea.or.kr/openapi/service/rest/KorService/")
            .build()
            .create(RetrofitService::class.java)
            .searchKeyWord(keyword, areaCode, MobileOS, MobileApp, _type)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                var d =
                Log.v("tlqkf",it.toString())
                var str = ""

                var tour = ArrayList<Search_Item>()
                var culture = ArrayList<Search_Item>()
                var food = ArrayList<Search_Item>()
                var hotel = ArrayList<Search_Item>()
                for(i in 0..it.response.body.items.item.size - 1){
                    val data = it.response.body.items.item[i]
                    Log.v("tlqkf",data.firstimage2.toString())

                    var searchitem = Search_Item(
                        data.contentid,
                        data.title,
                        data.firstimage2.toString(),
                        data.contenttypeid,
                        data.mapx,
                        data.mapy,
                        data.addr1,
                        data.addr2,
                        data.tel
                    )
                    when(data.contenttypeid){
                        12 ->{
                            tour.add(searchitem)
                        }
                        14, 15, 28, 38 ->{
                            culture.add(searchitem)
                        }
                        39->{
                            food.add(searchitem)
                        }
                        32->{
                            hotel.add(searchitem)
                        }
                    }

                }
                adapter.itemlist[0] = tour
                adapter.itemlist[1] = culture
                adapter.itemlist[2] = food
                adapter.itemlist[3] = hotel

                adapter.notifyDataSetChanged()
//                test_text.text = str

            },{
                Log.v("Fail","")
            })
    }
    fun createOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        builder.addInterceptor(interceptor)
        return builder.build()
    }
    //////////////////////////////////////////////////////
    fun init() {
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

    }

    //////////////////서버 통신////////////////////
    fun initData(p0: String?) {

    }




    lateinit var adapter:SearchViewPagerAdpater
    fun initViewPager(){
        category = ArrayList()
        for(i in 0..3){
            category.add(ArrayList<Search_Item>())
        }
        val flag = intent.hasExtra("flag")


        val listener = object : SearchAdapter.RecyclerViewAdapterEventListener {
            override fun onClick(view: View, position: Int, categoryIndex:Int) {
                val intent = Intent(this@SearchActivity,AddPlaceActivity::class.java)
                intent.putExtra("placeData", category[categoryIndex][position])
                setResult(Activity.RESULT_OK,intent)
                finish()
            }
        }
        adapter =  SearchViewPagerAdpater(this, category, listener, flag)
        search_viewpager.adapter = adapter
        TabLayoutMediator(search_tabLayout, search_viewpager, object : TabLayoutMediator.OnConfigureTabCallback {
            override fun onConfigureTab(tab: TabLayout.Tab, position: Int) {
                // Styling each tab here
                when(position){
                    0 -> {
                        tab.setText("관광")
                    }
                    1 -> {
                        tab.setText("문화/쇼핑")
                    }
                    2 -> {
                        tab.setText("맛집")
                    }
                    3 -> {
                        tab.setText("숙박")
                    }
                }
            }
        }).attach()
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
