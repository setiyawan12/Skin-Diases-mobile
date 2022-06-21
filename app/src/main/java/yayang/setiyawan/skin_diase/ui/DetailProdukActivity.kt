package yayang.setiyawan.skin_diase.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_detail_produk.*
import kotlinx.android.synthetic.main.toolbar_custom.*
import yayang.setiyawan.skin_diase.R
import yayang.setiyawan.skin_diase.helper.Helper
import yayang.setiyawan.skin_diase.model.Produk

class DetailProdukActivity : AppCompatActivity() {
    lateinit var produk:Produk
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_produk)
        getInfo()
    }

    private fun getInfo(){
        val data = intent.getStringExtra("extra")
        produk = Gson().fromJson<Produk>(data,Produk::class.java)
        tv_detailnama.text = produk.name
        tv_detaildeskripsi.text = produk.deskripsi
        val img = produk.image
        Picasso.get()
            .load(img)
            .placeholder(R.drawable.ic_launcher_foreground)
            .error(R.drawable.ic_launcher_foreground)
            .resize(400,400)
            .into(image)
        Helper().setToolbar(this,toolbar,produk.name.toString())
    }
}