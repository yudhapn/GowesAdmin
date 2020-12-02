package id.forum_admin.gowes

import android.content.Context.MODE_PRIVATE
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import id.forum_admin.core.account.data.repository.AccountRepositoryImpl
import id.forum_admin.core.account.data.service.AccountApolloService
import id.forum_admin.core.account.domain.repository.AccountRepository
import id.forum_admin.gowes.account.domain.usecase.AuthenticateUseCase
import id.forum_admin.gowes.account.domain.usecase.UpdateAccountCacheUseCase
import id.forum_admin.core.account.presentation.UserAccountViewModel
import id.forum_admin.gowes.data.Token
import id.forum_admin.gowes.network.*
import id.forum_admin.gowes.user.domain.model.User
import id.forum_admin.gowes.util.SHARED_TKN
import id.forum_admin.gowes.util.SHARED_USR
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val accountDataModule = module {
    factory {
        val sharedPref = androidApplication().getSharedPreferences(SHARED_TKN, MODE_PRIVATE)
        val tknJson = sharedPref.getString(SHARED_TKN, "")
        tknJson.let {
            Gson().fromJson(tknJson, Token::class.java)
        } ?: Token()
    }
    factory {
        val sharedPref = androidApplication().getSharedPreferences(SHARED_TKN, MODE_PRIVATE)
        val userJson = sharedPref.getString(SHARED_USR, "")
        userJson.let {
            Gson().fromJson(userJson, User::class.java)
        } ?: User()
    }

    factory {
        val prefEditor = androidApplication().getSharedPreferences(SHARED_TKN, MODE_PRIVATE).edit()
        prefEditor
    }
}

// No need injecting twice if it has injected when the app run for the first time

val networkModule = module {
    single { getRealmClient() }
    single { getFileApolloCache(androidContext()) }
    single { getApolloClientBuilder(androidContext()) }
    factory { getApolloClient(getHttpClient(token = get(), context = androidContext()), get()) }
}

val firebaseModule = module {
    factory { FirebaseStorage.getInstance() }
    factory { FirebaseAuth.getInstance() }
}

@ExperimentalCoroutinesApi
val serviceModule = module {
    factory { AccountApolloService(get(), get()) }
}

@ExperimentalCoroutinesApi
val repositoryModule = module {
    single { AccountRepositoryImpl(get(), get()) as AccountRepository }
}

@ExperimentalCoroutinesApi
val useCaseModule = module {
    factory { AuthenticateUseCase(get()) }
    factory { UpdateAccountCacheUseCase(get()) }
}

@ExperimentalCoroutinesApi
val baseViewModelModule = module {
    viewModel { UserAccountViewModel(get(), get(), get()) }
    viewModel { AuthenticationViewModel(get(), get()) }
}

@ExperimentalCoroutinesApi
val appComponent =
    listOf(
        accountDataModule,
        networkModule,
        serviceModule,
        repositoryModule,
        useCaseModule,
        firebaseModule,
        baseViewModelModule
    )