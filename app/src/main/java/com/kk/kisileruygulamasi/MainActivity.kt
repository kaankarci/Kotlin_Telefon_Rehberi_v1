package com.kk.kisileruygulamasi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity(), SearchView.OnQueryTextListener {
    private lateinit var adapter: KisilerAdapter
    private lateinit var kdi: KisilerDaoInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar.title = "Kişiler Uygulaması"
        setSupportActionBar(toolbar)

        rv.setHasFixedSize(true)    //recyclerview yapısının guzel gorunmesı ıcın
        rv.layoutManager = LinearLayoutManager(this)

        kdi = ApiUtils.getKisilerDaoInterface()

        tumKisiler()


        fab.setOnClickListener { alertGoster() }


    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        val item = menu?.findItem(R.id.action_ara)
        val searchView = item?.actionView as SearchView
        searchView.setOnQueryTextListener(this)
        return super.onCreateOptionsMenu(menu)
    }


    fun alertGoster() {
        val tasarim = LayoutInflater.from(this).inflate(R.layout.alert_tasarim, null)
        val editTextAd = tasarim.findViewById(R.id.editTextAd) as EditText
        val editTextTel = tasarim.findViewById(R.id.editTextTel) as EditText

        val ad = AlertDialog.Builder(this)
        ad.setTitle("Kişi Ekle")
        ad.setView(tasarim)
        ad.setPositiveButton("Ekle") { dialogInterface, i ->
            val kisi_ad = editTextAd.text.toString().trim()
            val kisi_tel = editTextTel.text.toString().trim()
            kdi.kisiEkle(kisi_ad,kisi_tel).enqueue(object : Callback<CRUDCevap>{

                override fun onResponse(call: Call<CRUDCevap>?, response: Response<CRUDCevap>?) {
                    Toast.makeText(applicationContext, "${kisi_ad} başarıyla eklendi!", Toast.LENGTH_SHORT).show()
                tumKisiler()
                }

                override fun onFailure(call: Call<CRUDCevap>?, t: Throwable?) {
                    Toast.makeText(applicationContext, "Kişi eklenirken hata oluştu!", Toast.LENGTH_SHORT).show()                }
            })


        }
        ad.setNegativeButton("İptal") { dialogInterface, i ->


        }
        ad.create().show()
    }

    //arama kısmı basla
    fun aramaYap(aramaKelime: String) {
        kdi.kisiAra(aramaKelime).enqueue(object : Callback<KisilerCevap> {
            override fun onResponse(call: Call<KisilerCevap>?, response: Response<KisilerCevap>?) {

                if (response != null) {
                    val liste = response.body().kisiler

                    adapter = KisilerAdapter(this@MainActivity, liste,kdi)
                    rv.adapter = adapter
                }

            }

            override fun onFailure(call: Call<KisilerCevap>?, t: Throwable?) {
            }
        })
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null) {
            aramaYap(query)
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText != null) {
            aramaYap(newText)
        }

        return true
    }
    //arama kısmı bitiş

    fun tumKisiler() {
        kdi.tumKisiler().enqueue(object : Callback<KisilerCevap> {
            override fun onResponse(call: Call<KisilerCevap>?, response: Response<KisilerCevap>?) {

                if (response != null) {
                    val liste = response.body().kisiler

                    adapter = KisilerAdapter(this@MainActivity, liste,kdi)
                    rv.adapter = adapter
                }

            }

            override fun onFailure(call: Call<KisilerCevap>?, t: Throwable?) {
            }
        })
    }


}