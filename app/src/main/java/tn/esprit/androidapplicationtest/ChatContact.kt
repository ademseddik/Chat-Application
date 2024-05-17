package tn.esprit.androidapplicationtest

data class ChatContact(
    val senderName: String,
    val messageContent: String,
    val isRead: Boolean // Assuming this flag indicates whether the message is read or not
)