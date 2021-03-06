package com.example.foodrunner.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodrunner.R
import com.example.foodrunner.model.CartItem
import com.example.foodrunner.model.OrderHistory
import com.example.foodrunner.util.ConnectionManager
import org.json.JSONException

class OrderHistoryAdapter(val context: Context, val orderedRestaurantList:ArrayList<OrderHistory>): RecyclerView.Adapter<OrderHistoryAdapter.ViewHolderOrderHistoryRestaurant>() {

    class ViewHolderOrderHistoryRestaurant(view: View) : RecyclerView.ViewHolder(view) {
        val textViewResturantName: TextView = view.findViewById(R.id.textViewResturantName)
        val textViewDate: TextView = view.findViewById(R.id.textViewDate)
        val recyclerViewItemsOrdered: RecyclerView =
            view.findViewById(R.id.recyclerViewItemsOrdered)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderOrderHistoryRestaurant {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_single_order, parent, false)

        return ViewHolderOrderHistoryRestaurant(view)
    }

    override fun getItemCount(): Int {
        return orderedRestaurantList.size
    }

    override fun onBindViewHolder(holder: ViewHolderOrderHistoryRestaurant, position: Int) {
        val restaurantObject = orderedRestaurantList[position]


        holder.textViewResturantName.text = restaurantObject.restaurantName
        var formatDate=restaurantObject.orderPlacedAt
        formatDate=formatDate.replace("-","/")//21-02-20 to 21/02/20
        formatDate=formatDate.substring(0,6)+"20"+formatDate.substring(6,8)//21/02/20 to 21/02/2020
        holder.textViewDate.text =  formatDate


        var layoutManager = LinearLayoutManager(context)
        var orderedItemAdapter: CartAdapter// the same cart adapter can be used to fill the ordered items for each restaurant

        if (ConnectionManager().checkConnectivity(context)) {

            try {

                val orderItemsPerRestaurant=ArrayList<CartItem>()

                val sharedPreferencess=context.getSharedPreferences(context.getString(R.string.shared_preferences),Context.MODE_PRIVATE)
                val user_id=sharedPreferencess.getString("user_id","0")
                val queue = Volley.newRequestQueue(context)

                val url="http://13.235.250.119/v2/orders/fetch_result/$user_id"

                val jsonObjectRequest = object : JsonObjectRequest(
                    Request.Method.GET,
                    url,
                    null,
                    Response.Listener {

                        val responseJsonObjectData = it.getJSONObject("data")
                        val success = responseJsonObjectData.getBoolean("success")
                        if (success) {

                            val data = responseJsonObjectData.getJSONArray("data")
                            val fetchedRestaurantJsonObject = data.getJSONObject(position)
                            orderItemsPerRestaurant.clear()

                            val foodOrderedData=fetchedRestaurantJsonObject.getJSONArray("food_items")

                            for(j in 0 until foodOrderedData.length())
                            {
                                val eachFoodItem = foodOrderedData.getJSONObject(j)
                                val itemObject = CartItem(
                                    eachFoodItem.getString("food_item_id"),
                                    eachFoodItem.getString("name"),
                                    eachFoodItem.getString("cost"),
                                    "000"
                                )
                                orderItemsPerRestaurant.add(itemObject)
                            }

                            orderedItemAdapter = CartAdapter(context, orderItemsPerRestaurant)
                            holder.recyclerViewItemsOrdered.adapter = orderedItemAdapter
                            holder.recyclerViewItemsOrdered.layoutManager = layoutManager

                        }
                    },
                    Response.ErrorListener {

                        Toast.makeText(context, "Some Error occurred!", Toast.LENGTH_SHORT).show()
                    }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()

                        headers["Content-type"] = "application/json"
                        headers["token"] ="a3a4be4d5fe093"

                        return headers
                    }
                }

                queue.add(jsonObjectRequest)

            } catch (e: JSONException) {
                Toast.makeText(context, "Some Unexpected error occurred!", Toast.LENGTH_SHORT).show()
            }

        }



    }
}
