package com.nereus.craftbeer.fragment

import android.widget.ToggleButton
import androidx.core.view.children
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.nereus.craftbeer.R
import com.nereus.craftbeer.constant.*
import com.nereus.craftbeer.databinding.FragmentCustomerAttributeFragmentdialogBinding
import com.nereus.craftbeer.exception.MessageException
import com.nereus.craftbeer.model.MessagesModel
import com.nereus.craftbeer.util.setOnClickDebounce
import com.nereus.craftbeer.viewmodel.FoodShopViewModel

/**
 * Customer attribute fragmentdialog
 *
 * コンストラクタ  Customer attribute fragmentdialog
 */
class CustomerAttributeFragmentdialog :
    BaseFragmentDialog<FragmentCustomerAttributeFragmentdialogBinding, FoodShopViewModel>() {

    /**
     * View model
     */
    override val viewModel: FoodShopViewModel by activityViewModels()

    /**
     * Get layout
     *
     * @return
     */
    override fun getLayout(): Int {
        return R.layout.fragment_customer_attribute_fragmentdialog
    }

    /**
     * After binding
     *
     */
    override fun afterBinding() {
        binding.viewModel = viewModel
    }

    /**
     * Set view listener
     *
     */
    override fun setViewListener() {
        binding.btnClose.setOnClickDebounce {
            findNavController().navigate(R.id.foodShopFragment)
        }

        binding.btnInput.setOnClickDebounce {
            findNavController().navigate(R.id.foodShopFragment)
        }

        binding.radioGroupAge.children.forEach {
            (it as ToggleButton).setOnCheckedChangeListener { view, isChecked ->
                if (isChecked) {
                    viewModel.setAgeRange(null)
                    binding.radioGroupAge.children.filter { it != view }.forEach {
                        (it as ToggleButton).isChecked = false
                    }
                    when (view.id) {
                        R.id.radio_age_20 -> viewModel.setAgeRange(AGE_20)
                        R.id.radio_age_30 -> viewModel.setAgeRange(AGE_30)
                        R.id.radio_age_40 -> viewModel.setAgeRange(AGE_40)
                        R.id.radio_age_50 -> viewModel.setAgeRange(AGE_50)
                    }
                } else if (viewModel.customerAttribute.value!!.ageRange != null) {
                    view.isChecked = true
                    //デフォルト値はいっているから選択して例外に落とさない
                    //viewModel.setException(MessageException(MessagesModel(R.string.msg_required_field)))
                }
            }
        }

        binding.radioGroupGender.children.forEach {
            (it as ToggleButton).setOnCheckedChangeListener { view, isChecked ->
                if (isChecked) {
                    viewModel.setGender(null)
                    binding.radioGroupGender.children.filter { it != view }.forEach {
                        (it as ToggleButton).isChecked = false
                    }
                    when (view.id) {
                        R.id.radio_gender_male -> viewModel.setGender(GENDER_MALE)
                        R.id.radio_gender_female -> viewModel.setGender(GENDER_FEMALE)
                        else -> viewModel.setGender(GENDER_OTHER)
                    }
                } else if (viewModel.customerAttribute.value!!.gender != null) {
                    view.isChecked = true
                    //デフォルト値はいっているから選択して例外に落とさない
                    //viewModel.setException(MessageException(MessagesModel(R.string.msg_required_field)))
                }
            }
        }
    }
}