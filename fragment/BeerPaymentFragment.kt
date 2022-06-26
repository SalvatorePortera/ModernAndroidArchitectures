package com.nereus.craftbeer.fragment

import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.nereus.craftbeer.R
import com.nereus.craftbeer.databinding.ActivityBeerPaymentFragmentBinding
import com.nereus.craftbeer.exception.MessageException
import com.nereus.craftbeer.model.MessagesModel
import com.nereus.craftbeer.util.livedata.EventObserver
import com.nereus.craftbeer.util.loadImage
import com.nereus.craftbeer.util.setOnClickDebounce
import com.nereus.craftbeer.viewmodel.BeerShopViewModel
import timber.log.Timber
import kotlin.math.roundToInt


class BeerPaymentFragment : BaseFragment<ActivityBeerPaymentFragmentBinding, BeerShopViewModel>() {


    override val viewModel: BeerShopViewModel by activityViewModels()

    override fun getLayout(): Int {
        return R.layout.activity_beer_payment_fragment
    }

    override fun afterBinding() {
        binding.viewModel = viewModel
        viewModel.selected_beer.value?.let {
            binding.imgPaymentFlag.loadImage(it.flagUrl)
            binding.imgPaymentBeerIcon.loadImage(it.imageUrl)
        }

        if(binding.txtPaymentRegion.text.isNullOrBlank()){
            binding.txtPaymentRegion.visibility = View.GONE
        } else {
            binding.txtPaymentRegion.visibility = View.VISIBLE
        }

        setupBeerPercentage()
        viewModel.restartHandler()
    }

    override fun setViewListener() {
        binding.paymentClose.setOnClickDebounce() {
            viewModel.stopHandler()
            findNavController().navigate(R.id.beerShopFragment)
        }
    }

    override fun setViewModelListener() {
        viewModel.payment.observe(viewLifecycleOwner, EventObserver {
            this.viewModel.checkBalance()
        })

        viewModel.balance.observe(viewLifecycleOwner, EventObserver {
            if (it <= viewModel.selected_beer.value!!.sellingPrice.roundToInt()) {
                viewModel.setException(MessageException(MessagesModel(R.string.msg_out_of_money)))
                Timber.i("==== card unlocked viewModel.balance.observe")
                viewModel.unlockCard()
            } else {
                this.viewModel.connectSocket()
                findNavController().navigate(R.id.beerPouringFragment)
            }
        })
    }

    private fun setupBeerPercentage() {
        val beerPercentage = viewModel.beerPercentage.value
        if (beerPercentage != null) {
            when (beerPercentage) {
                0 -> {
                    binding.imgPaymentBeerPercentage.setImageResource(R.drawable.b0)
                }
                in 1..10 -> {
                    binding.imgPaymentBeerPercentage.setImageResource(R.drawable.b10)
                }
                in 11..20 -> {
                    binding.imgPaymentBeerPercentage.setImageResource(R.drawable.b20)
                }
                in 21..30 -> {
                    binding.imgPaymentBeerPercentage.setImageResource(R.drawable.b30)
                }
                in 31..40 -> {
                    binding.imgPaymentBeerPercentage.setImageResource(R.drawable.b40)
                }
                in 41..50 -> {
                    binding.imgPaymentBeerPercentage.setImageResource(R.drawable.b50)
                }
                in 51..60 -> {
                    binding.imgPaymentBeerPercentage.setImageResource(R.drawable.b60)
                }
                in 61..70 -> {
                    binding.imgPaymentBeerPercentage.setImageResource(R.drawable.b70)
                }
                in 71..80 -> {
                    binding.imgPaymentBeerPercentage.setImageResource(R.drawable.b80)
                }
                in 81..90 -> {
                    binding.imgPaymentBeerPercentage.setImageResource(R.drawable.b90)
                }
                else -> {
                    binding.imgPaymentBeerPercentage.setImageResource(R.drawable.b100)
                }
            }
        }
    }
}
