package stats.crypto.mjn0898.cryptostats

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context

@Database(entities = arrayOf(Exchange::class), version = 1)
abstract class ExchangeDatabase : RoomDatabase() {
    abstract fun ExchangeDataDao(): ExchangeDataDao

    companion object {
        private var INSTANCE: ExchangeDatabase? = null

        fun getInstance(context: Context): ExchangeDatabase? {
            if (INSTANCE == null) {
                synchronized(ExchangeDatabase::class) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            ExchangeDatabase::class.java, "exchange.db")
                            .build()
                }
            }
            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}