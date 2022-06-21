package yayang.setiyawan.skin_diase

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import com.github.florent37.runtimepermission.kotlin.askPermission
import kotlinx.android.synthetic.main.activity_main.*
import yayang.setiyawan.skin_diase.clasification.Clasification
import yayang.setiyawan.skin_diase.data.DetectionResult
import yayang.setiyawan.skin_diase.databinding.ActivityMainBinding
import yayang.setiyawan.skin_diase.fragment.AkunFragment
import yayang.setiyawan.skin_diase.fragment.HistoryFragment
import yayang.setiyawan.skin_diase.fragment.HomeFragment
import yayang.setiyawan.skin_diase.fragment.ArticleFragment
import yayang.setiyawan.skin_diase.helper.SharedPref
import yayang.setiyawan.skin_diase.ui.LoginActivity
import yayang.setiyawan.skin_diase.ui.ResultActivity

class MainActivity : AppCompatActivity() {
    companion object {
        private const val CAMERA_REQUEST_CODE = 2
        private const val IMAGE_REQUEST_CODE=100
    }
    private lateinit var binding:ActivityMainBinding

    private val mInputSize = 224
    private val mModelPath = "model_elequentv3.tflite"
    private val mLabelPath = "labels.txt"
    private lateinit var clasification: Clasification
    lateinit var sharedPref: SharedPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPref = SharedPref(this)
        clasification = Clasification(assets,mModelPath,mLabelPath,mInputSize)
        askPermissions()
        val homeFragment = HomeFragment()
        val historyFragment = HistoryFragment()
        val articleFragment = ArticleFragment()
        val akunFragment = AkunFragment()

        setCurrentFragment(homeFragment)
        binding.bottomNavigationView.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.nvHome ->setCurrentFragment(homeFragment)
                R.id.nvArticle->setCurrentFragment(articleFragment)
                R.id.nvHistory->setCurrentFragment(historyFragment)
                R.id.nvProfile->{
                    if (sharedPref.getStatusLogin()){
                        setCurrentFragment(akunFragment)
                    }else{
                        startActivity(Intent(this, LoginActivity::class.java))
                    }
                }
            }
            true
        }

        fab.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, CAMERA_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when(requestCode){
                CAMERA_REQUEST_CODE->{
                    val bitmap = data?.extras?.get("data") as Bitmap
                    val result = clasification.recognizeImage(bitmap)
                    val confidence = result[0].confidence * 100
                    val detectionResult = DetectionResult(
                        1,
                        bitmap,
                        result[0].title,
                        confidence.toInt()
                    )
                    val intent = Intent(this, ResultActivity::class.java)
                    intent.putExtra(ResultActivity.EXTRA_DATA, detectionResult)
                    startActivity(intent)
                }
                IMAGE_REQUEST_CODE->{
                    val bitmap = data?.extras?.get("data") as Bitmap
                    val result = clasification.recognizeImage(bitmap)
                    val confidence = result[0].confidence * 100
                    val detectionResult = DetectionResult(
                        1,
                        bitmap,
                        result[0].title,
                        confidence.toInt()
                    )
                    val intent = Intent(this, ResultActivity::class.java)
                    intent.putExtra(ResultActivity.EXTRA_DATA, detectionResult)
                    startActivity(intent)
                }
            }
        }


    }

    private fun setCurrentFragment(fragment: Fragment)=
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment,fragment)
            commit()
        }
    private fun askPermissions(){
        askPermission(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CAMERA
        ){

        }.onDeclined{e ->
            if (e.hasDenied()){
                e.denied.forEach{

                }

                AlertDialog.Builder(this)
                    .setMessage("Please Accept Our Permission")
                    .setPositiveButton("Yes"){_,_ ->
                        e.askAgain()
                    }
                    .setNegativeButton("No"){dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }

            if (e.hasForeverDenied()){
                e.foreverDenied.forEach {

                }
                e.goToSettings()
            }
        }
    }
}