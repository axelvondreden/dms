package com.dude.dms.brain.options

data class StorageOptions(
        var ftp: FtpOptions,
        var backupMethod: String,
        var maxUploadFileSize: Int,
        var offlineLinkLocation: String
)
