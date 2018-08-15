package stats.crypto.mjn0898.cryptostats

import android.annotation.SuppressLint
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.time.LocalDateTime

@Entity
data class Exchange(@PrimaryKey(autoGenerate = true) var id: Long?,
                    var currencyFrom: String,
                    var currencyTo: String,
                    var amtFrom: Double,
                    var date: String) {
    @SuppressLint("NewApi")
    constructor():this(null,"","",0.0, "" )
}