package com.zer0s2m.creeptenuous.common.components;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Component for storing information about user avatars.
 */
@Getter
@Component
public final class UploadAvatar {

    @Value("${file.upload-avatar:/var/www/creep-tenuous/avatars}")
    private String uploadAvatarDir;

}
