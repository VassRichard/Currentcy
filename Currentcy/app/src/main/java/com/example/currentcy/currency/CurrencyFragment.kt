package com.example.currentcy.currency

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.android.volley.Request.Method.GET
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.currentcy.R
import com.example.currentcy.currency.currency_list.CurrencyListActivity
import com.example.currentcy.databinding.FragmentCurrencyBinding
import org.json.JSONException
import kotlin.collections.ArrayList

private var currencyList: ArrayList<Currencies>? = ArrayList<Currencies>()
private var currencyItem: List<Currencies>? = null

private var currencyListMultiply: ArrayList<Currencies>? = ArrayList<Currencies>()

private val api_key = "ff4b13a6965fddcc4e972fd82e3a6be9"

// CurrencyAdapterInfo
class CurrencyFragment : Fragment() {

    lateinit var binding: FragmentCurrencyBinding
    lateinit var currencyViewModel: CurrencyViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_currency, container, false)
        currencyViewModel = ViewModelProvider(this).get(CurrencyViewModel::class.java)

        binding.currencyViewModel = currencyViewModel

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onStart() {
        super.onStart()

        getPostVolley()

        currencyViewModel.calculateRates.observe(viewLifecycleOwner, Observer {
            if (it) {
                val multiplicatonRate = binding.currencyInput.text.toString()

                currencyListMultiply?.clear()

                if (!multiplicatonRate.isEmpty()) {
                    for (elements in currencyList!!) {
                        val tmpCurrencyName = elements.name

                        val tmpCurrencyRate =
                            elements.rate.toDouble() * multiplicatonRate.toDouble()

                        val tmpCurrency = Currencies(tmpCurrencyName, tmpCurrencyRate.toString())

                        currencyListMultiply!!.add(tmpCurrency)
                    }

                    val adapter = CurrencyAdapter(currencyListMultiply)

                    binding.currencyList.adapter = adapter

                    currencyViewModel.onCalculateReset()
                } else {
                    Toast.makeText(context, "Multiplication rate not set!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        })

        currencyViewModel.editList.observe(viewLifecycleOwner, Observer {
            if(it) {
                val intent = Intent(context, CurrencyListActivity::class.java)

                startActivity(intent)
                currencyViewModel.onEditListReset()
            }
        })
    }

    override fun onPause() {
        super.onPause()

        currencyList?.clear()
        currencyListMultiply?.clear()
    }

    fun getPostVolley() {
        val url = "http://api.exchangeratesapi.io/v1/latest?access_key=" + api_key

        val requestQueue: com.android.volley.RequestQueue? = Volley.newRequestQueue(context)
        val jsonObjectRequest =
            JsonObjectRequest(GET, url, null,
                { response ->
                    try {
                        val jsonArray = response.getJSONObject("rates")
                        val keys = jsonArray.keys()

                        currencyItem = ArrayList<Currencies>()

                        while (keys.hasNext()) {
                            val key = keys.next()
                            val value = jsonArray.optString(key)

                            val tmpCurrency = Currencies(key, value)

                            currencyList!!.add(tmpCurrency)
                        }
                        val adapter = CurrencyAdapter(currencyList)

                        binding.currencyList.adapter = adapter
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Log.e("ERROR: ", "$e")
                    }
                }) { error -> error.printStackTrace()
            Log.e("ERROR", error.toString())}

        if (requestQueue != null) {
            requestQueue.add(jsonObjectRequest)
        }
    }

//    override fun editItem(currentItem: CurrencyData) {
//        TODO("Not yet implemented")
//    }
}