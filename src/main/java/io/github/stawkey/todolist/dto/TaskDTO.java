package io.github.stawkey.todolist.dto;

import io.github.stawkey.todolist.entity.Task;

public record TaskDTO(Integer id, Integer userId, String title, String description) {
    public static TaskDTO convertToDTO(Task task) {
        return new TaskDTO(
                task.getId(),
                task.getUserId(),
                task.getTitle(),
                task.getDescription()
        );
    }
}