package com.github.test.ui.main

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.test.R
import com.github.test.base.CoreActivity
import com.github.test.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@ExperimentalPagingApi
class MainActivity : CoreActivity<ActivityMainBinding, MainViewModel>(), TextWatcher,
    View.OnClickListener, TextView.OnEditorActionListener {

    private var searchJob: Job? = null
    private val adapter = MainAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLayoutRes(R.layout.activity_main)
        initListener()
        setUpAdapter()
        startSearch()
    }

    private fun initListener() {
        binding.etSearch.addTextChangedListener(this)
        binding.etSearch.setOnEditorActionListener(this)
        binding.ivClose.setOnClickListener(this)
    }

    private fun startSearch(keyword: String? = null) {
        searchJob?.cancel()
        if (keyword.isNullOrEmpty()) {
            return
        }
        binding.progress.isVisible = true
        searchJob = lifecycleScope.launch {
            viewModel.searchPlayers(keyword)
                .collectLatest {
                    adapter.submitData(it)
                }
        }
    }


    private fun setUpAdapter() {
        binding.rvItem.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            setHasFixedSize(true)
        }
        binding.rvItem.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )
        adapter.addLoadStateListener { loadState ->
            if (loadState.mediator?.refresh is LoadState.Loading) {
                binding.tvError.isVisible = false
            } else {
                binding.progress.isVisible = false
                val error = when {
                    loadState.mediator?.prepend is LoadState.Error -> loadState.mediator?.prepend as LoadState.Error
                    loadState.mediator?.append is LoadState.Error -> loadState.mediator?.append as LoadState.Error
                    loadState.mediator?.refresh is LoadState.Error -> loadState.mediator?.refresh as LoadState.Error
                    else -> null
                }
                error?.let {
                    if (adapter.snapshot().isEmpty()) {
                        binding.tvError.isVisible = true
                        binding.tvError.text = it.error.localizedMessage
                    }
                }

            }
        }

    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        binding.ivClose.isVisible = s.toString().length >= 0
    }

    override fun afterTextChanged(s: Editable?) {

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivClose -> {
                binding.etSearch.setText("")
                binding.ivClose.isVisible = false
                searchJob?.cancel()
                searchJob = lifecycleScope.launch {
                    adapter.submitData(PagingData.empty())
                }
            }
        }
    }


    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            val keyword = binding.etSearch.text?.trim().toString()
            if (keyword.isEmpty()) {
                Toast.makeText(this, "kata kunci tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return false
            }
            startSearch(keyword)
            hideKeyboardFrom(v)
            return true
        }
        return false
    }

    private fun hideKeyboardFrom(view: View?) {
        val imm: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

}
