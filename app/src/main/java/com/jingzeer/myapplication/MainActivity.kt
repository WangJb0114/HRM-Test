package com.jingzeer.myapplication

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.onecoder.fitblekit.API.ArmBand.FBKApiArmBand
import com.onecoder.fitblekit.API.ArmBand.FBKApiArmBandCallBack
import com.onecoder.fitblekit.API.Base.FBKApiBsaeMethod
import com.onecoder.fitblekit.API.ScanDevices.FBKApiScan
import com.onecoder.fitblekit.API.ScanDevices.FBKApiScanCallBack
import com.onecoder.fitblekit.Ble.FBKBleDevice.FBKBleDevice
import com.onecoder.fitblekit.Ble.FBKBleDevice.FBKBleDeviceStatus


class MainActivity : AppCompatActivity() {

    private var mScanner: FBKApiScan? = null
    private var mApiArm: FBKApiArmBand? = null

    private var myAdapter: MyAdapter? = null
    private var listInfo: List<FBKBleDevice> = arrayListOf()

    private var textView: TextView? = null
    private var recyclerView: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textView = findViewById(R.id.textView)
        recyclerView = findViewById(R.id.recyclerView)
        findViewById<Button>(R.id.button).setOnClickListener {
            mScanner?.startScan(m_apiScanCallBack)
        }
        findViewById<Button>(R.id.button2).setOnClickListener {
            mScanner?.stopScan()
        }
        findViewById<Button>(R.id.button4).setOnClickListener {
            disconnectBle()
        }

        findViewById<Button>(R.id.button5).setOnClickListener {
            mApiArm?.readDeviceBatteryPower()
        }
        findViewById<Button>(R.id.button6).setOnClickListener {
            mApiArm?.readFirmwareVersion()
        }

        findViewById<Button>(R.id.button8).setOnClickListener {
            mApiArm?.setShock(100)
        }

