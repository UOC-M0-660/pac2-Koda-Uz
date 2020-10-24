package edu.uoc.pac2

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.room.Room
import com.google.android.gms.ads.MobileAds
import edu.uoc.pac2.data.*

/**
 * Entry point for the Application.
 */
class MyApplication : Application() {

    private lateinit var booksInteractor: BooksInteractor

    override fun onCreate() {
        super.onCreate()
        // Init Room Database
        // Uses allowMainThreadQueries
        val db = Room.databaseBuilder(this, ApplicationDatabase::class.java,
                "basedatos-app").allowMainThreadQueries().build()

        // BooksInteractor
        booksInteractor = BooksInteractor(db.bookDao())

        //MobileAds
        MobileAds.initialize(this)
    }

    fun getBooksInteractor(): BooksInteractor {
        return booksInteractor
    }

    fun hasInternetConnection(): Boolean {
        val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting == true
    }
}