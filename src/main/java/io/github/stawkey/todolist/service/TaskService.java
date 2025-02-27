package io.github.stawkey.todolist.service;

import io.github.stawkey.todolist.entity.Task;
import io.github.stawkey.todolist.repository.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class TaskService {

    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);
    private final TaskRepository taskRepository;

    TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Page<Task> getTasks(int page, int limit) {
        if (page < 0) {
            throw new IllegalArgumentException("Page number cannot be negative");
        }
        if (limit <= 0) {
            throw new IllegalArgumentException("Limit of tasks per page must be greater than zero");
        }

        logger.debug("Fetching tasks page {} with limit {}", page, limit);
        PageRequest pageRequest = PageRequest.of(page, limit);
        return taskRepository.findAll(pageRequest);
    }

    public Task add(Task task) {
        logger.debug("Adding new task: {}", task.getTitle());
        return taskRepository.save(task);
    }

    public Task update(Integer id, Task task) {
        return taskRepository.findById(id)
                .map(existingTask -> {
                    existingTask.setTitle(task.getTitle());
                    existingTask.setDescription(task.getDescription());
                    logger.debug("Task updated successfully: {}", id);
                    return taskRepository.save(existingTask);
                })
                .orElseThrow(() -> new EntityNotFoundException("Task not found with ID: " + id));
    }

    public void delete(Integer id) {
        if (!taskRepository.existsById(id)) {
            throw new EntityNotFoundException("Task not found with ID: " + id);
        }

        logger.debug("Deleting task with ID: {}", id);
        taskRepository.deleteById(id);
    }
}