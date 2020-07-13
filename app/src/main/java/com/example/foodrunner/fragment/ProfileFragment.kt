package com.example.foodrunner.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.foodrunner.R

class ProfileFragment(val profilecontext: Context) : Fragment() {

    lateinit var name: TextView
    lateinit var email:TextView
    lateinit var mobileNumber:TextView
    lateinit var address:TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_profile, container, false)

        name=view.findViewById(R.id.profileName)
        email=view.findViewById(R.id.profileEmail)
        mobileNumber=view.findViewById(R.id.profileMobileNumber)
        address=view.findViewById(R.id.profileDelievery)

        val sharedPreferencess=profilecontext.getSharedPreferences(getString(R.string.shared_preferences), Context.MODE_PRIVATE)
        name.text=sharedPreferencess.getString("name","")
        email.text=sharedPreferencess.getString("email","")
        mobileNumber.text="+91-"+sharedPreferencess.getString("mobile_number","")
        address.text=sharedPreferencess.getString("address","")

        return view
    }

}