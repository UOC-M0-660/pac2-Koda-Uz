package edu.uoc.pac2.ui

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.firebase.firestore.FirebaseFirestore
import edu.uoc.pac2.MyApplication
import edu.uoc.pac2.R
import edu.uoc.pac2.data.Book

/**
 * An activity representing a list of Books.
 */
class BookListActivity : AppCompatActivity() {

    private val TAG = "BookListActivity"

    private lateinit var adapter: BooksListAdapter
    private lateinit var myApp: MyApplication
    private lateinit var adView: AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_list)

        // Init UI
        initToolbar()
        initRecyclerView()

        // Init myApp
        myApp = this.application as MyApplication

        // Get Books
        getBooks()

        // Add books data to Firestore [Use once for new projects with empty Firestore Database]
        // FirestoreBookData.addBooksDataToFirestoreDatabase()

        // Setup AdView
        MobileAds.initialize(this)
        adView = findViewById(R.id.adView)
        // Configures emulator to receive test ads
        // Calls to addTestDevice should be removed before publishing
        val adRequest = AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build()
        adView.loadAd(adRequest)
    }

    // Init Top Toolbar
    private fun initToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.title = title
    }

    // Init RecyclerView
    private fun initRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.book_list)
        // Set Layout Manager
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        // Init Adapter
        adapter = BooksListAdapter(emptyList())
        recyclerView.adapter = adapter
    }

    // Get Books and Update UI
    private fun getBooks() {
        // Reads books from local DB
        loadBooksFromLocalDb()

        // Checks for internet connection
        if (myApp.hasInternetConnection()) {
            val db = FirebaseFirestore.getInstance()

            val docRef = db.collection("books")
            docRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }
                // If snapshot is received, stores book data in local DB and updates UI
                if (snapshot != null) {
                    val books: List<Book> = snapshot.documents.mapNotNull { it.toObject(Book::class.java) }
                    saveBooksToLocalDatabase(books)
                }
            }
        }
    }

    // Load Books from Room local DB with Async task in background
    private fun loadBooksFromLocalDb() {
        var books: List<Book> = ArrayList()
        // AsycncTask
        class LoadBooks: AsyncTask<Void, Void, Void>() {
            // Background query to Room DB
            override fun doInBackground(vararg params: Void?): Void? {
                val bookInteractor = myApp.getBooksInteractor()
                books = bookInteractor.getAllBooks()
                return null
            }

            // Task callback, will be executed on main thread
            override fun onPostExecute(result: Void?) {
                super.onPostExecute(result)
                adapter.setBooks(books)
            }
        }
        LoadBooks().execute()
    }

    // Save Books to Room Local Storage with Async task in background
    private fun saveBooksToLocalDatabase(books: List<Book>) {
        // AsycncTasks
        class SaveBooks: AsyncTask<Void, Void, Void>() {
            // Background query to Room DB
            override fun doInBackground(vararg params: Void?): Void? {
                val bookInteractor = myApp.getBooksInteractor()
                bookInteractor.saveBooks(books)
                return null
            }

            // Task callback, will be executed on main thread
            override fun onPostExecute(result: Void?) {
                super.onPostExecute(result)
                adapter.setBooks(books)
            }
        }
        SaveBooks().execute()
    }
}