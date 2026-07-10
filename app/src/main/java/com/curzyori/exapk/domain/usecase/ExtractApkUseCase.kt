package com.curzyori.exapk.domain.usecase

import com.curzyori.exapk.data.model.AppInfo
import com.curzyori.exapk.data.repository.AppRepository
import kotlinx.coroutines.flow.Flow
import com.curzyori.exapk.data.model.BatchExtractProgress
import javax.inject.Inject

class ExtractApkUseCase @Inject constructor(
    private val appRepository: AppRepository
) {
    operator fun invoke(apps: List<AppInfo>): Flow<BatchExtractProgress> {
        return appRepository.extractApkFlow(apps)
    }
}
