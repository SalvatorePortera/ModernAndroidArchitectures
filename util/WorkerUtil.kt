package com.nereus.craftbeer.util;

import androidx.work.*
import com.nereus.craftbeer.constant.*
import com.nereus.craftbeer.model.printer.Receipt
import com.nereus.craftbeer.worker.*
import timber.log.Timber
import java.util.concurrent.TimeUnit

fun setupSyncGoodsRecurringWork(token: String, workManager: WorkManager) {
    val repeatingRequest = PeriodicWorkRequestBuilder<SyncGoodsToDatabaseWorker>(
        SYNC_REPEAT_INTERVAL,
        SYNC_TIME_UNIT
    )
        .setInputData(Data.Builder().putString(ACCESS_TOKEN, token).build())
        .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 1, TimeUnit.MINUTES)
        .build()

    Timber.d("WorkManager: Periodic Work request for sync GOODS is scheduled")
    workManager.enqueueUniquePeriodicWork(
        SyncGoodsToDatabaseWorker.WORK_NAME,
        ExistingPeriodicWorkPolicy.REPLACE,
        repeatingRequest
    )
}

fun setupSyncSaleLogsRecurringWork(token: String, workManager: WorkManager) {
    val repeatingRequest = PeriodicWorkRequestBuilder<SyncSaleLogsToCoreServerWorker>(
        SYNC_REPEAT_INTERVAL,
        SYNC_TIME_UNIT
    )
        .setInputData(Data.Builder().putString(ACCESS_TOKEN, token).build())
        .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 1, TimeUnit.MINUTES)
        .build()

    Timber.d("WorkManager: Periodic Work request for sync sale logs is scheduled")
    workManager.enqueueUniquePeriodicWork(
        SyncSaleLogsToCoreServerWorker.WORK_NAME,
        ExistingPeriodicWorkPolicy.REPLACE,
        repeatingRequest
    )
}

fun setupSyncErrorLogsRecurringWork(token: String, workManager: WorkManager) {
    val repeatingRequest = PeriodicWorkRequestBuilder<SyncErrorLogsToCoreServerWorker>(
        SYNC_REPEAT_INTERVAL,
        SYNC_TIME_UNIT
    )
        .setInputData(Data.Builder().putString(ACCESS_TOKEN, token).build())
        .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 1, TimeUnit.MINUTES)
        .build()

    Timber.d("WorkManager: Periodic Work request for sync error logs is scheduled")
    workManager.enqueueUniquePeriodicWork(
        SyncErrorLogsToCoreServerWorker.WORK_NAME,
        ExistingPeriodicWorkPolicy.REPLACE,
        repeatingRequest
    )
}

fun setupSyncShopInfoRecurringWork(
    token: String,
    workManager: WorkManager,
    companyId: String,
    shopId: String
) {
    val repeatingRequest = PeriodicWorkRequestBuilder<SyncShopInfoWorker>(
        SHOP_INFO_SYNC_REPEAT_INTERVAL,
        SHOP_INFO_SYNC_TIME_UNIT
    )
        .setInputData(
            Data.Builder().putString(ACCESS_TOKEN, token)
                .putString(COMPANY_ID, companyId)
                .putString(SHOP_ID, shopId).build()
        )
        .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 1, TimeUnit.MINUTES)
        .build()

    Timber.d("WorkManager: Periodic Work request for sync shop info is scheduled")
    workManager.enqueueUniquePeriodicWork(
        SyncShopInfoWorker.WORK_NAME,
        ExistingPeriodicWorkPolicy.REPLACE,
        repeatingRequest
    )
}

fun setupUpdateBeerDataRecurringWork(
    workManager: WorkManager
) {
    val repeatingRequest = PeriodicWorkRequestBuilder<UpdateBeerWorker>(
        SHOP_INFO_SYNC_REPEAT_INTERVAL,
        SHOP_INFO_SYNC_TIME_UNIT
    )
        .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 1, TimeUnit.MINUTES)
        .build()

    Timber.d("WorkManager: Periodic Work request for download beer info is scheduled")
    workManager.enqueueUniquePeriodicWork(
        UpdateBeerWorker.WORK_NAME,
        ExistingPeriodicWorkPolicy.REPLACE,
        repeatingRequest
    )
}


/**
 * Setup WorkManager background job to 'fetch' new network data immediately.
 */
fun setupPrinterWork(
    token: String,
    workManager: WorkManager,
    receipts: List<Receipt>
) {
    val data: Map<String, Any> = hashMapOf(ACCESS_TOKEN to token, RECEIPT_LIST to receipts)

    val request = OneTimeWorkRequestBuilder<PrinterWorker>()
        .setInputData(Data.Builder().putAll(data).build())
        .build()

    Timber.d("--- WorkManager: One time Printer request for sync is executed")
    workManager.enqueueUniqueWork(
        PrinterWorker.WORK_NAME,
        ExistingWorkPolicy.REPLACE,
        request
    )
}