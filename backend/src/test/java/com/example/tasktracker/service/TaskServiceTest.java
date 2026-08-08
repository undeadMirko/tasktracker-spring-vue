package com.example.tasktracker.service;

import com.example.tasktracker.model.Task;
import com.example.tasktracker.model.User;
import com.example.tasktracker.payload.request.TaskRequest;
import com.example.tasktracker.repository.TaskRepository;
import com.example.tasktracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TaskService taskService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateTask_Success() {
        TaskRequest request = new TaskRequest();
        request.setTitle("Test Task");
        request.setDescription("Desc");
        request.setStatus(Task.Status.PENDING);
        request.setPriority(Task.Priority.HIGH);
        request.setDueDate(LocalDate.now());

        Task savedTask = new Task();
        savedTask.setId(1L);
        savedTask.setTitle("Test Task");

        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        Task result = taskService.createTask(request);

        assertNotNull(result);
        assertEquals("Test Task", result.getTitle());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void testCreateTask_WithAssignee() {
        TaskRequest request = new TaskRequest();
        request.setTitle("Test Task");
        request.setAssigneeId(1L);

        User assignee = new User("user1", "pass");
        assignee.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(assignee));
        
        Task savedTask = new Task();
        savedTask.setId(1L);
        savedTask.setTitle("Test Task");
        savedTask.setAssignee(assignee);

        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        Task result = taskService.createTask(request);

        assertNotNull(result);
        assertEquals(assignee, result.getAssignee());
    }
}
