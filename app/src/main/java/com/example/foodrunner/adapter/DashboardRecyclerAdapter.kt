package com.example.foodrunner.adapter

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.foodrunner.R
import com.example.foodrunner.activity.RestaurantMenuActivity
import com.example.foodrunner.database.RestaurantDatabase
import com.example.foodrunner.database.RestaurantEntity
import com.example.foodrunner.model.Restaurent
import com.squareup.picasso.Picasso

class DashboardRecyclerAdapter(val context:Context, val itemList: ArrayList<Restaurent>): RecyclerView.Adapter<DashboardRecyclerAdapter.DashboardViewHolder>() {

    class DashboardViewHolder(view:View):RecyclerView.ViewHolder(view)
    {
        val restName: TextView=view.findViewById(R.id.restaurantNameView)
        val restRating: TextView=view.findViewById(R.id.textViewRating)
        val costperperson: TextView=view.findViewById(R.id.pricePerPerson)
        val restImage: ImageView=view.findViewById(R.id.restaurantImage)

        val favourites : TextView=view.findViewById(R.id.viewFavourite)
        val llContent:LinearLayout=view.findViewById(R.id.llContent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.recycler_dashboard_single_row,parent,false)
        return DashboardViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {

        val restaurent=itemList[position]
        holder.restName.text=restaurent.restaurentName
        holder.restRating.text=restaurent.restaurentRating
        holder.costperperson.text=restaurent.costperperson+"/Person"
        Picasso.get().load(restaurent.restaurentImage).error(R.drawable.ic_action_resaurent).into(holder.restImage)

        holder.llContent.setOnClickListener {
          val intent = Intent(context, RestaurantMenuActivity::class.java)
            intent.putExtra("id",restaurent.restaurentId)
            intent.putExtra("restaurantName", holder.restName.text.toString())
            context.startActivity(intent)
        }

        val restaurantEntity=RestaurantEntity(
            restaurent.restaurentId,
            restaurent.restaurentName,
            restaurent.restaurentRating,
            restaurent.costperperson,
            restaurent.restaurentImage
        )

        holder.favourites.setOnClickListener(View.OnClickListener {
            if (!DBAsynTask(context,restaurantEntity,1).execute().get()) {

                val result=DBAsynTask(context,restaurantEntity,2).execute().get()

                if(result){

                    Toast.makeText(context,"Added to favourites",Toast.LENGTH_SHORT).show()

                    holder.favourites.setTag("liked")//new value
                    holder.favourites.background =
                        context.resources.getDrawable(R.drawable.ic_action_favourites)
                }else{
                    Toast.makeText(context,"Some error occured",Toast.LENGTH_SHORT).show()
                }

            } else {

                val result=DBAsynTask(context,restaurantEntity,3).execute().get()
                if(result){

                    Toast.makeText(context,"Removed from favourites", Toast.LENGTH_SHORT).show()

                    holder.favourites.setTag("unliked")
                    holder.favourites.background =
                        context.resources.getDrawable(R.drawable.ic_action_favouritesoutlined)
                }else{

                    Toast.makeText(context,"Some error occured",Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    class DBAsynTask(val context: Context, val restaurantEntity: RestaurantEntity, val mode: Int) : AsyncTask<Void, Void, Boolean>() {

        val db=Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurant-db").build()

        override fun doInBackground(vararg p0: Void?): Boolean {
            /*
            * Mode 1->check if restaurant is in favourites
            * Mode 2->Save the restaurant into DB as favourites
            * Mode 3-> Remove the favourite restaurant*/

            when (mode) {
                1 -> {
                    val restaurant: RestaurantEntity? = db.restaurantDao()
                        .getRestaurantById(restaurantEntity.restaurantId)
                    db.close()
                    return restaurant != null
                }
                2 -> {
                    db.restaurantDao().insertRestaurant(restaurantEntity)
                    db.close()
                    return true
                }
                3 -> {
                    db.restaurantDao().deleteRestaurant(restaurantEntity)
                    db.close()
                    return true
                }
                else->return false
            }
        }
    }


}