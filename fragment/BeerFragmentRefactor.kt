package com.nereus.craftbeer.fragment

import android.os.Handler
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nereus.craftbeer.BuildConfig
import com.nereus.craftbeer.R
import com.nereus.craftbeer.adapter.BeerViewAdapter
import com.nereus.craftbeer.databinding.ActivityBeerFragmentRefactorBinding
import com.nereus.craftbeer.enums.ErrorLogCode
import com.nereus.craftbeer.enums.MessageLogCode
import com.nereus.craftbeer.exception.MessageException
import com.nereus.craftbeer.model.MessagesModel
import com.nereus.craftbeer.util.displayToast
import com.nereus.craftbeer.util.getEmptyViewVisibility
import com.nereus.craftbeer.util.getRecyclerViewVisibility
import com.nereus.craftbeer.viewmodel.BeerShopViewModel
import timber.log.Timber


/**
 * Beer fragment refactor
 *
 * @constructor Create empty Beer fragment refactor
 */
class BeerFragmentRefactor :
    BaseFragment<ActivityBeerFragmentRefactorBinding, BeerShopViewModel>() {

    /**
     * View model
     */
    override val viewModel: BeerShopViewModel by activityViewModels()

    override fun getLayout(): Int {
        return R.layout.activity_beer_fragment_refactor
    }

    override fun afterBinding() {
        configRecyclerView()
        viewModel.loadBeers()
        // Auto update beer data after a certain time
        val waitTime = BuildConfig.WAIT_TIME
        val handler = Handler()
        val delay: Long = waitTime.toLong() * 1000 // second unit
        handler.postDelayed(object : Runnable {
            override fun run() {
                viewModel.loadBeers()
                handler.postDelayed(this, delay)
            }
        }, delay)
    }

    override fun onResume() {
        super.onResume()
        configRecyclerView()
        viewModel.loadBeers()
    }

    override fun setViewModelListener() {
        this.viewModel.beers.observe(binding.lifecycleOwner!!, Observer { beers ->
            if (viewModel.beers.value.isNullOrEmpty()) {
                this.findNavController()
                    .navigate(R.id.selectModeActivity)
                displayToast(context, getString(R.string.msg_all_beers_under_maintenance))
            } else {
                Timber.i("============= beers")
                Timber.i(beers.size.toString())
                binding.recyclerViewBeerListRefactor.visibility = beers.getRecyclerViewVisibility()
                binding.emptyView.visibility = beers.getEmptyViewVisibility()

                (binding.recyclerViewBeerListRefactor.adapter as BeerViewAdapter).submitList(beers)
            }
        })
    }


    private fun configRecyclerView() {
        binding.recyclerViewBeerListRefactor.adapter =
            BeerViewAdapter(this.requireContext(), viewModel)
        binding.recyclerViewBeerListRefactor.layoutManager =
            LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
    }
}