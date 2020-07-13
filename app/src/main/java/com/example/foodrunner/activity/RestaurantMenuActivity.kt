 package com.example.foodrunner.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodrunner.R
import com.example.foodrunner.adapter.DashboardRecyclerAdapter
import com.example.foodrunner.adapter.RestaurantMenuRecyclerAdapter
import com.example.foodrunner.model.Restaurent
import com.example.foodrunner.model.RestaurentMenu
import com.example.foodrunner.util.ConnectionManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*
import org.json.JSONException
import org.json.JSONObject

 class RestaurantMenuActivity : AppCompatActivity() {

     lateinit var toolbar:androidx.appcompat.widget.Toolbar


     lateinit var recyclerView: RecyclerView
     lateinit var layoutManager: RecyclerView.LayoutManager
     lateinit var rmenuAdapter: RestaurantMenuRecyclerAdapter
     lateinit var progresslayout: RelativeLayout
     lateinit var proceedToCartLayout:RelativeLayout
     lateinit var buttonProceedToCart:Button

     var restaurantMenuList = arrayListOf<RestaurentMenu>()
     lateinit  var restId:String
     lateinit var restname:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_menu)
        toolbar=findViewById(R.id.restaurant_menu_toolBar)
        proceedToCartLayout=findViewById(R.id.ProceedToCartLayout)
        buttonProceedToCart=findViewById(R.id.ProceedCartBtn)
        progresslayout=findViewById(R.id.restaurant_menu_ProgressLayout)
        layoutManager = LinearLayoutManager(this)
        recyclerView = findViewById(R.id.recyclerViewRestaurantMenu)

        progresslayout.visibility=View.VISIBLE

                restId=intent.getStringExtra("id")
                restname=intent.getStringExtra("restaurantName")

        setUpToolbar()

        val queue= Volley.newRequestQueue(this)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/$restId"


        if (ConnectionManager().checkConnectivity(this)) {
            val jsonObjectRequest = object : JsonObjectRequest(
                Request.Method.GET,
                url,null,

                Response.Listener {
                    try {

                        val responseJsonObjectData = it.getJSONObject("data")
                        val success = responseJsonObjectData.getBoolean("success")
                        if (success) {
                            progresslayout.visibility=View.INVISIBLE
                            val data = responseJsonObjectData.getJSONArray("data")
                            for (i in 0 until data.length()) {
                                val restaurantJmenusonObject = data.getJSONObject(i)
                                val restaurantMenuObject = RestaurentMenu(
                                    restaurantJmenusonObject.getString("id"),
                                    restaurantJmenusonObject.getString("name"),
                                    restaurantJmenusonObject.getString("cost_for_one")
                                )
                                restaurantMenuList.add(restaurantMenuObject)

                                rmenuAdapter = RestaurantMenuRecyclerAdapter(this, restaurantMenuList,proceedToCartLayout,buttonProceedToCart,restId,restname)

                                recyclerView.adapter = rmenuAdapter
                                recyclerView.layoutManager = layoutManager

                            }
                        } else {
                            Toast.makeText(this, "Some Error Occured ", Toast.LENGTH_LONG).show()
                        }
                    }
                    catch (e: JSONException)
                    {
                        Toast.makeText(this, "Some Eexception Occured ", Toast.LENGTH_LONG).show()
                    }
                },
                Response.ErrorListener {

                    Toast.makeText(this, "Please Try again ", Toast.LENGTH_LONG).show()

                }) {

                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "application/json"
                    headers["token"] = "a3a4be4d5fe093"
                    return headers
                }
            }

            queue.add(jsonObjectRequest)

        }
        else{
            val dialog=androidx.appcompat.app.AlertDialog.Builder(this)
            dialog.setTitle("No Internet")
            dialog.setMessage("Internet Connection can't be establish!")
            dialog.setPositiveButton("Open Settings"){ text, listener->
                val settingsIntent= Intent(Settings.ACTION_WIFI_SETTINGS)
                startActivity(settingsIntent)

            }

            dialog.setNegativeButton("Exit"){ text, listener->
                ActivityCompat.finishAffinity(this)
            }
            dialog.setCancelable(false)

            dialog.create()
            dialog.show()

        }
    }

     fun setUpToolbar()
     {
         setSupportActionBar(toolbar)
         supportActionBar?.title=restname
         supportActionBar?.setHomeButtonEnabled(true)
         supportActionBar?.setDisplayHomeAsUpEnabled(true)

     }
     override fun onOptionsItemSelected(item: MenuItem): Boolean {

         val id=item.itemId

         when(id){
             android.R.id.home->{
                 if(rmenuAdapter.getSelectedItemCount()>0) {

                     val alterDialog = androidx.appcompat.app.AlertDialog.Builder(this)
                     alterDialog.setTitle("Alert!")
                     alterDialog.setMessage("Going back will remove everything from cart")
                     alterDialog.setPositiveButton("Okay") { text, listener ->
                         super.onBackPressed()
                     }
                     alterDialog.setNegativeButton("No") { text, listener ->

                     }
                     alterDialog.show()
                 }else{
                     super.onBackPressed()
                 }
             }
         }
         return super.onOptionsItemSelected(item)
     }

     override fun onBackPressed() {
         if(rmenuAdapter.getSelectedItemCount()>0) {

             val alterDialog = androidx.appcompat.app.AlertDialog.Builder(this)
             alterDialog.setTitle("Alert!")
             alterDialog.setMessage("Going back will remove everything from cart")
             alterDialog.setPositiveButton("Okay") { text, listener ->
                 super.onBackPressed()
             }
             alterDialog.setNegativeButton("No") { text, listener ->
             }
             alterDialog.show()
         }else{
             super.onBackPressed()
         }
     }
}