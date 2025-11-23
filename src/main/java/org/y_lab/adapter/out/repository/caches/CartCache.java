package org.y_lab.adapter.out.repository.caches;

import org.y_lab.adapter.out.repository.interfaces.Cache;
import org.y_lab.application.exceptions.NotFoundException;
import org.y_lab.application.model.Cart;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CartCache implements Cache<UUID, Cart> {

    Map<UUID, Cart> cartMap;

    public CartCache() {
        cartMap = new HashMap<>();
    }

    @Override
    public UUID cache(Cart cart) {
        cartMap.put(cart.getId(), cart);
        return cart.getId();
    }

    @Override
    public Cart fromCache(UUID id) throws NotFoundException {
        Cart cart = cartMap.get(id);
        if (cart == null)
            throw new NotFoundException("Cart not found");
        return cart;
    }

    @Override
    public void delete(UUID id) {
        cartMap.remove(id);
    }
}
