package com.example.news.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.news.R
import com.example.news.models.Article

class SearchNewsFragment : Fragment(R.layout.fragment_search){

    private val TAG = "SearchNewsFragment"
    private lateinit var searchEditText: EditText
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    private fun initUi(){
        searchEditText = requireView().findViewById(R.id.searchEdit)
        recyclerView = requireView().findViewById(R.id.recyclerView)

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // do nothing
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //do nothing
            }

            override fun afterTextChanged(p0: Editable?) {
                if (p0.toString().length >= 3){
                    // call search function
                }
            }

        })
    }

    private fun searchNews(query: String){
        // call search function
    }

    fun submitListToAdapter(articles: List<Article>){
        // submit list to adapter
    }

    fun showProgressBar(){
        // show progress bar
    }

    fun hideProgressBar(){
        // hide progress bar
    }

    fun showNoNetworkDialog(){
        // show no network dialog
    }

    fun showInternalErrorDialog(){
        // show internal error dialog
    }

    fun isNetworkAvailable(): Boolean{
        // check network availability
        return true
    }



}