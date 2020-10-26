package edu.uoc.pac2

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.room.Room
import edu.uoc.pac2.data.*

/**
 * Álvaro Pérez Gómez
 */

/**
 * Entry point for the Application.
 */
class MyApplication : Application() {

    private lateinit var booksInteractor: BooksInteractor

    override fun onCreate() {
        super.onCreate()
        // Init Room Database
        // Uses allowMainThreadQueries
        // This is not ideal for large scale DB because it can halt the main thread
        // For this app works with no issues
        val db = Room.databaseBuilder(this, ApplicationDatabase::class.java,
                "basedatos-app").allowMainThreadQueries().build()

        // BooksInteractor
        booksInteractor = BooksInteractor(db.bookDao())
    }

    /**
     * Returns Book Interactor
     */
    fun getBooksInteractor(): BooksInteractor {
        return booksInteractor
    }

    /**
     * Checks for internet connection
     */
    fun hasInternetConnection(): Boolean {
        val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting == true
    }
}