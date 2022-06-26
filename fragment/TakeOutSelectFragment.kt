package com.nereus.craftbeer.fragment

import android.widget.ToggleButton
import androidx.core.view.children
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.nereus.craftbeer.R
import com.nereus.craftbeer.databinding.FragmentTakeOutSelectBinding
import com.nereus.craftbeer.exception.MessageException
import com.nereus.craftbeer.model.MessagesModel
import com.nereus.craftbeer.util.setOnClickDebounce
import com.nereus.craftbeer.viewmodel.FoodShopViewModel

class TakeOutSelectFragment :
    BaseFragmentDialog<FragmentTakeOutSelectBinding, FoodShopViewModel>() {

    override val viewModel: FoodShopViewModel by activityViewModels()

    override fun getLayout(): Int {
        return R.layout.fragment_take_out_select
    }

    override fun afterBinding() {
        binding.viewModel = viewModel

    }

    override fun setViewListener() {
        binding.btnClose.setOnClickDebounce {
            findNavController().navigate(R.id.foodShopFragment)
        }

        binding.btnInput.setOnClickDebounce {
            findNavController().navigate(R.id.foodShopFragment)
        }


        binding.radioGroupTakeOut.children.apply {
            forEach {
                (it as ToggleButton).apply {
                    setOnCheckedChangeListener { view, isChecked ->
                        if (isChecked) {
                            viewModel.setIsTakeOut(null)
                            filter { it != view }.forEach {
                                (it as ToggleButton).isChecked = false
                            }
                            when (view.id) {
                                R.id.btn_take_in -> viewModel.setIsTakeOut(false)
                                R.id.btn_take_out -> viewModel.setIsTakeOut(true)
                            }
                        } else if (viewModel.customerAttribute.value!!.isTakeAway != null) {
                            view.isChecked = true
                            //デフォルト値はいっているから選択して例外に落とさない
                            //viewModel.setException(MessageException(MessagesModel(R.string.msg_required_field)))
                        }
                    }
                }
            }
        }

    }
}