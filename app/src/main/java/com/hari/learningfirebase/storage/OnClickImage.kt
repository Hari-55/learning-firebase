package com.hari.learningfirebase.storage

import com.google.firebase.storage.StorageReference

interface OnClickImage {
    fun click(image: ImageModel, imagesRef: StorageReference)
}