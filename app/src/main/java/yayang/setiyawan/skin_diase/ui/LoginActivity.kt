package yayang.setiyawan.skin_diase.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import yayang.setiyawan.skin_diase.MainActivity
import yayang.setiyawan.skin_diase.R
import yayang.setiyawan.skin_diase.app.ApiConfig
import yayang.setiyawan.skin_diase.helper.SharedPref
import yayang.setiyawan.skin_diase.model.ResponModel

class LoginActivity : AppCompatActivity() {
    lateinit var sharedPref: SharedPref
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        sharedPref = SharedPref(this)
        btn_login.setOnClickListener {
            login()
        }
    }

    private fun login(){
        if (edt_email.text.isEmpty()){
            edt_email.error = "Kolom Tidak Boleh Kosong"
            edt_email.requestFocus()
            return
        }else if (edt_pass.text.isEmpty()){
            edt_pass.error = "Kolom Tidak Boleh Kosong"
            edt_pass.requestFocus()
            return
        }
        progressBar.visibility = View.VISIBLE
        ApiConfig.instanceRetrofit.login(edt_email.text.toString(),edt_pass.text.toString()).enqueue(object : Callback<ResponModel>{
            override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {
                progressBar.visibility = View.GONE
                val respon = response.body()!!
                if (respon.success == 1){
                    sharedPref.setStatusLogin(true)
                    sharedPref.setUser(respon.user)
                    val intent = Intent(this@LoginActivity,MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    Toast.makeText(this@LoginActivity, "Selamat datang " + respon.user.name, Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this@LoginActivity, "Error:" + respon.message, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponModel>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }
}