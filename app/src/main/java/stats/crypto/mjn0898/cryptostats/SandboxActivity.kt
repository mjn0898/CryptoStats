package stats.crypto.mjn0898.cryptostats

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import kotlinx.android.synthetic.main.activity_sandbox.*
import org.jetbrains.anko.UI
import org.jetbrains.anko.custom.async
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.doAsyncResult
import stats.crypto.mjn0898.cryptostats.R.id.async
import java.time.LocalDateTime
import java.util.concurrent.CountDownLatch

class SandboxActivity : AppCompatActivity() {
    private var listExchanges = ArrayList<Exchange>()
    private var database : ExchangeDatabase? = null
    val COLUMNS = 4
    val tableLayout by lazy { TableLayout(this) }
    val context : Context = this

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sandbox)
        val doing = doAsync {
            database = ExchangeDatabase.getInstance(context = this@SandboxActivity)
            database?.ExchangeDataDao()?.deleteAll()
            var e :Exchange = Exchange()
            e.amtFrom = 100.0
            e.currencyFrom = "USD"
            e.currencyTo = "BTC"
            e.date = LocalDateTime.now().toString()
            insertExchangeData(e)
            val data = doAsyncResult { fetchExchangeData() }
            Log.d("unit", data.toString())

            val lp = TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            val apply = tableLayout.apply {
                layoutParams = lp
                isShrinkAllColumns = true
            }
            val retData = data.get()
            if(retData != null) {
                val Rows = retData.size//database?.ExchangeDataDao()?.getAll()?.size//data.size
                Log.d("rows", Rows.toString())
                for (i in 0 until Rows) {
                 listExchanges.add(retData.get(i))
                 Log.d("rows", retData.get(i).currencyFrom)

                }
            }



        }
        doing.get()
        var listAdapter = ExchangeAdapter(context, listExchanges)
        exchange_list.adapter = listAdapter
        Log.d("adapter", "adapter added")


    }

    inner class ExchangeAdapter : BaseAdapter {

        private var exchangeList = ArrayList<Exchange>()
        private var context: Context? = null

        constructor(context: Context, notesList: ArrayList<Exchange>) : super() {
            this.exchangeList = notesList
            this.context = context
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {

            val view: View?
            val vh: ViewHolder

            if (convertView == null) {
                view = layoutInflater.inflate(R.layout.list, parent, false)
                vh = ViewHolder(view)
                view.tag = vh
                Log.i("JSA", "set Tag for ViewHolder, position: " + position)
            } else {
                view = convertView
                vh = view.tag as ViewHolder
            }

            vh.date.text = exchangeList[position].date
            vh.currFrom.text = exchangeList[position].currencyFrom
            vh.currTo.text = exchangeList[position].currencyTo
            vh.amtFrom.text = exchangeList[position].amtFrom.toString()
            vh.amtTo.text = "TBD"


            return view
        }

        override fun getItem(position: Int): Any {
            return exchangeList[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return exchangeList.size
        }
    }

    private class ViewHolder(view: View?) {
        val date: TextView
        val currFrom: TextView
        val currTo: TextView
        val amtFrom: TextView
        val amtTo: TextView

        init {
            this.date = view?.findViewById(R.id.date) as TextView
            this.currFrom = view?.findViewById(R.id.currFrom) as TextView
            this.currTo = view?.findViewById(R.id.currTo) as TextView
            this.amtFrom = view?.findViewById(R.id.amtFrom) as TextView
            this.amtTo = view?.findViewById(R.id.amtTo) as TextView
        }
    }





    private fun fetchExchangeData() : List<Exchange> {
        lateinit var retrievedData : List<Exchange>


        //doAsync {
            retrievedData = database?.ExchangeDataDao()?.getAll()!!
            val any = if (retrievedData == null || retrievedData.size == 0) {
                //Empty
            } else {
                // do something
                for(i in 0 until retrievedData.size-1) {
                    Log.d("fetch", i.toString())
                    Log.d("fetch", retrievedData[i].currencyFrom.toString())
                    Log.d("fetch", retrievedData[i].currencyTo.toString())
                    Log.d("fetch", retrievedData[i].amtFrom.toString())
                    Log.d("fetch", retrievedData[i].date.toString())
                }
            }
        //}
        return retrievedData
    }

    private fun insertExchangeData(exchangeData: Exchange) {
        doAsync {
            database?.ExchangeDataDao()?.insert(exchangeData)
        }
    }

}
