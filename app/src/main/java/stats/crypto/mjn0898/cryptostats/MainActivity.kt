package stats.crypto.mjn0898.cryptostats

import android.annotation.TargetApi
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.*
import com.google.common.hash.Hashing
import khttp.get
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.nio.charset.StandardCharsets
import java.security.Timestamp
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import org.json.JSONArray


class MainActivity : AppCompatActivity() {
    //var rgroup : RadioGroup =
    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                message.setText(R.string.bot_button_1)
                //makeCall("/accounts")
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                message.setText(R.string.bot_button_2)
                launchSandboxActivity()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                message.setText(R.string.bot_button_3)
                launchSandboxOrderActivity()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        var timeLength : String = "3"
        var currencySelected : String = "BTC"
        var dropdown : Spinner = findViewById(R.id.spinner) as Spinner
        val items : Array<String> = arrayOf("BTC", "ETH", "LTC", "XRP", "BCH");
        var adapter : ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter)

        dropdown?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                //okkk
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                currencySelected = parent?.getItemAtPosition(position).toString()
                Log.d("spinnersel", currencySelected)
                makeMainGraph("histoday?fsym="+currencySelected+"&tsym=USD&limit="+timeLength)
            }

        }

        val rgroup = findViewById(R.id.radioGroup) as RadioGroup
        rgroup.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { radioGroup, i ->
            val checkedRadioButton = findViewById<RadioButton>(i)
            Log.i("rgroup", checkedRadioButton.text.toString())
            when(checkedRadioButton.text.toString()) {
                "3 Days" -> timeLength="3"
                "Week" -> timeLength="7"
                "Month" -> timeLength="30"
            }
            makeMainGraph("histoday?fsym="+currencySelected+"&tsym=USD&limit="+timeLength)
            //makeMainGraph("histoday?fsym=BTC&tsym=USD&limit=3")
        })

        makeMainGraph("histoday?fsym=BTC&tsym=USD&limit=3")


    }

    fun currencyButtonChange(view: View) {
        //val currency = R.
    }
/*
    fun timeButtonChange(view: View) {
        val rgroup : RadioGroup = findViewById(R.id.radioGroup) as RadioGroup
        when(rgroup.getCheckedRadioButtonId()) {

        }
        val temp : String =  "histoday?fsym=BTC&tsym=USD&limit=3"
    }
*/
    @TargetApi(26)
    private fun makeMainGraph(str: String) {
        var highData : MutableList<Double> =  arrayListOf()
        var highDataSeries : MutableList<DataPoint> =  arrayListOf()
    doAsync {
            val base = "https://min-api.cryptocompare.com/data/"
            val api: String = base + str

            val response = khttp.get((api))
            val obj = response.jsonObject
            val dataArray : JSONArray = obj["Data"] as JSONArray
            //val dayOne : JSONObject = dataArray[0] as JSONObject
            //val dayTwo : JSONObject = dataArray[1] as JSONObject
            //val dayThree : JSONObject = dataArray[2] as JSONObject
            //highData = mutableListOf(dayOne.getDouble("high"),dayTwo.getDouble("high"),dayThree.getDouble("high"))

            for(i in 0 until dataArray.length()) {
                highData.add((dataArray[i] as JSONObject).getDouble("high"))
            }
            Log.i("price", response.text)
            Log.i("check", dataArray.toString())
            Log.i("checknow", highData.toString())

            for(i in 0 until highData.size) {
                highDataSeries.add(DataPoint((i).toDouble(), highData.get(i)))
            }
            Log.i("checknow", highDataSeries.toString())
            uiThread {
                val graph = findViewById(R.id.main_graph) as GraphView
                graph.removeAllSeries()
                graph.getViewport().setYAxisBoundsManual(true);
                graph.getViewport().setMinY(highData.min()!!.minus(100));
                graph.getViewport().setMaxY(highData.max()!!.plus(100));
                graph.getViewport().setXAxisBoundsManual(true);
                graph.getViewport().setMinX(1.0);
                graph.getViewport().setMaxX((highDataSeries.size-1).toDouble());
                graph.getViewport().setScrollableY(true) // enables vertical scrolling
                graph.getViewport().setScalableY(true)
                Log.i("checknum", highData.toString())
                //val series = LineGraphSeries(arrayOf(DataPoint(0.0, highData[0]), DataPoint(1.0, highData[1]), DataPoint(2.0, highData[2])))
                val series = LineGraphSeries(highDataSeries.toTypedArray())
                graph.addSeries(series);
            }
        }
    }

    private fun launchSandboxActivity() {
        Log.d("tag", "Sandbox Activity!")
        val intent = Intent(this, SandboxActivity::class.java)
        //val message = mMessageEditText!!.getText().toString()
        //intent.putExtra(EXTRA_MESSAGE, message)
        startActivity(intent)

    }

    private fun launchSandboxOrderActivity() {
        Log.d("tag", "Sandbox Order Activity!")
        val intent = Intent(this, SandboxOrderActivity::class.java)
        //val message = mMessageEditText!!.getText().toString()
        //intent.putExtra(EXTRA_MESSAGE, message)
        startActivity(intent)

    }
}
