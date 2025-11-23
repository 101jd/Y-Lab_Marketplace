package org.y_lab.adapter.out.repository.caches;

import org.y_lab.adapter.out.repository.interfaces.Cache;
import org.y_lab.application.exceptions.NotFoundException;
import org.y_lab.application.model.MarketPlace.Item;

import java.util.HashMap;
import java.util.Map;

public class ItemCache implements Cache<Long, Item> {

    Map<Long, Item> itemMap;

    public ItemCache() {
        itemMap = new HashMap<>();
    }

    @Override
    public Long cache(Item item) {
        Long id = item.getProduct().getId();
        itemMap.put(id, item);

        return id;
    }

    @Override
    public Item getFromCache(Long id) throws NotFoundException {
        Item item = itemMap.get(id);

        if (item == null)
            throw new NotFoundException("Item not found");
        return item;
    }

    @Override
    public void delete(Long id) {
        itemMap.remove(id);
    }
}
