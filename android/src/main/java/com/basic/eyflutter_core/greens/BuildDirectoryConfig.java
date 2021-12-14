package com.basic.eyflutter_core.greens;

import com.basic.eyflutter_core.enums.DirectoryNames;
import com.cloud.eyutils.events.OnEntrySyncCall;
import com.cloud.eyutils.storage.files.DirectoryUtils;

public class BuildDirectoryConfig implements OnEntrySyncCall {
    @Override
    public void onCall() {
        DirectoryUtils directoryUtils = DirectoryUtils.getInstance();
        directoryUtils.addDirectory(DirectoryNames.cacheDir.name());
        directoryUtils.addDirectory(DirectoryNames.logs.name());
        directoryUtils.addDirectory(DirectoryNames.images.name());
        directoryUtils.addDirectory(DirectoryNames.videos.name());
        directoryUtils.addDirectory(DirectoryNames.temporary.name());
        directoryUtils.addDirectory(DirectoryNames.compression.name());
        directoryUtils.buildDirectories();
    }
}