        findViewById<Button>(R.id.button9).setOnClickListener {
            mApiArm?.closeShock()
        }
        initApi()
        initRecycler()
    }

    private fun initRecycler() {
        myAdapter = MyAdapter()
        recyclerView?.apply {
            adapter = myAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
        myAdapter?.onItemClickListener = object : OnItemClickListener {
            override fun onItemClickL(device: FBKBleDevice) {
                mApiArm?.connecBluetooth(device.bleDevice)
            }
        }
    }

    private fun disconnectBle() {
        mApiArm?.apply {
            disconnectBle()
            unregisterBleListenerReceiver()
        }
    }

    private fun initApi() {
        mScanner = FBKApiScan()
        mScanner?.apply {
            setScanRssi(-100)
        }
        mApiArm = FBKApiArmBand(this, object : FBKApiArmBandCallBack {
            override fun bleConnectError(p0: String?, p1: FBKApiBsaeMethod?) {
                //connection error info p0
                textView?.text = "Connection error: $p0"
            }

            override fun bleConnectStatus(p0: FBKBleDeviceStatus, p1: FBKApiBsaeMethod?) {
                //Connection status callback
                println("====================${p0.name}")
                textView?.text = "Connection status: $p0"
            }

            override fun batteryPower(p0: Int, p1: FBKApiBsaeMethod?) {
                //电池电量回调 p0百分比
                println("===================$p0")
                textView?.text = "batteryPower $p0 %"
            }

            override fun protocolVersion(p0: String?, p1: FBKApiBsaeMethod?) {
                //协议版本回调 p0 设备协议版本号
                textView?.text = "protocolVersion $p0"
            }

            override fun firmwareVersion(p0: String?, p1: FBKApiBsaeMethod?) {
                //固件版本回调 p0
                textView?.text = "protocolVersion $p0"
            }

            override fun hardwareVersion(p0: String?, p1: FBKApiBsaeMethod?) {
                //硬件版本回调 p0
                textView?.text = "protocolVersion $p0 %"
            }

            override fun softwareVersion(p0: String?, p1: FBKApiBsaeMethod?) {
                //软件版本回调 p0
                textView?.text = "protocolVersion $p0 %"
            }

            override fun realTimeHeartRate(p0: Any?, p1: FBKApiArmBand?) {
                //实时心率回调
                val hashMap: Map<String, Any> = p0 as Map<String, Any>
                hashMap.apply {
                    get("timeStamps")//时间戳
                    get("createTime") //时间
                    get("dataLength")//保留字段
                    get("heartRate") //实时心率
                    get("HRV") //实时HRV 部分设备有此参数
                    get("interval") //list<string> 实时心率RR值 部分设备有此参数
                }
            }

            override fun realTimeStepFrequency(p0: Any?, p1: FBKApiArmBand?) {
                //实时步频回调
                val hashMap: Map<String, Any> = p0 as Map<String, Any>
                hashMap.apply {
                    get("timeStamps")//时间戳
                    get("createTime") //时间
                    get("steps")//实时步数
                    get("stepFrequency")//实时步频
                    get("calories") //实时卡路里
                }
            }

            override fun armBandTemperature(p0: Any?, p1: FBKApiArmBand?) {
                //实时温度回调
                val hashMap: Map<String, Any> = p0 as Map<String, Any>
                hashMap.apply {
                    get("surfaceTemperature") //体表温度
                    get("ambientTemperature") //环境温度
                    get("armpitTemperature") //腋窝温度
                    get("bodyTemperature") //体温
                    get("heartRate") //心率
                    get("status") //状态 0自动测试中 1自动测试超时 2手动测试中 3手动测试结束 4手动测试超时
                }
            }

            override fun armBandSPO2(p0: Any?, p1: FBKApiArmBand?) {
                //血氧测量回调
                val hashMap: Map<String, Any> = p0 as Map<String, Any>
                hashMap.apply {
                    get("spo2") //血氧值
                    get("isMoving") //臂带状态 0静止 1移动
                    get("status") //状态 0自动 1自动超时 2手动 3手动超时
                }
            }

            override fun HRVResultData(p0: Any?, p1: FBKApiArmBand?) {
                //HRV测量数据
                val hashMap: Map<String, Any> = p0 as Map<String, Any>
                hashMap.apply {
                    get("heartRate") //心率
                    get("hrv") // HRV
                    get("pnn50")
                    get("rmssd")
                    get("nnvgr")
                    get("sdnn")
                }
            }

            override fun armBandRecord(p0: Any?, p1: FBKApiArmBand?) {
                //历史数据回调
                val hashMap: Map<String, Any> = p0 as Map<String, Any>

            }

            override fun setShockStatus(p0: Boolean, p1: FBKApiArmBand?) {
                //设置震动阈值结果回调
                if (p0) {
                    //成功
                    textView?.text = "Setting Shock Success"
                } else {
                    //失败
                    textView?.text = "Setting Shock fail"
                }
            }

            override fun getShockStatus(p0: Any?, p1: FBKApiArmBand?) {
                //获取震动阈值结果回调
                val hashMap: Map<String, Any> = p0 as Map<String, Any>
                hashMap.apply {
                    get("switchMark") //0开启 1关闭
                    get("shockNumber") //震动阈值
                }
            }

            override fun closeShockStatus(p0: Boolean, p1: FBKApiArmBand?) {
                //关闭震动结果回调
                if (p0) {
                    //成功
                    textView?.text = "关闭震动成功"
                } else {
                    //失败
                    textView?.text = "关闭震动成功"
                }
            }

            override fun setMaxIntervalStatus(p0: Boolean, p1: FBKApiArmBand?) {
                //设置最大心率区间结果回调
            }

            override fun setLightSwitchStatus(p0: Boolean, p1: FBKApiArmBand?) {
                //设置呼吸灯开关结果回调
            }

            override fun deviceMacAddress(p0: Any?, p1: FBKApiArmBand?) {
                //设备MAC地址回调
                val hashMap: Map<String, Any> = p0 as Map<String, Any>
                hashMap.apply {
                    get("macString") //设备MAC地址
                    get("OTAMacString") //设备进入OTA模式的MAC地址
                }
            }

            override fun totalVersion(p0: Any?, p1: FBKApiArmBand?) {
                //设备版本号数据回调
                val hashMap: Map<String, Any> = p0 as Map<String, Any>
                hashMap.apply {
                    get("hardwareVersion") //硬件版本号
                    get("firmwareVersion") //固件版本号
                    get("softwareVersion") //软件版本号
                }
            }
        })
        mApiArm?.registerBleListenerReceiver()
    }

    private val m_apiScanCallBack: FBKApiScanCallBack = object : FBKApiScanCallBack {
        override fun bleScanResult(deviceArray: List<FBKBleDevice>, apiScan: FBKApiScan) {
            println("================================${deviceArray}")
            listInfo = deviceArray
            myAdapter?.notifyDataSetChanged()
//            for (e in deviceArray) {
//                if (e.deviceName.equals("EBEAT-0012763")) {
//                    device = e
//                }
//            }
        }

        override fun bleScanAvailable(isAvailable: Boolean, apiScan: FBKApiScan) {
            println("=============${isAvailable}")
        }
    }

    inner class MyAdapter : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

        var onItemClickListener: OnItemClickListener? = null

        inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val layout: LinearLayout = itemView.findViewById(R.id.layout)
            val tvName: TextView = itemView.findViewById(R.id.tvName)
            val tvMac: TextView = itemView.findViewById(R.id.tvMac)
            val tvRSSI: TextView = itemView.findViewById(R.id.tvRSSI)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_info, parent, false)
            return MyViewHolder(view)
        }

        override fun getItemCount() = listInfo.size

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.apply {
                tvName.text = listInfo[position].deviceName
                tvMac.text = listInfo[position].macAddress
                tvRSSI.text = listInfo[position].deviceRssi.toString()
                layout.setOnClickListener {
                    onItemClickListener?.onItemClickL(listInfo[position])
                }
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClickL(device: FBKBleDevice)
    }
}