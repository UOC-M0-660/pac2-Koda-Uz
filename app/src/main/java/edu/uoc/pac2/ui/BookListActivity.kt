package edu.uoc.pac2.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
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
        adView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest )
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

    // Load Books from Room
    private fun loadBooksFromLocalDb() {
        val bookInteractor = myApp.getBooksInteractor()
        val books: List<Book> = bookInteractor.getAllBooks()
        adapter.setBooks(books)
    }

    // Save Books to Local Storage
    private fun saveBooksToLocalDatabase(books: List<Book>) {
        val bookInteractor = myApp.getBooksInteractor()
        bookInteractor.saveBooks(books)
        adapter.setBooks(books)
    }
}