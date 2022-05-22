package com.raonstudio.customincalldisplay.views.main

import android.app.role.RoleManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.telecom.TelecomManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.raonstudio.customincalldisplay.R
import com.raonstudio.customincalldisplay.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    //RoleManager 방식은 api 29 부터 사용할 수 있음
    private val roleManager: RoleManager? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getSystemService(ROLE_SERVICE) as RoleManager
        } else null
    }
    private val telecomManager: TelecomManager by lazy { getSystemService(TELECOM_SERVICE) as TelecomManager }

    private val isDefaultDialer get() = packageName.equals(telecomManager.defaultDialerPackage)

    private val changeDefaultDialerIntent
        get() = if (isDefaultDialer) {
            //앱이 이미 기본 전화앱이라면 기본 앱 변경 팝업이 뜨지 않음
            //이 경우 이 앱이 아닌 다른 앱으로 변경하기 위해 기본 앱 설정 화면에서 수동으로 기본 전화 앱을 바꿔줘야 함
            Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS)
        } else {
            //roleManager 방식은 api29 부터 동작하여 버전에 따른 intent 객체를 만들어 줌
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                roleManager!!.createRequestRoleIntent(RoleManager.ROLE_DIALER)
            } else {
                Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER).apply {
                    putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, packageName)
                }
            }
        }

    //startActivityForResult deprecate 이후로 ActivityResultLauncher 를 이용하여 같은 기능을 수행함
    //이 객체는 무조건 액티비티 생성시에 같이 생성되어야 함
    //onCreate 안에서 객체를 생성할 경우 크래시 발생
    private val changeDefaultDialerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            //이 람다 함수에는 기존의 onActivityResult 콜백에 들어갈 내용이 들어감
            binding.isDefaultDialer = isDefaultDialer
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityMainBinding?>(this, R.layout.activity_main).apply {
            lifecycleOwner = this@MainActivity
            isDefaultDialer = this@MainActivity.isDefaultDialer
        }

        binding.changeDefaultDialer.setOnClickListener {
            //기존의 startActivityForResult 호출과 같은 기능을 함
            changeDefaultDialerLauncher.launch(changeDefaultDialerIntent)
        }
    }
}