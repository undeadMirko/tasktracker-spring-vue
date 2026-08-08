package com.example.tasktracker.service;

import com.example.tasktracker.model.Task;
import com.example.tasktracker.model.User;
import com.example.tasktracker.payload.request.TaskRequest;
import com.example.tasktracker.repository.TaskRepository;
import com.example.tasktracker.repository.TaskSpecification;
import com.example.tasktracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Task> getTasks(Task.Status status, Task.Priority priority, Long assigneeId) {
        Specification<Task> spec = Specification
                .where(TaskSpecification.hasStatus(status))
                .and(TaskSpecification.hasPriority(priority))
                .and(TaskSpecification.hasAssignee(assigneeId));
        return taskRepository.findAll(spec);
    }

    public Task createTask(TaskRequest request) {
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        if (request.getStatus() != null) task.setStatus(request.getStatus());
        if (request.getPriority() != null) task.setPriority(request.getPriority());
        task.setDueDate(request.getDueDate());

        if (request.getAssigneeId() != null) {
            User assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new RuntimeException("Assignee not found"));
            task.setAssignee(assignee);
        }

        return taskRepository.save(task);
    }

    public Task updateTask(Long id, TaskRequest request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (request.getTitle() != null) task.setTitle(request.getTitle());
        if (request.getDescription() != null) task.setDescription(request.getDescription());
        if (request.getStatus() != null) task.setStatus(request.getStatus());
        if (request.getPriority() != null) task.setPriority(request.getPriority());
        if (request.getDueDate() != null) task.setDueDate(request.getDueDate());

        if (request.getAssigneeId() != null) {
            User assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new RuntimeException("Assignee not found"));
            task.setAssignee(assignee);
        }

        return taskRepository.save(task);
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    public Map<String, Object> getDashboardMetrics() {
        List<Task> allTasks = taskRepository.findAll();
        Map<String, Object> metrics = new HashMap<>();
        
        long total = allTasks.size();
        long pending = allTasks.stream().filter(t -> t.getStatus() == Task.Status.PENDING).count();
        long inProgress = allTasks.stream().filter(t -> t.getStatus() == Task.Status.IN_PROGRESS).count();
        long done = allTasks.stream().filter(t -> t.getStatus() == Task.Status.DONE).count();

        long high = allTasks.stream().filter(t -> t.getPriority() == Task.Priority.HIGH).count();
        long medium = allTasks.stream().filter(t -> t.getPriority() == Task.Priority.MEDIUM).count();
        long low = allTasks.stream().filter(t -> t.getPriority() == Task.Priority.LOW).count();
        
        metrics.put("total", total);
        metrics.put("pending", pending);
        metrics.put("inProgress", inProgress);
        metrics.put("done", done);

        metrics.put("high", high);
        metrics.put("medium", medium);
        metrics.put("low", low);

        return metrics;
    }
}
