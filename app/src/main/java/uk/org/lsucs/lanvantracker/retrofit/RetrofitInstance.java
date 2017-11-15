package uk.org.lsucs.lanvantracker.retrofit;

import android.content.Context;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import uk.org.lsucs.lanvantracker.BuildConfig;
import uk.org.lsucs.lanvantracker.retrofit.dropup.DropUpAPI;
import uk.org.lsucs.lanvantracker.retrofit.van.VanAPI;
import uk.org.lsucs.lanvantracker.utils.SharedPreferencesUtil;

/**
 * Created by Zack Pollard on 11/11/2017.
 */

public class RetrofitInstance {

    private Retrofit retrofit;
    private VanAPI vanAPI;
    private DropUpAPI dropUpAPI;

    public RetrofitInstance(final Context context) {

        final OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();

        okHttpClientBuilder.addInterceptor(new HeaderInterceptor(context));

        OkHttpClient okHttpClient = okHttpClientBuilder.build();

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(BuildConfig.API_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create());
        retrofit = builder.build();
    }

    public VanAPI vanAPI() {
        if (vanAPI == null) {
            vanAPI = retrofit.create(VanAPI.class);
        }
        return vanAPI;
    }

    public DropUpAPI dropUpAPI() {
        if (dropUpAPI == null) {
            dropUpAPI = retrofit.create(DropUpAPI.class);
        }
        return dropUpAPI;
    }

    private class HeaderInterceptor implements Interceptor {

        private Context context;

        HeaderInterceptor(Context context) {
            this.context = context;
        }

        @Override
        public Response intercept(final Chain chain) throws IOException {

            return chain.proceed(chain.request()
                    .newBuilder()
                    .addHeader("AuthKey", SharedPreferencesUtil.getStringValue(context, SharedPreferencesUtil.AUTH_KEY))
                    .build()
            );
        }
    }
}
