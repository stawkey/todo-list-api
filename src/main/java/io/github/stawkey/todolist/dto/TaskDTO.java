package io.github.stawkey.todolist.dto;

import io.github.stawkey.todolist.entity.Task;

import java.time.LocalDateTime;

public record TaskDTO(Integer id, Integer userId, String title, String description, LocalDateTime creationDate) {
    public static TaskDTO convertToDTO(Task task) {
        return new TaskDTO(
                task.getId(),
                task.getUserId(),
                task.getTitle(),
                task.getDescription(),
                task.getCreationDate()
        );
    }
}