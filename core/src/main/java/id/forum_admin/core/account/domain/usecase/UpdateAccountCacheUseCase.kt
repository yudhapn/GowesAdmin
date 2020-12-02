package id.forum_admin.gowes.account.domain.usecase

import id.forum_admin.core.account.domain.repository.AccountRepository
import id.forum_admin.gowes.user.domain.model.User

class UpdateAccountCacheUseCase(private val accountRepository: AccountRepository) {
    fun execute(user: User) {
        accountRepository.updateAccountCache(user)
    }
}