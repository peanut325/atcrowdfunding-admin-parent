package org.fall.service.api;

import crowd.entity.Menu;

import java.util.List;

public interface MenuService {
    // 查找所有节点
    List<Menu> getAll();

    // 添加节点
    void saveMenu(Menu menu);

    // 修改节点
    void updateMenu(Menu menu);

    // 删除节点
    void remove(Integer id);
}