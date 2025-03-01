package io.github.stawkey.todolist.service;

import io.github.stawkey.todolist.dto.TaskDTO;
import io.github.stawkey.todolist.entity.Task;
import io.github.stawkey.todolist.repository.TaskRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static io.github.stawkey.todolist.dto.TaskDTO.convertToDTO;

@Service
public class TaskService {

    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);
    private final TaskRepository taskRepository;
    private final UserService userService;

    TaskService(TaskRepository taskRepository, UserService userService) {
        this.taskRepository = taskRepository;
        this.userService = userService;
    }

    public Page<TaskDTO> getTasks(int page, int limit, String title, Sort.Direction sortDirection, String sortBy) {
        if (page < 0) {
            throw new IllegalArgumentException("Page number cannot be negative");
        }
        if (limit <= 0) {
            throw new IllegalArgumentException("Limit of tasks per page must be greater than zero");
        }

        Integer userId = userService.getCurrentUserId();
        if (userId == null) {
            throw new AccessDeniedException("User not authenticated");
        }

        Sort sort;
        if (sortBy != null && !sortBy.isEmpty()) {
            if (sortDirection == null) {
                sortDirection = Sort.Direction.ASC;
            }
            sort = Sort.by(sortDirection, sortBy);
        } else {
            sort = Sort.by(Sort.Direction.ASC, "id");
        }

        PageRequest pageRequest = PageRequest.of(page, limit, sort);

        if (title != null) {
            logger.debug("Fetching tasks containing {} for user {} page {} with limit {}", title, userId, page, limit);
            return taskRepository.findByTitleContainingIgnoreCase(title, pageRequest)
                    .map(TaskDTO::convertToDTO);
        }

        logger.debug("Fetching tasks for user {} page {} with limit {}", userId, page, limit);
        return taskRepository.findAllByUserId(userId, pageRequest).map(TaskDTO::convertToDTO);
    }

    public TaskDTO add(TaskDTO taskDTO) {
        Integer userId = userService.getCurrentUserId();

        Task task = new Task(
                userId,
                taskDTO.title(),
                taskDTO.description()
        );

        logger.debug("Adding new task for user {}: {}", userId, task.getTitle());
        Task savedTask = taskRepository.save(task);
        return convertToDTO(savedTask);
    }

    public TaskDTO update(Integer id, TaskDTO taskDTO) {
        Integer userId = userService.getCurrentUserId();

        return taskRepository.findByIdAndUserId(id, userId)
                .map(existingTask -> {
                    existingTask.setTitle(taskDTO.title());
                    existingTask.setDescription(taskDTO.description());
                    logger.debug("Task updated successfully: {}", id);
                    Task updatedTask = taskRepository.save(existingTask);
                    return convertToDTO(updatedTask);
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Task not found or not owned by current user"));
    }

    @Transactional
    public void delete(Integer id) {
        Integer userId = userService.getCurrentUserId();
        if (userId == null) {
            throw new AccessDeniedException("User not authenticated");
        }

        if (!taskRepository.existsByIdAndUserId(id, userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found or not owned by current user");
        }

        logger.debug("Deleting task with ID: {} for user: {}", id, userId);
        taskRepository.deleteByIdAndUserId(id, userId);
    }

}