package edu.uoc.pac2.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.picasso.Picasso
import edu.uoc.pac2.MyApplication
import edu.uoc.pac2.R
import edu.uoc.pac2.data.Book
import kotlinx.android.synthetic.main.activity_book_detail.*
import kotlinx.android.synthetic.main.fragment_book_detail.*

/**
 * A fragment representing a single Book detail screen.
 * This fragment is contained in a [BookDetailActivity].
 */
class BookDetailFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_book_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Get Book for this detail screen
        loadBook()
    }


    // Get Book for the given {@param ARG_ITEM_ID} Book id
    private fun loadBook() {
        val myApp = activity?.application as MyApplication
        val booksInteractor = myApp.getBooksInteractor()
        arguments?.let {
            if (it.containsKey(ARG_ITEM_ID)) {
                val book = booksInteractor.getBookById(it.getInt(ARG_ITEM_ID))
                initUI(book)
            }
        }
    }

    // Init UI with book details
    private fun initUI(book: Book?) {
        book?.let {
            var toolbar = activity?.findViewById<CollapsingToolbarLayout>(R.id.collapsing_toolbar_layout)
            var toolbarImage = activity?.findViewById<ImageView>(R.id.toolbar_image)
            toolbar?.title = book.title
            author.text = book.author
            date.text = book.publicationDate
            description.text = book.description
            // Loads image with Picasso module
            Picasso.get().load(book.urlImage).into(toolbarImage)

            // Setup share button
            val shareButton = activity?.findViewById<FloatingActionButton>(R.id.fab)
            shareButton?.setOnClickListener {
                shareContent(book)
            }
        }
    }

    // Share Book Title and Image URL
    private fun shareContent(book: Book) {
        // Implicit intent
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "Check out this awesome book: ${book.title} ${book.urlImage}")
            type = "text/plain"
        }

        // Crates chooser menu for user to select an app to share
        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)

    }

    companion object {
        /**
         * The fragment argument representing the item title that this fragment
         * represents.
         */
        const val ARG_ITEM_ID = "itemIdKey"

        fun newInstance(itemId: Int): BookDetailFragment {
            val fragment = BookDetailFragment()
            val arguments = Bundle()
            arguments.putInt(ARG_ITEM_ID, itemId)
            fragment.arguments = arguments
            return fragment
        }
    }
}