package id.forum_admin.gowes.util

import com.apollographql.apollo.fetcher.ApolloResponseFetchers
import com.apollographql.apollo.fetcher.ResponseFetcher


const val SHARED_TKN = "shared_token"
const val SHARED_TKN_FB = "shared_token_fb"
const val SHARED_USR = "shared_user_data"

fun Boolean.apolloResponseFetchers(): ResponseFetcher = if (this) ApolloResponseFetchers.NETWORK_ONLY else ApolloResponseFetchers.CACHE_FIRST
