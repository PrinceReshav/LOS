package com.los.losadminservice.employee.hierarchy;

import com.los.losadminservice.employee.model.Employee;
import com.los.losadminservice.employee.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class EmployeeHierarchyResolver {

    private final EmployeeRepository employeeRepository;

    /**
     * Returns all employees under a manager
     */
    public Set<String> resolveSubordinates(String managerEmployeeId){

        List<Employee> allEmployees =
                employeeRepository.findAll();

        Map<String, HierarchyNode> nodeMap = new HashMap<>();

        for(Employee e : allEmployees){
            nodeMap.put(
                    e.getEmployeeId(),
                    new HierarchyNode(e.getEmployeeId())
            );
        }

        for(Employee e : allEmployees){

            if(e.getManagerEmployeeId() == null) continue;

            HierarchyNode manager =
                    nodeMap.get(e.getManagerEmployeeId());

            HierarchyNode child =
                    nodeMap.get(e.getEmployeeId());

            if(manager != null){
                manager.addChild(child);
            }
        }

        HierarchyNode root = nodeMap.get(managerEmployeeId);

        if(root == null){
            return Collections.emptySet();
        }

        Set<String> result = new HashSet<>();

        traverse(root, result, new HashSet<>());

        result.remove(managerEmployeeId);

        return result;
    }

    /**
     * DFS traversal
     */
    private void traverse(
            HierarchyNode node,
            Set<String> result,
            Set<String> visited
    ){

        if(node == null) return;

        if(visited.contains(node.getEmployeeId()))
            return;

        visited.add(node.getEmployeeId());

        result.add(node.getEmployeeId());

        for(HierarchyNode child : node.getChildren()){
            traverse(child,result,visited);
        }
    }
}