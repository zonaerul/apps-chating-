package com.chaerul.chating.adapter.data;

public class ChatMessage {
    private String messageId;
    private String email;
    private String message;
    private String profile;
    private String timestamp;
    private String sender;
    private boolean isUser;
    private boolean isRead;  // Status apakah pesan sudah dibaca
    private boolean isOnline;  // Status online/offline
    private String lastOnline;  // Waktu terakhir kali pengguna online
    private Receiver receiver;

    // Empty constructor for Firebase
    public ChatMessage() {}

    public ChatMessage(String messageId, String email, String message, String profile, String timestamp, String sender, boolean isUser, boolean isRead, boolean isOnline, String lastOnline, Receiver receiver) {
        this.messageId = messageId;
        this.email = email;
        this.message = message;
        this.profile = profile;
        this.timestamp = timestamp;
        this.sender = sender;
        this.isUser = isUser;
        this.isRead = isRead;  // Menyimpan status 'isRead'
        this.isOnline = isOnline;  // Menyimpan status 'isOnline'
        this.lastOnline = lastOnline;  // Menyimpan waktu terakhir online
        this.receiver = receiver;  // Menyimpan receiver
    }

    // Getter methods
    public String getMessageId() {
        return messageId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getProfile() {
        return profile;
    }

    public String getMessage() {
        return message;
    }

    public String getEmail() {
        return email;
    }

    public String getSender() {
        return sender;
    }

    public boolean isUser() {
        return isUser;
    }

    public boolean isRead() {
        return isRead;  // Getter untuk status 'isRead'
    }

    public boolean isOnline() {
        return isOnline;  // Getter untuk status 'isOnline'
    }

    public String getLastOnline() {
        return lastOnline;  // Getter untuk waktu terakhir online
    }

    public Receiver getReceiver() {
        return receiver;
    }

    // Setter methods for Firebase
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setUser(boolean isUser) {
        this.isUser = isUser;
    }

    public void setRead(boolean isRead) {
        this.isRead = isRead;  // Setter untuk status 'isRead'
    }

    public void setOnline(boolean isOnline) {
        this.isOnline = isOnline;  // Setter untuk status 'isOnline'
    }

    public void setLastOnline(String lastOnline) {
        this.lastOnline = lastOnline;  // Setter untuk waktu terakhir online
    }

    public void setReceiver(Receiver receiver) {
        this.receiver = receiver;
    }

    // Receiver class untuk menampung sender dan receiver email
    public static class Receiver {
        private String sender;
        private String receiver;

        // Konstruktor untuk Firebase
        public Receiver() {}

        public Receiver(String sender, String receiver) {
            this.sender = sender;
            this.receiver = receiver;
        }

        public String getSender() {
            return sender;
        }

        public String getReceiver() {
            return receiver;
        }

        public void setSender(String sender) {
            this.sender = sender;
        }

        public void setReceiver(String receiver) {
            this.receiver = receiver;
        }
    }
}
