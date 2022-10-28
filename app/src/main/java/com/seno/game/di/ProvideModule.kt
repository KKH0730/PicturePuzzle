package com.seno.game.di

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.seno.game.data.network.NetworkConstant
import com.seno.game.di.network.DiffDocRef
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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

    @Singleton
    @Provides
    fun provideFirebaseStore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @DiffDocRef
    @Singleton
    @Provides
    fun provideDiffDocRef(): DocumentReference {
        return  FirebaseFirestore.getInstance().collection("game").document("diff_picture_room")
    }
}