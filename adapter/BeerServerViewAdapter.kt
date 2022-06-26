package com.nereus.craftbeer.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nereus.craftbeer.R
import com.nereus.craftbeer.databinding.RecyclerviewBeerserverItemLayoutBinding
import com.nereus.craftbeer.model.*
import com.nereus.craftbeer.model.obniz.ObnizInfo
import com.nereus.craftbeer.util.displayToast
import com.nereus.craftbeer.util.getStringResource
import com.nereus.craftbeer.util.hideKeyboard
import com.nereus.craftbeer.util.setOnClickDebounce
import com.nereus.craftbeer.viewmodel.SettingMasterViewModel
import com.squareup.picasso.Picasso
import timber.log.Timber


/**
 * Beer server view adapter
 *
 * @property viewModel
 * @constructor
 *
 * @param context
 */
class BeerServerViewAdapter(
    context: Context,
    val viewModel: SettingMasterViewModel


) : ListAdapter<CombinationBeersInfo, BeerServerViewAdapter.ViewHolder>(BeerDiffCallBack()) {
    private val context: Context = context

    /**
     * On create view holder
     *
     * @param parent
     * @param viewType
     * @return
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(viewModel, parent)
    }


    /**
     * On bind view holder
     *
     * @param holder
     * @param position
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        viewModel.connectSocket()
        val item = getItem(position)
        holder.bind(item)
    }


    /**
     * View holder
     *
     * @property binding
     * @constructor Create empty View holder
     */
    class ViewHolder private constructor(val binding: RecyclerviewBeerserverItemLayoutBinding) :
        RecyclerView.ViewHolder(
            binding.root
        ) {

        /**
         * Bind
         *
         * @param item
         */
        fun bind(item: CombinationBeersInfo) {
            if(!item.imageUrl.isNullOrBlank()){
                Picasso.get().load(item.imageUrl).into(binding.imgBeerServerItem)
            }
            binding.beer = item
            // Set current attached Server CD
            var i = 0
            while (i < binding.spCD.adapter.count) {
                if (binding.beer!!.obnizId!!.trim() == binding.spCD.adapter.getItem(i).toString()
                        .trim()
                ) {
                    binding.spCD.setSelection(i)
                    break
                }
                i++
            }
            // **********
            i = 0
            while (i < binding.spBeerShop.adapter.count) {
                if (binding.beer!!.beerName!!.trim() == binding.spBeerShop.adapter.getItem(i)
                        .toString().trim()
                ) {
                    binding.spBeerShop.setSelection(i)
                    break
                }
                i++
            }

            binding.executePendingBindings()
        }

        companion object {
            fun from(viewModel: SettingMasterViewModel, parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding =
                    RecyclerviewBeerserverItemLayoutBinding.inflate(layoutInflater, parent, false)


                // Binding Obniz Id
                val listObniz: LiveData<List<ObnizInfo>> = viewModel.obnizs
                var listObnizId: ArrayList<String> = ArrayList()
                for (i in listObniz.value!!.indices) {
                    listObnizId.add(listObniz.value!![i].obnizId)
                }
                val adapter: ArrayAdapter<String> = ArrayAdapter(
                    (parent.context),
                    android.R.layout.simple_spinner_item,
                    listObnizId
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spCD.adapter = adapter
                binding.spCD.onItemSelectedListener = object : OnItemSelectedListener {
                    override fun onItemSelected(
                        parentView: AdapterView<*>?,
                        selectedItemView: View,
                        position: Int,
                        id: Long
                    ) {
                        for (i in viewModel.obnizs.value!!.indices) {
                            if (viewModel.obnizs.value!![i].obnizId.trim() == binding.spCD.selectedItem.toString()
                                    .trim()
                            ) {
                                binding.varTmp.text = viewModel.obnizs.value!![i].id
                                break
                            }
                        }
                    }

                    override fun onNothingSelected(parentView: AdapterView<*>?) {

                    }
                }
                // ********



                // Binding Tap Beer Shop
                val listTapBeerShop: LiveData<List<TabBeerDeviceInfoList>> = viewModel.tapBeerDevice
                val listTabBeerName: ArrayList<String> = ArrayList()
                for (i in listTapBeerShop.value!!.indices) {
                    listTabBeerName.add(listTapBeerShop.value!![i].beerName)
                }
                val adapterTapBeer: ArrayAdapter<String> = ArrayAdapter(
                    (parent.context),
                    android.R.layout.simple_spinner_item,
                    listTabBeerName
                )
                adapterTapBeer.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spBeerShop.adapter = adapterTapBeer
                binding.spBeerShop.onItemSelectedListener = object : OnItemSelectedListener {
                    override fun onItemSelected(
                        parentView: AdapterView<*>?,
                        selectedItemView: View,
                        position: Int,
                        id: Long
                    ) {
                        // This will not filter which beer has been attached to beer server
                        for (i in viewModel.tapBeerDevice.value!!.indices) {
                            if (viewModel.tapBeerDevice.value!![i].beerName
                                    .trim() == binding.spBeerShop.selectedItem.toString().trim()
                            ) {
                                binding.varTmp2.text =
                                    viewModel.tapBeerDevice.value!![i].id
                                break
                            }
                        }
                    }

                    override fun onNothingSelected(parentView: AdapterView<*>?) {

                    }
                }

                binding.swStatus.setOnClickDebounce {
                    if (binding.swStatus.isChecked) {

                        Timber.i("swStatus forceStop: obnizId: " + binding.beer?.obnizId!!)

                        binding.beer!!.maintainFlag = 0
                        binding.swStatus.text = getStringResource(R.string.beer_status_on)
                        binding.spanCover.visibility = View.GONE
                        viewModel.forceStopSocket(binding.beer?.obnizId!!)
                        viewModel.handlePourBeerGross(binding.beer!!)
                    } else {
                        binding.beer!!.maintainFlag = 1
                        binding.swStatus.text = getStringResource(R.string.beer_status_off)
                        binding.spanCover.visibility = View.VISIBLE
                        viewModel.setupPouringBeer(binding.beer?.obnizId!!)
//                            viewModel.simulatePourOutGrossBeer()
                    }
                    viewModel.updateBeersStatus(binding.beer!!)
                }

                binding.callback = object : BeerServerClickCallBack {

//                    override fun onCheck(beer: CombinationBeersInfo) {
//                        viewModel.updateBeersStatus(beer)
//                        if (binding.swStatus.isChecked) {
//                            binding.swStatus.text = getStringResource(R.string.beer_status_on)
//                            binding.spanLayout1.visibility = View.GONE
//                            binding.spanLayout2.visibility = View.GONE
//                            binding.spanLayout3.visibility = View.GONE
//                            viewModel.forceStopSocket(beer.obnizId!!)
//                            viewModel.handlePourBeerGross(beer)
//                        } else {
//                            binding.swStatus.text = getStringResource(R.string.beer_status_off)
//                            binding.spanLayout1.visibility = View.VISIBLE
//                            binding.spanLayout2.visibility = View.VISIBLE
//                            binding.spanLayout3.visibility = View.VISIBLE
//                            viewModel.setupPouringBeer(beer.obnizId!!)
////                            viewModel.simulatePourOutGrossBeer()
//                        }
//                    }

                    override fun onUpdate() {
                        val updateTapbeerId = binding.beer?.id.toString()
                        val beerServerUpdate =
                            BeerServerUpdate(binding.varTmp.text.toString().trim())
                        val tapBeerUpdate = TapBeerUpdate(binding.varTmp2.text.toString().trim())
                        val originalAmount = binding.txtOriginalAmountEdit.text.toString().trim()
                        val outStandard = binding.txtOutStandingAmount.text.toString().trim()
                        if ((originalAmount.isNullOrBlank() || outStandard.isNullOrBlank())) {
                            displayToast(
                                parent.context,
                                "Original amount or out standard amount could not be left blank or empty"
                            )
                        } else {
                            val updateRequest = UpdateTapBeerServerRequest(
                                beerServerUpdate,
                                tapBeerUpdate,
                                originalAmount.toInt(),
                                null,
                                outStandard.toInt()
                            )
                            viewModel.updateTapBeerServer(updateTapbeerId, updateRequest)
                        }
                    }

                    override fun onReset() {
                        val updateTapbeerId = binding.beer?.id.toString()
                        val remainingAmount = binding.beer?.originalAmount
                        val updateRequest = UpdateTapBeerServerRequest(
                            null,
                            null,
                            null,
                            remainingAmount,
                            null
                        )
                        viewModel.updateTapBeerServer(updateTapbeerId, updateRequest)
                    }

                    override fun onHideKeyBoard(){
                        binding.spanCover.hideKeyboard(binding.imgBeerServerItem.context)
                    }
                }
                return ViewHolder(binding)
            }
        }
    }
}

/**
 * Beer server click call back
 *
 * @constructor Create empty Beer server click call back
 */
internal interface BeerServerClickCallBack {
//    fun onCheck(beer: CombinationBeersInfo)
    fun onUpdate()
    fun onReset()
    fun onHideKeyBoard()
}

/**
 * Beer diff call back
 *
 * @constructor Create empty Beer diff call back
 */
class BeerDiffCallBack : DiffUtil.ItemCallback<CombinationBeersInfo>() {
    override fun areItemsTheSame(
        oldItem: CombinationBeersInfo,
        newItem: CombinationBeersInfo
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: CombinationBeersInfo,
        newItem: CombinationBeersInfo
    ): Boolean {
        return oldItem == newItem
    }
}

