package id.forum_admin.request

import id.forum_admin.request.data.repository.RequestRepositoryImpl
import id.forum_admin.request.data.service.RequestApolloService
import id.forum_admin.request.domain.repository.RequestRepository
import id.forum_admin.request.domain.usecase.AcceptCommunityRequestUseCase
import id.forum_admin.request.domain.usecase.GetCommunityCreateRequestUseCase
import id.forum_admin.request.domain.usecase.RejectCommunityRequestUseCase
import id.forum_admin.request.presentation.CommunityRequestViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module

fun injectFeature() = loadFeature

private val loadFeature by lazy {
    loadKoinModules(appComponent)
}

@ExperimentalCoroutinesApi
val serviceModule = module {
    factory { RequestApolloService() }
}

@ExperimentalCoroutinesApi
val repositoryModule = module {
    single { RequestRepositoryImpl() as RequestRepository }
}

@ExperimentalCoroutinesApi
val useCaseModule = module {
    factory { GetCommunityCreateRequestUseCase(get()) }
    factory { AcceptCommunityRequestUseCase(get()) }
    factory { RejectCommunityRequestUseCase(get()) }
}

@ExperimentalCoroutinesApi
val viewModelModule = module {
    viewModel { CommunityRequestViewModel(get(), get(), get(), get()) }
}

@ExperimentalCoroutinesApi
val appComponent =
    listOf(
        serviceModule,
        repositoryModule,
        useCaseModule,
        viewModelModule
    )