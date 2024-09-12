package com.example.yemeksiparis.di

import com.example.yemeksiparis.data.YDataSource
import com.example.yemeksiparis.data.YRepo
import com.example.yemeksiparis.retrofit.ApiUtils
import com.example.yemeksiparis.retrofit.YemeklerDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun provideYemeklerDataSource(ydao: YemeklerDao) : YDataSource{
        return YDataSource(ydao)
    }

    @Provides
    @Singleton
    fun provideYemeklerRepository(yds: YDataSource) : YRepo{
        return YRepo(yds)
    }
    @Provides
    @Singleton
    fun provideYemeklerDao() : YemeklerDao{
        return ApiUtils.getYemeklerDao()
    }



}