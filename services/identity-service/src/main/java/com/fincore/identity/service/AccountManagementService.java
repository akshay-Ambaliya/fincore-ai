
package com.fincore.identity.service;

import com.fincore.identity.entity.User;

public interface AccountManagementService {

    void deactivateAccount(Long userId);


    void activateAccount(Long userId);

    void deleteAccount(User user, String currentPassword);

}
