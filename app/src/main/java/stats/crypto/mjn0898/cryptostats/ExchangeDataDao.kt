package stats.crypto.mjn0898.cryptostats

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query

@Dao
interface ExchangeDataDao {

    @Query("SELECT * from Exchange")
    fun getAll(): List<Exchange>

    @Insert(onConflict = REPLACE)
    fun insert(exchange: Exchange)

    @Query("DELETE from Exchange")
    fun deleteAll()
}