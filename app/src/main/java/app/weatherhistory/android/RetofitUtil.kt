package app.weatherhistory.android

import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File

class RetrofitUtil {

    companion object {
        private var INSTANCE: Retrofit? = null

        fun getInstance(cacheDir: File?): Retrofit? {

            if (INSTANCE == null) {

                synchronized(Retrofit::class.java) {
                    val cacheSize: Long = 10 * 1024 * 1024 // 10 MB
                    val cache = Cache(cacheDir, cacheSize)

                    val okHttpClient = OkHttpClient.Builder()
                            .cache(cache)
                            .build()

                    INSTANCE = Retrofit.Builder()
                            .baseUrl(URL)
                            //.client(okHttpClient)
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .addConverterFactory(MoshiConverterFactory.create())
                            .build()
                }
            }

            return INSTANCE
        }
    }
}