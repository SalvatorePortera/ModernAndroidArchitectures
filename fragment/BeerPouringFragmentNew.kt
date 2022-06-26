package com.nereus.craftbeer.fragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.nereus.craftbeer.R
import com.nereus.craftbeer.databinding.ActivityBeerPouringFragmentBinding
import com.nereus.craftbeer.exception.MessageException
import com.nereus.craftbeer.model.MessagesModel
import com.nereus.craftbeer.util.displayToast
import com.nereus.craftbeer.util.livedata.EventObserver
import com.nereus.craftbeer.util.loadImage
import com.nereus.craftbeer.util.setOnClickDebounce
import com.nereus.craftbeer.viewmodel.BeerShopViewModel
import com.robinhood.ticker.TickerUtils
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import kotlin.math.roundToInt


/**
 * Beer pouring fragment new
 *
 * @constructor Create empty Beer pouring fragment new
 */
@AndroidEntryPoint
class BeerPouringFragmentNew :
    BaseFragment<ActivityBeerPouringFragmentBinding, BeerShopViewModel>() {

    override val viewModel: BeerShopViewModel by activityViewModels()

    /**
     * Get layout
     *
     * @return
     */
    override fun getLayout(): Int {
        return R.layout.activity_beer_pouring_fragment
    }

    /**
     * After binding
     *
     */
    override fun afterBinding() {
        Timber.i("Pouring after binding")
        binding.viewModel = viewModel
        binding.txtStatus.setTextColor(Color.BLACK)
        setupTickViews()
        viewModel.selected_beer.value?.let {
            binding.imgPouringFlag.loadImage(it.flagUrl)
            binding.imgPouringBeerIcon.loadImage(it.imageUrl)
        }

        viewModel.setupPouringBeer()
        setupBeerPercentage()
        setBeerPos()

        if (binding.txtPouringRegion.text.isNullOrBlank()) {
            binding.txtPouringRegion.visibility = View.GONE
        } else {
            binding.txtPouringRegion.visibility = View.VISIBLE
        }

        if (viewModel.selected_beer.value?.colour.equals("sour") || (viewModel.selected_beer.value?.colour.equals(
                "highball"
            ))
        ) {
            Timber.i("Pouring the drink")
            binding.txtStatus.setText(R.string.pour_drink)
        } else {
            Timber.i("Pouring the beer")
            binding.txtStatus.setText(R.string.pour_beer)
        }
    }

    /**
     * On destroy view
     *
     */
    override fun onDestroyView() {
        super.onDestroyView()
        Timber.i("==== card unlocked onDestroyView")
        viewModel.unlockCard()
    }

    /**
     * Set view listener
     *
     */
    override fun setViewListener() {
        binding.infoClose.setOnClickDebounce {
            viewModel.cleanCardData()
            viewModel.closeSocket()
            findNavController().navigate(R.id.beerShopFragment)
        }
    }

    /**
     * Set view model listener
     *
     */
    override fun setViewModelListener() {
        this.viewModel.beerShopFlowHandler.observe(this, EventObserver {
            Timber.i("Pouring isPouringFinished: %s, isExecuted: %s, true & false is finish flag", it.isPouringFinished.toString(), it.isExecuted.toString())
            if (it.isPouringFinished && !it.isExecuted) {
                binding.txtStatus.text = "FINISH"
                binding.txtStatus.setTextColor(Color.RED)
                binding.tickerViewBalance.textColor = Color.RED
                binding.tickerViewTotal.textColor = Color.RED
                this.viewModel.pay()
            }
            if (it.isExecuted) {
                if (it.paymentResult.isSuccess) {
                    displayToast(requireContext(), getString(R.string.payment_success))
                    Handler().postDelayed({
                        viewModel.handlePostPayment()
                        findNavController().navigate(R.id.beerShopFragment)
                    }, 3000L)
                } else {
                    viewModel.setException(MessageException(MessagesModel(R.string.msg_payment_failed)))
                }
            }

        })

        this.viewModel.lockHandler.observe(this, EventObserver {
            if (it.isLock && viewModel.beerPouring.value?.amountInMl == 0) {
                viewModel.cleanCardData()
                viewModel.closeSocket()
                it.isLock = false
                findNavController().navigate(R.id.beerShopFragment)
            }
        })

    }

    /**
     * Setup tick views
     *
     */
    private fun setupTickViews() {
        val digitalFont = Typeface.createFromAsset(requireActivity().assets, "fonts/digital.ttf")
        listOf(binding.tickerViewBalance, binding.tickerViewTotal, binding.tickerViewMl).forEach {
            it.apply {
                typeface = digitalFont
                setCharacterLists(TickerUtils.provideNumberList())
            }
        }
    }

    /**
     * Setup beer percentage
     *
     */
    private fun setupBeerPercentage() {
        val beerPercentage = viewModel.beerPercentage.value
        if (beerPercentage != null) {
            when (beerPercentage) {
                0 -> {
                    binding.imgPouringBeerRemain.setImageResource(R.drawable.b0)
                }
                in 1..10 -> {
                    binding.imgPouringBeerRemain.setImageResource(R.drawable.b10)
                }
                in 11..20 -> {
                    binding.imgPouringBeerRemain.setImageResource(R.drawable.b20)
                }
                in 21..30 -> {
                    binding.imgPouringBeerRemain.setImageResource(R.drawable.b30)
                }
                in 31..40 -> {
                    binding.imgPouringBeerRemain.setImageResource(R.drawable.b40)
                }
                in 41..50 -> {
                    binding.imgPouringBeerRemain.setImageResource(R.drawable.b50)
                }
                in 51..60 -> {
                    binding.imgPouringBeerRemain.setImageResource(R.drawable.b60)
                }
                in 61..70 -> {
                    binding.imgPouringBeerRemain.setImageResource(R.drawable.b70)
                }
                in 71..80 -> {
                    binding.imgPouringBeerRemain.setImageResource(R.drawable.b80)
                }
                in 81..90 -> {
                    binding.imgPouringBeerRemain.setImageResource(R.drawable.b90)
                }
                else -> {
                    binding.imgPouringBeerRemain.setImageResource(R.drawable.b100)
                }
            }
        }
    }


    /**
     * Set beer pos
     *
     */
    private fun setBeerPos() {
        var posVal = viewModel.beerPos.value
        var beersSize = viewModel.beersNoFilter.value!!.size
        if (posVal == 1 && beersSize == 1) {
            binding.txtPos.text = ""
        } else if (posVal == 1 && beersSize > 1) {
            binding.txtPos.text = getText(R.string.left)
        } else if (posVal == 2 && beersSize == 2) {
            binding.txtPos.text = getText(R.string.right)
        } else if (posVal == 2 && beersSize == 3) {
            binding.txtPos.text = getText(R.string.center)
        } else if (posVal == 3 && beersSize == 3) {
            binding.txtPos.text = getText(R.string.right)
        }
    }


    /**
     * TODO Setup Mock pouring beer process not using for deploy
     */
    var balance = 5000
    var ml = 0
    var isDone = false
    var isExecuted = false

    /**
     * Setup pouring listener
     *
     * @param price
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun setupPouringListener(price: Double) {
        Timber.i("Pouring setupPouringListener")
        val mainHandler = Handler(Looper.getMainLooper())
        val mHandler = Handler()
        val updateTextTask = object : Runnable {
            override fun run() {
                var amount = Math.random() * 200
                mainHandler.postDelayed(this, 2000)

                displayToast(
                    activity?.applicationContext,
                    "Pouring ${amount.roundToInt()} ml"
                )
                if (((ml + amount) * price).roundToInt() > balance) {
                    Timber.i("Pouring NG amount: %d, calc price: %d, balance: %d", amount, ((ml + amount) * price).roundToInt(), balance)
                    isDone = true
                    displayToast(
                        activity?.applicationContext,
                        "Deposit more money to buy more beer !"
                    )
                } else {
                    Timber.i("Pouring OK amount: %d, calc price: %d, balance: %d", amount, ((ml + amount) * price).roundToInt(), balance)
                    isDone = false
                    amount += viewModel.addCorrectionAmount()
                    repeat(amount.roundToInt()) {
                        val start = System.currentTimeMillis()
                        mainHandler.postDelayed({
                            viewModel.pourBeer(1)
                            val end = System.currentTimeMillis()
                            Timber.i("benchmarl %d", end - start)
                        }, 500)
//

                    }
//                    mainHandler.postDelayed(this, 200)
                }
            }
        }

        /**
         * Payment action
         */
        val paymentAction = Runnable {
            Timber.i("Pouring isDone: %s, isExecuted: %s, true & false is finish flag", isDone.toString(), isExecuted.toString())
            if (isDone && !isExecuted) {
                isExecuted = true
//                gifPouring.setImageResource(R.drawable.last)

                binding.txtStatus.text = "FINISH"
                binding.tickerViewBalance.textColor = Color.RED
                binding.tickerViewTotal.textColor = Color.RED
                //Refactor to respository later
//                val handler = Handler()
//                handler.postDelayed({
//                    findNavController().navigate(R.id.action_beerPouringFragment_to_beerShopFragment)
//                }, 2500)
                // TODO add balance after payment
//                viewModel.saveSaleLog()
            }
        }

        binding.gifPouring.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    Timber.i("Pouring touching card")
                    if (!isExecuted) {
                        mainHandler.post(updateTextTask)
                        mHandler.removeCallbacks(paymentAction)
                    }
                }
                MotionEvent.ACTION_UP -> {
                    Timber.i("Pouring out over card")
                    isDone = true
                    mainHandler.removeCallbacks(updateTextTask)
                    mHandler.postDelayed(paymentAction, 3000)
                }
            }
            true
        }
    }
}
