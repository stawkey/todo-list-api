package io.github.stawkey.todolist.service;

import io.github.stawkey.todolist.dto.TaskDTO;
import io.github.stawkey.todolist.entity.Task;
import io.github.stawkey.todolist.repository.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import static io.github.stawkey.todolist.dto.TaskDTO.convertToDTO;

@Service
public class TaskService {

    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);
    private final TaskRepository taskRepository;

    TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Page<TaskDTO> getTasks(int page, int limit) {
        if (page < 0) {
            throw new IllegalArgumentException("Page number cannot be negative");
        }
        if (limit <= 0) {
            throw new IllegalArgumentException("Limit of tasks per page must be greater than zero");
        }

        logger.debug("Fetching tasks page {} with limit {}", page, limit);
        PageRequest pageRequest = PageRequest.of(page, limit);
        return taskRepository.findAll(pageRequest).map(TaskDTO::convertToDTO);
    }

    public TaskDTO add(TaskDTO taskDTO) {
        Task task = new Task(
                taskDTO.userId(),
                taskDTO.title(),
                taskDTO.description()
        );

        logger.debug("Adding new task: {}", task.getTitle());
        Task savedTask = taskRepository.save(task);
        return convertToDTO(savedTask);
    }

    public TaskDTO update(Integer id, TaskDTO taskDTO) {
        Task task = new Task(
                taskDTO.userId(),
                taskDTO.title(),
                taskDTO.description()
        );

        return taskRepository.findById(id)
                .map(existingTask -> {
                    existingTask.setTitle(task.getTitle());
                    existingTask.setDescription(task.getDescription());
                    existingTask.setUserId(task.getUserId());
                    logger.debug("Task updated successfully: {}", id);
                    Task updatedTask = taskRepository.save(existingTask);
                    return convertToDTO(updatedTask);
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