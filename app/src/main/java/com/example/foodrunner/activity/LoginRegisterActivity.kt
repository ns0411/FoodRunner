package com.example.foodrunner.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.foodrunner.R
import com.example.foodrunner.fragment.LoginFragment

class LoginRegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_register)

        val sharedPreferencess=getSharedPreferences(getString(R.string.shared_preferences),
            Context.MODE_PRIVATE)

        if(sharedPreferencess.getBoolean("user_logged_in",false)){
            val intent= Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish();
        }else{
            setLoginFragment()
        }

    }

    fun setLoginFragment()
    {
        val transaction =supportFragmentManager.beginTransaction()
        transaction.replace(R.id.loginregisterLayout,LoginFragment(this))
        transaction.commit()
    }
    override fun onBackPressed() {
        val currFragment = supportFragmentManager.findFragmentById(R.id.loginregisterLayout)

        when(currFragment){
            !is LoginFragment -> setAnimatedLoginFragment()
            else ->
            {
                val homeIntent = Intent(Intent.ACTION_MAIN)
                homeIntent.addCategory(Intent.CATEGORY_HOME)
                homeIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(homeIntent)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id=item.itemId

        when(id)
        {
            android.R.id.home->{
                setAnimatedLoginFragment()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun setAnimatedLoginFragment()
    {
        val transaction =supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        transaction.replace(R.id.loginregisterLayout,LoginFragment(this))
        transaction.commit()
    }
}