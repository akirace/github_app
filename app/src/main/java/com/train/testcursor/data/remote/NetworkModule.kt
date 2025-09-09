package com.train.testcursor.data.remote

import com.train.testcursor.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object NetworkModule {
	private const val BASE_URL: String = "https://api.github.com/"

	private val authInterceptor = Interceptor { chain ->
		val original = chain.request()
		val token = BuildConfig.GITHUB_TOKEN
		val builder = original.newBuilder()
			.header("Accept", "application/vnd.github+json")
			.header("X-GitHub-Api-Version", "2022-11-28")
			.header("User-Agent", "testcursor-app")
		if (token.isNotEmpty()) {
			builder.header("Authorization", "Bearer $token")
		}
		chain.proceed(builder.build())
	}

	private fun buildOkHttpClient(): OkHttpClient {
		val logging = HttpLoggingInterceptor().apply {
			level = HttpLoggingInterceptor.Level.BASIC
		}
		return OkHttpClient.Builder()
			.addInterceptor(authInterceptor)
			.addInterceptor(logging)
			.connectTimeout(30, TimeUnit.SECONDS)
			.readTimeout(30, TimeUnit.SECONDS)
			.writeTimeout(30, TimeUnit.SECONDS)
			.build()
	}

	private fun buildRetrofit(): Retrofit {
		val moshi = Moshi.Builder()
			.add(KotlinJsonAdapterFactory())
			.build()
		return Retrofit.Builder()
			.baseUrl(BASE_URL)
			.client(buildOkHttpClient())
			.addConverterFactory(MoshiConverterFactory.create(moshi))
			.build()
	}

	val apiService: GithubApiService by lazy {
		buildRetrofit().create(GithubApiService::class.java)
	}
}


