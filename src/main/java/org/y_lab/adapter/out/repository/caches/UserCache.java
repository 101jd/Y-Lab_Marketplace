package org.y_lab.adapter.out.repository.caches;

import org.y_lab.adapter.out.repository.interfaces.Cache;
import org.y_lab.application.exceptions.NotFoundException;
import org.y_lab.application.model.User;

import java.util.HashMap;
import java.util.Map;

public class UserCache implements Cache<Long, User> {

    Map<Long, User> userMap;

    public UserCache() {
        userMap = new HashMap<>();
    }

    @Override
    public Long cache(User user) {
        userMap.put(user.getId(), user);
        return user.getId();
    }

    @Override
    public User getFromCache(Long id) throws NotFoundException {
        User user = userMap.get(id);
        if (user == null)
            throw new NotFoundException("User not found");
        return user;
    }

    @Override
    public void delete(Long id) {
        userMap.remove(id);
    }
}
