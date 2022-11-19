package com.example.tflite_demo04_dogs_vs_cats

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.TextureView
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraX
import androidx.camera.core.Preview
import androidx.camera.core.PreviewConfig
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner

//这是设置请求权限的code码
private const val REQUEST_CODE_PERMISSIONS = 1
//这是要获取的权限
private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA,
    Manifest.permission.READ_EXTERNAL_STORAGE)

class MainActivity : AppCompatActivity() {
    private lateinit var view_finder:TextureView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        view_finder = findViewById(R.id.view_finder)

        // 判断是否有权限
        if (allPermissionsGranted()) {
            // Toast.makeText(this, "已获取权限！", Toast.LENGTH_SHORT).show()
            view_finder.post{ startCamera() }
        } else {
            //请求权限数组中的权限，并在获取后返回请求码
            ActivityCompat.requestPermissions(this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS)
        }
    }

    /**
     * 处理权限请求的结果 对话框中有被批准了？否则请提示
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                //Toast.makeText(this, "权限获取成功！！", Toast.LENGTH_SHORT).show()
                view_finder.post{ startCamera() }
            } else {
                Toast.makeText(this, "没有权限！！", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }


    /**
     * 检查权限是否被授权
     */
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * 初始化拍照
     */
    private fun startCamera() {
        //为取景器用例创建配置对象
        val previewConfig = PreviewConfig.Builder().apply {
            //设置取景器前后摄像头
            setLensFacing(CameraX.LensFacing.BACK)
        }.build()

        // 创建预览
        val preview = Preview(previewConfig)

        // 输出到视图控件上
        preview.setOnPreviewOutputUpdateListener {
            val parent = view_finder.parent as ViewGroup
            parent.removeView(view_finder)
            parent.addView(view_finder, 0)
            view_finder.setSurfaceTexture(it.surfaceTexture)
        }

        // 将用例绑定到生命周期
        CameraX.bindToLifecycle(this as LifecycleOwner, preview)
    }
}