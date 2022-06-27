package com.nereus.craftbeer.repository

import com.nereus.craftbeer.R
import com.nereus.craftbeer.constant.DEFAULT_REDUCTION_TAX_RATE
import com.nereus.craftbeer.constant.DEFAULT_TAX_RATE
import com.nereus.craftbeer.constant.EMPTY_STRING
import com.nereus.craftbeer.database.dao.GoodsDao
import com.nereus.craftbeer.database.entity.Goods
import com.nereus.craftbeer.enums.ProducType
import com.nereus.craftbeer.exception.MessageException
import com.nereus.craftbeer.model.*
import com.nereus.craftbeer.model.obniz.ObnizInfo
import com.nereus.craftbeer.networking.ApiErrorResponse
import com.nereus.craftbeer.networking.ApiResponse
import com.nereus.craftbeer.networking.ApiSuccessResponse
import com.nereus.craftbeer.networking.CoreApiWithoutLiveData
import com.nereus.craftbeer.util.getStringResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

/**
 * Goods repository
 *
 * @property apiService
 * @property goodsDao
 * @constructor  Goods repository
 */
class GoodsRepository @Inject constructor(
    private val apiService: CoreApiWithoutLiveData,
    private val goodsDao: GoodsDao
) {

    /**
     * Is goods exist in local db
     *
     * @param barcode
     * @return
     */
    suspend fun isGoodsExistInLocalDb(barcode: String): Boolean {
        val goods = goodsDao.get(barcode)
        return goods != null
    }

    /**
     * Get goods
     *
     * @param barcode
     * @return
     */
    suspend fun getGoods(barcode: String): Goods? {
        return goodsDao.get(barcode)
    }

    /**
     * Search goods
     *
     * @param query
     * @return
     */
    suspend fun searchGoods(query: String): CombinationGoodsInfo? {
        try {
            val goodsList = withContext(Dispatchers.IO) {
                apiService.search(AuthRepository.getAccessToken(), query)
            }
            when (val response = ApiResponse.create(goodsList)) {
                is ApiSuccessResponse -> {
                    return response.body.data.firstOrNull()?.asCombinationGoodsInfo()
                }
                else -> {
                    throw MessageException(MessagesModel(R.string.msg_product_not_exist))
                }
            }
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * Get goods by product code
     *
     * @param productCode
     * @return
     */
    suspend fun getGoodsByProductCode(productCode: String): Goods? {
        return goodsDao.getByGoodsCode(productCode)
    }

    /**
     * Sync goods
     *
     * @param token
     */
    suspend fun syncGoods(token: String) {
        try {
            val goodsList = withContext(Dispatchers.IO) {
                apiService.getGoodsList1(token)
            }
            when (val response = ApiResponse.create(goodsList)) {
                is ApiSuccessResponse -> {
                    withContext(Dispatchers.IO) {
                        goodsDao.deleteAll()
                        goodsDao.insertMultiple(response.body.data
                            .map {
                                val taxRate = it.taxRate?: if (it.taxReduction == 1) DEFAULT_REDUCTION_TAX_RATE else DEFAULT_TAX_RATE
                                Goods(
                                    goodsId = it.id,
                                    goodsCode = it.goodsCode,
                                    janCode = it.janCode,
                                    type = ProducType.GOODS.getValue(),
                                    goodsName = it.goodsName ?: EMPTY_STRING,
                                    goodsShortName = it.goodsShortName ?: EMPTY_STRING,
                                    description = it.description ?: EMPTY_STRING,
                                    soldOutFlag = it.soldOutFlag,
                                    handlingFlag = it.handlingFlag,
                                    purchaseCost = it.purchaseCost,
                                    imageUrl = it.goodsShop?.firstOrNull()?.imageUrl ?: it.imageUrl
                                    ?: EMPTY_STRING,
                                    imageKey = it.goodsShop?.firstOrNull()?.imageKey ?: it.imageKey
                                    ?: EMPTY_STRING,
                                    sellingPrice = it.goodsShop?.firstOrNull()?.sellingPrice
                                        ?: it.sellingPrice,
                                    taxReduction = it.taxReduction,
                                    taxRate =  taxRate
                                )
                            })
                    }
                }
                is ApiErrorResponse -> {
                    Timber.e(response.body.message.toString())
                }
                else -> {
                    Timber.e(getStringResource(R.string.msg_internal_exception))
                }
            }

            val goods = goodsDao.getAll()


            Timber.d("goods.toString()")
            Timber.d("goods.size%s", goods.size)
            Timber.d(goods.toString())
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * Get tab beers server list
     *
     * @return
     */
    suspend fun getTabBeersServerList(): List<CombinationBeersInfo> {
        val goodsList = withContext(Dispatchers.IO) {
            apiService.getTabBeersServerList(AuthRepository.getAccessToken())
        }
        return when (val response = ApiResponse.create(goodsList)) {
            is ApiSuccessResponse -> {
                response.body.data.filter { it.tapBeer != null && it.beerServer != null }
                    .map { beer ->
                        beer.tapBeer!!.let {
                            val finalPrice: Double = if (it.tapBeerShops?.size == 0) {
                                it.sellingPrice
                            } else {
                                it.tapBeerShops?.get(0)!!.sellingPrice
                            }
                            CombinationBeersInfo(
                                tapBeerId = it.id,
                                beerName = it.beerName,
                                beerNameEn = it.beerNameEn,
                                taxRate = it.taxRate,
                                ibu = it.ibu,
                                alcohol = it.alcohol,
                                brewery = it.brewery,
                                breweryEn = it.breweryEn,
                                description = it.description,
                                imageUrl = it.imageUrl,
                                acidity = it.acidity,
                                weight = it.weight,
                                bitter = it.bitter,
                                sweet = it.sweet,
                                smell = it.smell,
                                countryCode = it.country.countryCode,
                                flagUrl = it.country.flagUrl,
                                purchaseCost = it.purchaseCost,
                                sellingPrice = finalPrice,
                                beerStyle = it.beerStyle,
                                region = it.country.region,
                                maintainFlag = beer.maintainFlag,
                                id = beer.id,
                                amountInMlL = 0,
                                obnizId = beer.beerServer!!.obnizId,
                                outStandardDisplay = beer.outStandardDisplay,
                                originalAmount = beer.originalAmount,
                                remainingAmount = beer.remainingAmount,
                                idObnizServer = beer.beerServer.id,
                                colour = it.colour
                            )
                        }
                    }
            }
            is ApiErrorResponse -> {
                Timber.e(response.body.message.toString())
                throw MessageException(MessagesModel(R.string.msg_failed_to_load_beers))
            }
            else -> {
                throw MessageException(MessagesModel(R.string.msg_internal_exception))
            }
        }

    }

    /**
     * Update beers status
     *
     * @param beersInfo
     */
    suspend fun updateBeersStatus(beersInfo: CombinationBeersInfo) {

        val result = withContext(Dispatchers.IO) {
            apiService.updateBeersStatus(
                beersInfo.id,
                AuthRepository.getAccessToken(),
                UpdateBeersStatusRequest(beersInfo.maintainFlag)
            )
        }
        return when (val response = ApiResponse.create(result)) {
            is ApiSuccessResponse -> {

            }
            is ApiErrorResponse -> {
                Timber.e(response.body.message.toString())
                throw MessageException(MessagesModel(R.string.msg_failed_to_update_beer_status))
            }
            else -> {
                throw MessageException(MessagesModel(R.string.msg_internal_exception))
            }
        }

    }


    /**
     * Get obniz server list
     *
     * @return
     */
    suspend fun getObnizServerList(): List<ObnizInfo> {
        val obnizServerList = withContext(Dispatchers.IO) {
            apiService.getObnizServerList(AuthRepository.getAccessToken())
        }
        return when (val response = ApiResponse.create(obnizServerList)) {
            is ApiSuccessResponse -> {
                response.body.data.map { obniz ->
                    ObnizInfo(
                        obniz.id,
                        obniz.updatedAt,
                        obniz.deletedAt,
                        obniz.serverCode,
                        obniz.obnizId,
                        obniz.validFlag,
                        obniz.isOnline,
                        obniz.startUsingAt,
                        obniz.shopId,
                        obniz.companyId,
                        obniz.createdAt
                    )
                }
            }
            is ApiErrorResponse -> {
                Timber.e(response.body.message.toString())
                throw MessageException(MessagesModel(R.string.msg_failed_to_load_obnizID))
            }
            else -> {
                throw MessageException(MessagesModel(R.string.msg_internal_exception))
            }
        }
    }

    /**
     * Get tap beer shop list
     *
     * @return
     */
    suspend fun getTapBeerShopList(): List<CombinationBeersInfo> {
        val tapBeerShopList = withContext(Dispatchers.IO) {
            apiService.getTapBeerShop(AuthRepository.getAccessToken())
        }
        return when (val response = ApiResponse.create(tapBeerShopList)) {
            is ApiSuccessResponse -> {
                response.body.data.filter { it.tapBeer != null }.map { beer ->
                    beer.tapBeer!!.let {
                        CombinationBeersInfo(
                            it.id,
                            it.beerName,
                            it.beerNameEn,
                            it.taxRate,
                            it.ibu,
                            it.alcohol,
                            it.brewery,
                            it.breweryEn,
                            it.description,
                            it.imageUrl,
                            it.acidity,
                            it.weight,
                            it.bitter,
                            it.sweet,
                            it.smell,
                            it.country.countryCode,
                            it.country.flagUrl,
                            it.purchaseCost,
                            it.sellingPrice,
                            it.beerStyle,
                            it.country.region,
                            beer.maintainFlag,
                            beer.id,
                            0,
                            beer.beerServer?.obnizId,
                            beer.outStandardDisplay,
                            beer.originalAmount,
                            beer.remainingAmount,
                            beer.beerServer?.id,
                            it.colour
                        )
                    }
                }
            }
            is ApiErrorResponse -> {
                Timber.e(response.body.message.toString())
                throw MessageException(MessagesModel(R.string.msg_failed_to_load_beers))
            }
            else -> {
                throw MessageException(MessagesModel(R.string.msg_internal_exception))
            }
        }
    }

    /**
     * Get tap beer device
     *
     * @return
     */
    suspend fun getTapBeerDevice(): List<TabBeerDeviceInfoList> {
        val tapBeerDeviceList = withContext(Dispatchers.IO) {
            apiService.getTapBeerDevice(AuthRepository.getAccessToken())
        }
        return when (val response = ApiResponse.create(tapBeerDeviceList)) {
            is ApiSuccessResponse -> {
                response.body.data.map { beer ->
                    TabBeerDeviceInfoList(
                        id = beer.id,
                        beerName = beer.beerName
                    )
                }
            }
            is ApiErrorResponse -> {
                Timber.e(response.body.message.toString())
                throw MessageException(MessagesModel(R.string.msg_failed_to_load_beers))
            }
            else -> {
                throw MessageException(MessagesModel(R.string.msg_internal_exception))
            }
        }
    }


    /**
     * Update tap beer server
     *
     * @param id
     * @param updateRequest
     */
    suspend fun updateTapBeerServer(
        id: String,
        updateRequest: UpdateTapBeerServerRequest
    ) {

        val result = withContext(Dispatchers.IO) {
            apiService.updateTapBeerServer(
                id,
                AuthRepository.getAccessToken(),
                updateRequest
            )
        }
        return when (val response = ApiResponse.create(result)) {
            is ApiSuccessResponse -> {

            }
            is ApiErrorResponse -> {
                Timber.e(response.body.message.toString())
                throw MessageException(MessagesModel(R.string.msg_failed_to_update_beer_status))
            }
            else -> {
                throw MessageException(MessagesModel(R.string.msg_internal_exception))
            }
        }

    }
}

