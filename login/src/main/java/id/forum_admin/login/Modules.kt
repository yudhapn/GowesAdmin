package id.forum_admin.login

import id.forum_admin.login.data.repository.LoginRepositoryImpl
import id.forum_admin.login.domain.repository.LoginRepository
import id.forum_admin.login.domain.usecase.LoginByEmailUseCase
import id.forum_admin.login.data.service.LoginRealmService
import id.forum_admin.login.presentation.LoginViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module

@ExperimentalCoroutinesApi
fun injectFeature() = loadFeature

@ExperimentalCoroutinesApi
private val loadFeature by lazy {
    loadKoinModules(appComponent)
}

@ExperimentalCoroutinesApi
val viewModelModule = module {
    viewModel { LoginViewModel(loginByEmailUseCase = get()) }
}

@ExperimentalCoroutinesApi
val repositoryModule = module {
    single { LoginRepositoryImpl(get()) as LoginRepository }
}

@ExperimentalCoroutinesApi
val useCaseModule = module {
    factory { LoginByEmailUseCase(get()) }
}

@ExperimentalCoroutinesApi
val serviceModule = module {
    factory { LoginRealmService(get()) }
}

@ExperimentalCoroutinesApi
val appComponent =
    listOf(
        viewModelModule,
        useCaseModule,
        repositoryModule,
        serviceModule
    )
