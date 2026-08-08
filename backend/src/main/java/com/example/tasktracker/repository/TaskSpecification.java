package com.example.tasktracker.repository;

import com.example.tasktracker.model.Task;
import com.example.tasktracker.model.User;
import org.springframework.data.jpa.domain.Specification;

public class TaskSpecification {

    public static Specification<Task> hasStatus(Task.Status status) {
        return (root, query, criteriaBuilder) -> 
            status == null ? null : criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<Task> hasPriority(Task.Priority priority) {
        return (root, query, criteriaBuilder) -> 
            priority == null ? null : criteriaBuilder.equal(root.get("priority"), priority);
    }

    public static Specification<Task> hasAssignee(Long assigneeId) {
        return (root, query, criteriaBuilder) -> {
            if (assigneeId == null) return null;
            return criteriaBuilder.equal(root.get("assignee").get("id"), assigneeId);
        };
    }
}
