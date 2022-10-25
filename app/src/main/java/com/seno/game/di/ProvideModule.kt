package com.seno.game.di

import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.seno.game.data.network.NetworkConstant
import com.seno.game.di.network.DiffPictureStorageRef
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class ProvideModule {
    @Singleton
    @Provides
    fun provideStorageReference(): StorageReference {
        return FirebaseStorage.getInstance().getReferenceFromUrl(NetworkConstant.DIFF_PICTURE_STORAGE_REF)
    }
}