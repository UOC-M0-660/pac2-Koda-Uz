package edu.uoc.pac2.ui

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.uoc.pac2.R
import edu.uoc.pac2.data.Book

/**
 * Adapter for a list of Books.
 */

class BooksListAdapter(private var books: List<Book>) : RecyclerView.Adapter<BooksListAdapter.ViewHolder>() {

    private val evenViewType = 0
    private val oddViewType = 1

    // OnClickListener for RecyclerView rows
    private val onClickListener: View.OnClickListener

    init {
        onClickListener = View.OnClickListener { v ->
            val book = v.tag as Book

            // Creates intent with book uid as an extra
            val intent = Intent(v.context, BookDetailActivity::class.java).apply {
                putExtra(BookDetailFragment.ARG_ITEM_ID, book.uid)
            }
            // Starts new activity with translation animation from bottom to top
            v.context.startActivity(intent)
            (v.context as Activity).overridePendingTransition(R.anim.translate_in_bottom, R.anim.activity_stay);
        }
    }

    /**
     * Returns book at selected position
     */
    private fun getBook(position: Int): Book {
        return books[position]
    }

    /**
     * Changes array of books and refreshes UI
     */
    fun setBooks(books: List<Book>) {
        this.books = books
        // Reloads the RecyclerView with new adapter data
        notifyDataSetChanged()
    }

    /**
     * Returns view type ever or odd
     */
    override fun getItemViewType(position: Int): Int {
        return if (position % 2 == 0) {
            evenViewType
        } else {
            oddViewType
        }
    }

    // Creates View Holder for re-use
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = when (viewType) {
            evenViewType -> {
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.row_book_list_content_even, parent, false)
            }
            oddViewType -> {
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.row_book_list_content_odd, parent, false)
            }
            else -> {
                throw IllegalStateException("Unsupported viewType $viewType")
            }
        }
        return ViewHolder(view)
    }

    // Binds re-usable View for a given position
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val book = getBook(position)
        holder.titleView.text = book.title
        holder.authorView.text = book.author

        // Set View Click Listener
        with(holder.itemView) {
            tag = book
            setOnClickListener(onClickListener)
        }
    }

    // Returns total items in Adapter
    override fun getItemCount(): Int {
        return books.size
    }

    // Holds an instance to the view for re-use
    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val titleView: TextView = view.findViewById(R.id.title)
        val authorView: TextView = view.findViewById(R.id.author)
    }

}
