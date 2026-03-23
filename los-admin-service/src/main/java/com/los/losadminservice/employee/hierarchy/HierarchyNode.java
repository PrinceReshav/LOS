package com.los.losadminservice.employee.hierarchy;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class HierarchyNode {

    private final String employeeId;

    private final List<HierarchyNode> children = new ArrayList<>();

    public HierarchyNode(String employeeId) {
        this.employeeId = employeeId;
    }

    public void addChild(HierarchyNode node) {
        children.add(node);
    }
}